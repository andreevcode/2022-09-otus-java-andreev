package ru.otus.homework.hw03;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.otus.reflection.ReflectionHelper.callMethod;
import static ru.otus.reflection.ReflectionHelper.instantiate;

public class TestAnnotationUtils {

    private TestAnnotationUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(TestAnnotationUtils.class);

    static final String TEST_ANNOTATION_CLASS_NAME = Test.class.getName();
    static final String BEFORE_ANNOTATION_CLASS_NAME = Before.class.getName();
    static final String AFTER_ANNOTATION_CLASS_NAME = After.class.getName();

    static int goodEnded;

    static List<Method> beforeMethods = new ArrayList<>();
    static List<Method> afterMethods = new ArrayList<>();
    static List<Method> testMethods = new ArrayList<>();

    public static void runTests(String className) throws ClassNotFoundException {
        logger.info("-------Starting tests runner");
        Class<?> clazz = Class.forName(className);

        for (Method method : clazz.getDeclaredMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                String name = annotation.annotationType().getName();
                if (name.equals(BEFORE_ANNOTATION_CLASS_NAME)) {
                    beforeMethods.add(method);
                } else if (name.equals(AFTER_ANNOTATION_CLASS_NAME)) {
                    afterMethods.add(method);
                } else if (name.equals(TEST_ANNOTATION_CLASS_NAME)) {
                    testMethods.add(method);
                }
            }
        }

        logger.info("@Before methods: {}", beforeMethods.stream().map(Method::getName).toList());
        logger.info("@After methods: {}", afterMethods.stream().map(Method::getName).toList());
        logger.info("@Test methods: {}\n", testMethods.stream().map(Method::getName).toList());

        for (Method testMethod : testMethods) {
            Object testInstance = instantiate(clazz);
            invokeMethods(testInstance, beforeMethods);
            goodEnded += invokeMethods(testInstance, Collections.singletonList(testMethod));
            invokeMethods(testInstance, afterMethods);
        }

        printResult();
    }


    private static int invokeMethods(Object instance, List<Method> methods){
        int endedGood = 0;
        for (Method method : methods) {
            try {
                callMethod(instance, method.getName());
                endedGood++;
            }
            catch (RuntimeException e){
                var cause = (InvocationTargetException)e.getCause();
                logger.error("  {}", cause.getTargetException().toString().replaceAll("[\\t\\n\\r]+", " "));
            }
        }
        return endedGood;
    }

    private static void printResult() {
        int testsCount = testMethods.size();
        logger.info("\n--------------\nTESTS RESULTS:\n   TESTS: {}\n    GOOD: {}\n     BAD: {}\n", testsCount,
                goodEnded, testsCount - goodEnded);
    }

}