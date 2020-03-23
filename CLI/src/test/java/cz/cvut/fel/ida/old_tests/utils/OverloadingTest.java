package cz.cvut.fel.ida.old_tests.utils;

import java.util.logging.Logger;

public class OverloadingTest {
    private static final Logger LOG = Logger.getLogger(OverloadingTest.class.getName());

    public static void main(String[] args) {
        A base = new Base();
        method(base);
    }

    static void method(A a){
        System.out.println("A");
    }

    static void method(B b){
        System.out.println("B");
    }

    private static class Base extends A implements B{}

    private static class A {}
    private static interface B{}
}
