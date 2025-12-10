package uj.wmii.pwj.anns;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyTestEngine {

    private record TestResult(TestStatus status, String reason) {}

    private class InvalidAnswerException extends Exception {
        private String message;

        public InvalidAnswerException(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    private class InconsistentParametersException extends Exception {}

    private class EmptyOutputException extends Exception {}

    private final String className;

    public static void main(String[] args) {
        /*
         * if (args.length < 1) {
         * System.out.println("Please specify test class name");
         * System.exit(-1);
         * }
         * String className = args[0].trim();
         */
        String className = "uj.wmii.pwj.anns.MyBeautifulTestSuite";
        System.out.printf("Testing class: %s\n", className);
        MyTestEngine engine = new MyTestEngine(className);
        engine.runTests();
    }

    public MyTestEngine(String className) {
        this.className = className;
    }

    public void runTests() {
        System.out.println(
                """
                        ====================================
                        ▗▄▄▄▖▗▄▄▄▖ ▗▄▄▖▗▄▄▄▖▗▄▄▄▖▗▖  ▗▖ ▗▄▄▖
                          █  ▐▌   ▐▌     █    █  ▐▛▚▖▐▌▐▌
                          █  ▐▛▀▀▘ ▝▀▚▖  █    █  ▐▌ ▝▜▌▐▌▝▜▌
                          █  ▐▙▄▄▖▗▄▄▞▘  █  ▗▄█▄▖▐▌  ▐▌▝▚▄▞▘


                        ▗▄▄▄▖▗▖  ▗▖ ▗▄▄▖▗▄▄▄▖▗▖  ▗▖▗▄▄▄▖
                        ▐▌   ▐▛▚▖▐▌▐▌     █  ▐▛▚▖▐▌▐▌
                        ▐▛▀▀▘▐▌ ▝▜▌▐▌▝▜▌  █  ▐▌ ▝▜▌▐▛▀▀▘    v3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679
                        ▐▙▄▄▖▐▌  ▐▌▝▚▄▞▘▗▄█▄▖▐▌  ▐▌▐▙▄▄▖
                        ====================================
                        """);

        final Object unit = getObject(className);
        List<Method> testMethods = getTestMethods(unit);
        int successCount = 0;
        int failCount = 0;
        int errorCount = 0;
        int index = 0;

        System.out.printf("[===] Running %s tests\n", testMethods.size());

        for (Method m : testMethods) {
            TestResult result = launchSingleMethod(m, unit);

            index++;
            if (result.status == TestStatus.PASS) {
                successCount++;
                System.out.printf("[ %d] %s\n    PASS\n", index, m.getName());
            } else if (result.status == TestStatus.FAIL) {
                failCount++;
                System.out.printf("[*%d] %s\n    FAIL: %s\n", index, m.getName(), result.reason);
            } else {
                errorCount++;
                System.out.printf("[*%d] %s\n    ERROR: %s\n", index, m.getName(), result.reason);
            }
        }

        System.out.printf("[===   Tests completed: %d\n", successCount + failCount + errorCount);
        System.out.printf("[      PASS:            %d\n", successCount);
        System.out.printf("[      FAIL:            %d\n", failCount);
        System.out.printf("[      ERROR:           %d\n", errorCount);
    }

    private TestResult launchSingleMethod(Method m, Object unit) {
        try {
            if (m.isAnnotationPresent(Verify.class)) {
                String[] input = m.getAnnotation(Verify.class).input();
                if (input.length == 0) {
                    m.invoke(unit);
                } else {
                    for (String arg : input) {
                        m.invoke(unit, arg);
                    }
                }
            }
            else if (m.isAnnotationPresent(VerifyLong.class)) {
                String[] input = m.getAnnotation(VerifyLong.class).input();
                long[] output = m.getAnnotation(VerifyLong.class).output();

                if (input.length != output.length) {
                    throw new InconsistentParametersException();
                }

                if (input.length == 0) {
                    throw new EmptyOutputException();
                } else {
                    for (int i = 0; i < input.length; i++) {
                        Long expected = Long.valueOf(output[i]);
                        Object answer = m.invoke(unit, input[i]);
                        if (!expected.equals(answer)) {
                            throw new InvalidAnswerException("expected " + expected + ", got " + answer);
                        }
                    }
                }
            }
            else if (m.isAnnotationPresent(VerifyDouble.class)) {
                String[] input = m.getAnnotation(VerifyDouble.class).input();
                double[] output = m.getAnnotation(VerifyDouble.class).output();

                if (input.length != output.length) {
                    throw new InconsistentParametersException();
                }

                if (input.length == 0) {
                    throw new EmptyOutputException();
                } else {
                    for (int i = 0; i < input.length; i++) {
                        Double expected = Double.valueOf(output[i]);
                        Object answer = m.invoke(unit, input[i]);
                        if (!expected.equals(answer)) {
                            throw new InvalidAnswerException("expected " + expected + ", got " + answer);
                        }
                    }
                }
            }
            else if (m.isAnnotationPresent(VerifyString.class)) {
                String[] input = m.getAnnotation(VerifyString.class).input();
                String[] output = m.getAnnotation(VerifyString.class).output();

                if (input.length != output.length) {
                    throw new InconsistentParametersException();
                }

                if (input.length == 0) {
                    throw new EmptyOutputException();
                } else {
                    for (int i = 0; i < input.length; i++) {
                        String expected = output[i];
                        Object answer = m.invoke(unit, input[i]);
                        if (!expected.equals(answer)) {
                            throw new InvalidAnswerException("expected " + expected + ", got " + answer);
                        }
                    }
                }
            }
            else {
                throw new Exception("?");
            }
        }
        catch (InvalidAnswerException e) {
            return new TestResult(TestStatus.FAIL, "invalid answer: " + e.getMessage());
        }
        catch (InconsistentParametersException e) {
            return new TestResult(TestStatus.ERROR, "output length does not match input");
        }
        catch (EmptyOutputException e) {
            return new TestResult(TestStatus.ERROR, "empty output; use @Verify");
        }
        catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
            return new TestResult(TestStatus.ERROR, "unhandled exception: " + e.getCause().getClass());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new TestResult(TestStatus.ERROR, "unhandled exception: " + e.getClass());
        }

        return new TestResult(TestStatus.PASS, "");  
    }

    private static List<Method> getTestMethods(Object unit) {
        Method[] methods = unit.getClass().getDeclaredMethods();
        return Arrays.stream(methods).filter(
                m -> 
                    m.getAnnotation(Verify.class) != null || 
                    m.getAnnotation(VerifyLong.class) != null ||
                    m.getAnnotation(VerifyDouble.class) != null ||
                    m.getAnnotation(VerifyString.class) != null
            ).collect(Collectors.toList());
    }

    private static Object getObject(String className) {
        try {
            Class<?> unitClass = Class.forName(className);
            return unitClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return new Object();
        }
    }
}
