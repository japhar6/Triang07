package com.eni.devops;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage : java -jar triang07.jar <a> <b> <c>");
            return;
        }
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        int c = Integer.parseInt(args[2]);
        System.out.println("Triangle (" + a + ", " + b + ", " + c + ") : " + Triangle.classify(a, b, c));
    }
}
