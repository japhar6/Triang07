package com.eni.devops;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TriangleTest {

    @Test
    public void equilateral() {
        assertEquals(Triangle.Type.EQUILATERAL, Triangle.classify(3, 3, 3));
    }

    @Test
    public void isosceles() {
        assertEquals(Triangle.Type.ISOSCELES, Triangle.classify(3, 3, 5));
        assertEquals(Triangle.Type.ISOSCELES, Triangle.classify(5, 3, 3));
        assertEquals(Triangle.Type.ISOSCELES, Triangle.classify(3, 5, 3));
    }

    @Test
    public void scalene() {
        assertEquals(Triangle.Type.SCALENE, Triangle.classify(3, 4, 5));
    }

    @Test
    public void invalidSides() {
        assertEquals(Triangle.Type.INVALID, Triangle.classify(0, 4, 5));
        assertEquals(Triangle.Type.INVALID, Triangle.classify(-1, 4, 5));
    }

    @Test
    public void invalidTriangleInequality() {
        assertEquals(Triangle.Type.INVALID, Triangle.classify(1, 2, 3));
        assertEquals(Triangle.Type.INVALID, Triangle.classify(1, 10, 3));
    }
}
