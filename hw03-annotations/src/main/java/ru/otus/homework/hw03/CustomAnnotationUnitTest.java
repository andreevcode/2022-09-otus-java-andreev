package ru.otus.homework.hw03;

public class CustomAnnotationUnitTest {

    public static void main(String[] args) {
        new TestAnnotationRunner("ru.otus.homework.hw03.AnnotationsTest").runTests();
        new TestAnnotationRunner("ru.otus.homework.hw03.AnnotationsTestNotExisted").runTests();
    }
}
