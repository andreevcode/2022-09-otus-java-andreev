package ru.otus.homework.hw03;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.hw03.annotations.After;
import ru.otus.homework.hw03.annotations.Before;
import ru.otus.homework.hw03.annotations.Test;

import static ru.otus.reflection.ReflectionHelper.callMethod;
import static ru.otus.reflection.ReflectionHelper.instantiate;

public class TestAnnotationRunner {

    private final Logger logger = LoggerFactory.getLogger(TestAnnotationRunner.class);

    private static final String TEST_ANNOTATION_CLASS_NAME = Test.class.getName();
    private static final String BEFORE_ANNOTATION_CLASS_NAME = Before.class.getName();
    private static final String AFTER_ANNOTATION_CLASS_NAME = After.class.getName();

    private int goodEnded;

    private final String testClassName;

    private final List<Method> beforeMethods = new ArrayList<>();
    private final List<Method> afterMethods = new ArrayList<>();
    private final List<Method> testMethods = new ArrayList<>();

    public TestAnnotationRunner(String testClassName) {
        this.testClassName = testClassName;
    }

    public void runTests() {
        logger.info("-------Starting tests runner for {}", testClassName);

        Class<?> clazz;
        try {
            clazz = Class.forName(testClassName);
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

            Object testInstance = instantiate(clazz);

            for (Method testMethod : testMethods){
                goodEnded += runTest(testInstance, testMethod, beforeMethods, afterMethods);
            }

        } catch (ClassNotFoundException | RuntimeException e) {
            logger.error("Error while test class or instance initialisation: ", e);
        }

        printResult();
    }


    private int runTest(Object instance, Method testMethod, List<Method> beforeMethods, List<Method> afterMethods){
        int endedGood = 0;
        try {
            beforeMethods.forEach(method -> callMethod(instance, method.getName()));
            callMethod(instance, testMethod.getName());
            endedGood++;
        } catch (RuntimeException e) {
            logger.error("Error while setUp or run test", e);
        }

        try {
            afterMethods.forEach(method -> callMethod(instance, method.getName()));
        } catch (RuntimeException e){
            logger.error("Error while teardown", e);
        }
        return endedGood;

    }

    private void printResult() {
        int testsCount = testMethods.size();
        logger.info("\n--------------\nTESTS RESULTS:\n   TESTS: {}\n    GOOD: {}\n     BAD: {}\n", testsCount,
                goodEnded, testsCount - goodEnded);
    }

}