package ru.otus;

import com.google.common.base.Joiner;

public class HelloOtus {

    public static void main(String[] args) {
        Joiner joiner = Joiner.on(", ").skipNulls();
        System.out.println(joiner.join("Hello", null, "world!"));
    }
}
