# TP2 – Jenkins pipelines : Intégration et Livraison Continues

Guide pas à pas pour réaliser le TP avec le projet **Triang07** (classification de triangles, Maven + JUnit + Docker).

## Contenu du dépôt

| Fichier | Rôle |
|---|---|
| `pom.xml` | Build Maven (Java 8, JUnit 4) |
| `src/main/java/.../Triangle.java`, `Main.java` | Application |
| `src/test/java/.../TriangleTest.java` | Tests unitaires |
| `Dockerfile` | Image Docker de l'application |
| `Jenkinsfile` | Pipeline complet (partie B) |

Test en local avant tout :

```bash
mvn clean install
java -jar target/triang07-1.0.0.jar 3 4 5      # -> SCALENE
docker build -t VOTRE_USER/triang7:1.0.0 .
docker run --rm VOTRE_USER/triang7:1.0.0 3 3 3  # -> EQUILATERAL
```

## 0. Pré-requis Jenkins

- Jenkins a accès à Internet.
- Plugins : Pipeline, Git, Maven Integration, Credentials Binding, Email Extension, GitHub.
- Docker installé sur la machine Jenkins, et l'utilisateur `jenkins` dans le groupe `docker` :
  `sudo usermod -aG docker jenkins && sudo systemctl restart jenkins`
- Maven et JDK 8 disponibles (ou configurés via Global Tool Configuration).

## 1. Pousser le projet sur GitHub

```bash
git init
git add .
git commit -m "v1"
git remote add origin https://github.com/VOTRE_COMPTE/Triang07.git
git push -u origin master
```

## 2. Credentials dans Jenkins (partie A-a)

*Manage Jenkins > Credentials > System > Global credentials > Add Credentials*

1. **GitHub** : type *Username with password*, ID `git_credentials`.
   (Avec GitHub, utilisez un **Personal Access Token** à la place du mot de passe.)
2. **DockerHub** : type *Secret text*, ID `dockerhubpass`, valeur = mot de passe ou token DockerHub.

## 3. Global Tool Configuration (partie A-b)

*Manage Jenkins > Tools* : ajouter **Maven** (nom : `Maven_nom_sur_Jenkins`, ou adaptez le Jenkinsfile) et **Git**.

## 4. Premier pipeline de test

*Nouveau Item > Pipeline* (nom `First-pipeline`), script :

```groovy
pipeline {
    agent any
    stages {
        stage('Premier Stage') { steps { echo 'Un premier Bonjour !' } }
        stage('Deuxième Stage') {
            steps {
                echo 'Un deuxième Bonjour !'
                echo 'Au revoir!'
            }
        }
    }
}
```

Sauvegardez, lancez un build, observez le *Stage View*.

## 5. Pipeline pour le projet Maven (`pipeline-triangle`)

Créez un item Pipeline nommé `pipeline-triangle` et collez le script ci-dessous, étape par étape
(le Jenkinsfile fourni contient la version finale).

1. **Checkout** : `git credentialsId: 'git_credentials', url: 'https://github.com/VOTRE_COMPTE/Triang07.git'`
2. **Build** : `sh 'mvn clean install'` (+ bloc `tools { maven 'Maven_nom_sur_Jenkins' }`)
3. **Tests unitaires** : `sh 'mvn test'`
4. **Image Docker** : `sh "docker build --tag VOTRE_USER/triang7:1.0.0 ."`
5. **Push DockerHub** : `withCredentials([string(credentialsId: 'dockerhubpass', variable: 'dockerHubPass')]) { ... docker login ... docker push ... }`

> Remarque : le sujet utilise `docker login -u ... -p $mdp`, ce qui expose le mot de passe (Docker affiche un avertissement).
> Le Jenkinsfile fourni utilise `--password-stdin`, plus sûr.

## 6. Partie B – Jenkinsfile

1. Le `Jenkinsfile` est déjà à la racine du dépôt. Adaptez l'URL GitHub, `DOCKER_USER` et l'email.
2. `git add Jenkinsfile && git commit -m "Jenkinsfile" && git push`
3. Dans Jenkins : *Configure* du job > Pipeline > Definition = **Pipeline script from SCM** > SCM = **Git** > URL du dépôt, credentials `git_credentials`, branche `*/master`, Script Path = `Jenkinsfile`.
4. Sauvegardez et lancez le build.

## 7. Partie C – Webhook GitHub + ngrok

1. Job > *Build Triggers* > cocher **GitHub hook trigger for GITScm polling**.
2. Rendre Jenkins public avec ngrok :
   ```bash
   unzip /chemin/vers/ngrok.zip
   ./ngrok config add-authtoken VOTRE_TOKEN_NGROK   # depuis dashboard.ngrok.com (compte gratuit)
   ./ngrok http 8080
   ```
   Relevez l'URL `https://xxxx.ngrok-free.app`.
3. GitHub > dépôt > *Settings > Webhooks > Add webhook* :
   - Payload URL : `https://xxxx.ngrok-free.app/github-webhook/` (le `/` final est obligatoire)
   - Content type : `application/json`
   - Event : *Just the push event*
4. Modifiez un fichier, `git commit` + `git push` : un build doit démarrer tout seul.

## 8. Partie D – Notification email en cas d'échec

1. *Manage Jenkins > System > Extended E-mail Notification* :
   - SMTP server : `smtp.gmail.com`, port `465`, cocher **Use SSL**
   - Credentials : compte Gmail + **mot de passe d'application** (validation en 2 étapes requise).
2. Le bloc `post { failure { emailext ... } }` est déjà dans le Jenkinsfile : remplacez l'adresse `to:`.
3. Test : cassez volontairement un test dans `TriangleTest.java`, poussez, et vérifiez la réception du mail.

## Dépannage rapide

| Symptôme | Cause probable |
|---|---|
| `mvn: command not found` | Bloc `tools` absent ou nom différent de celui de Global Tool Configuration |
| `permission denied ... docker.sock` | `jenkins` pas dans le groupe `docker` |
| Pas de build au push | URL webhook sans `/github-webhook/`, ou URL ngrok changée (elle change à chaque relance) |
| Échec `git checkout` | Mauvais ID de credentials ou token GitHub expiré |
