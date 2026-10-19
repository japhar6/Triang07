package com.eni.devops;

/** Classification d'un triangle à partir de la longueur de ses trois côtés. */
public class Triangle {

    public enum Type { EQUILATERAL, ISOSCELES, SCALENE, INVALID }

    public static Type classify(int a, int b, int c) {
        if (a <= 0 || b <= 0 || c <= 0) {
            return Type.INVALID;
        }
        // inégalité triangulaire (long pour éviter tout débordement)
        if ((long) a + b <= c || (long) a + c <= b || (long) b + c <= a) {
            return Type.INVALID;
        }
        if (a == b && b == c) {
            return Type.EQUILATERAL;
        }
        if (a == b || b == c || a == c) {
            return Type.ISOSCELES;
        }
        return Type.SCALENE;
    }
}
