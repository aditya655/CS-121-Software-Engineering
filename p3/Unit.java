import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;

public class Unit {

    public static Map<String, Throwable> testClass(String name) {
        Map<String, Throwable> result = new HashMap<>();
        try {
            Class<?> clazz = Class.forName(name);
            Object instance = clazz.getDeclaredConstructor().newInstance();

            Method[] methods = clazz.getDeclaredMethods();
            List<Method> beforeClassMethods = new ArrayList<>();
            List<Method> beforeMethods = new ArrayList<>();
            List<Method> testMethods = new ArrayList<>();
            List<Method> afterMethods = new ArrayList<>();
            List<Method> afterClassMethods = new ArrayList<>();

            for (Method method : methods) {
                if (method.isAnnotationPresent(BeforeClass.class)) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalArgumentException("@BeforeClass methods must be static");
                    }
                    beforeClassMethods.add(method);
                } else if (method.isAnnotationPresent(Before.class)) {
                    beforeMethods.add(method);
                } else if (method.isAnnotationPresent(Test.class)) {
                    testMethods.add(method);
                } else if (method.isAnnotationPresent(After.class)) {
                    afterMethods.add(method);
                } else if (method.isAnnotationPresent(AfterClass.class)) {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalArgumentException("@AfterClass methods must be static");
                    }
                    afterClassMethods.add(method);
                }
            }

            beforeClassMethods.sort(Comparator.comparing(Method::getName));
            beforeMethods.sort(Comparator.comparing(Method::getName));
            testMethods.sort(Comparator.comparing(Method::getName));
            afterMethods.sort(Comparator.comparing(Method::getName));
            afterClassMethods.sort(Comparator.comparing(Method::getName));

            for (Method method : beforeClassMethods) {
                method.invoke(null);
            }

            for (Method testMethod : testMethods) {
                for (Method beforeMethod : beforeMethods) {
                    beforeMethod.invoke(instance);
                }
                try {
                    testMethod.invoke(instance);
                    result.put(testMethod.getName(), null);
                } catch (InvocationTargetException e) {
                    result.put(testMethod.getName(), e.getCause());
                }
                for (Method afterMethod : afterMethods) {
                    afterMethod.invoke(instance);
                }
            }

            for (Method method : afterClassMethods) {
                method.invoke(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object[]> quickCheckClass(String name) {
        Map<String, Object[]> result = new HashMap<>();
        try {
            Class<?> clazz = Class.forName(name);
            Object instance = clazz.getDeclaredConstructor().newInstance();

            Method[] methods = clazz.getDeclaredMethods();
            List<Method> propertyMethods = new ArrayList<>();

            for (Method method : methods) {
                if (method.isAnnotationPresent(Property.class)) {
                    propertyMethods.add(method);
                }
            }

            propertyMethods.sort(Comparator.comparing(Method::getName));

            for (Method propertyMethod : propertyMethods) {
                Parameter[] parameters = propertyMethod.getParameters();
                List<Object[]> argsList = new ArrayList<>();
                generateArgs(parameters, argsList, new Object[parameters.length], 0);

                boolean passed = true;
                int runCount = 0;
                for (Object[] args : argsList) {
                    try {
                        runCount++;
                        boolean resultBoolean = (boolean) propertyMethod.invoke(instance, args);
                        if (!resultBoolean) {
                            result.put(propertyMethod.getName(), args);
                            passed = false;
                            break;
                        }
                    } catch (Throwable t) {
                        result.put(propertyMethod.getName(), args);
                        passed = false;
                        break;
                    }
                    if (runCount >= 100) {
                        break;
                    }
                }
                if (passed) {
                    result.put(propertyMethod.getName(), null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void generateArgs(Parameter[] parameters, List<Object[]> argsList, Object[] currentArgs, int index) throws Exception {
        if (index == parameters.length) {
            argsList.add(currentArgs.clone());
            return;
        }

        Parameter parameter = parameters[index];
        if (parameter.isAnnotationPresent(IntRange.class)) {
            IntRange range = parameter.getAnnotation(IntRange.class);
            for (int i = range.min(); i <= range.max(); i++) {
                currentArgs[index] = i;
                generateArgs(parameters, argsList, currentArgs, index + 1);
            }
        } else if (parameter.isAnnotationPresent(StringSet.class)) {
            StringSet stringSet = parameter.getAnnotation(StringSet.class);
            for (String s : stringSet.strings()) {
                currentArgs[index] = s;
                generateArgs(parameters, argsList, currentArgs, index + 1);
            }
        } else if (parameter.isAnnotationPresent(ListLength.class)) {
            ListLength listLength = parameter.getAnnotation(ListLength.class);
            generateListArgs(parameter, argsList, currentArgs, index, listLength.min(), listLength.max());
        } else if (parameter.isAnnotationPresent(ForAll.class)) {
            ForAll forAll = parameter.getAnnotation(ForAll.class);
            Method generatorMethod = parameter.getDeclaringExecutable().getDeclaringClass().getMethod(forAll.name());
            List<Object> generatedValues = new ArrayList<>();
            for (int i = 0; i < forAll.times(); i++) {
                generatedValues.add(generatorMethod.invoke(generatorMethod.getDeclaringClass().getDeclaredConstructor().newInstance()));
            }
            for (Object value : generatedValues) {
                currentArgs[index] = value;
                generateArgs(parameters, argsList, currentArgs, index + 1);
            }
        } else {
            throw new IllegalArgumentException("Unsupported parameter annotation");
        }
    }

    private static void generateListArgs(Parameter parameter, List<Object[]> argsList, Object[] currentArgs, int index, int min, int max) throws Exception {
        ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
        Type elementType = parameterizedType.getActualTypeArguments()[0];
        for (int length = min; length <= max; length++) {
            List<Object> list = new ArrayList<>();
            generateListElementArgs(elementType, list, length);
            currentArgs[index] = list;
            generateArgs(parameter.getDeclaringExecutable().getParameters(), argsList, currentArgs, index + 1);
        }
    }

    private static void generateListElementArgs(Type type, List<Object> list, int length) throws Exception {
        if (list.size() == length) {
            return;
        }

        if (type instanceof Class && ((Class<?>) type).isAnnotationPresent(IntRange.class)) {
            IntRange range = ((Class<?>) type).getAnnotation(IntRange.class);
            for (int i = range.min(); i <= range.max(); i++) {
                list.add(i);
                generateListElementArgs(type, list, length);
                list.remove(list.size() - 1);
            }
        } else if (type instanceof Class && ((Class<?>) type).isAnnotationPresent(StringSet.class)) {
            StringSet stringSet = ((Class<?>) type).getAnnotation(StringSet.class);
            for (String s : stringSet.strings()) {
                list.add(s);
                generateListElementArgs(type, list, length);
                list.remove(list.size() - 1);
            }
        } else {
            throw new IllegalArgumentException("Unsupported list element type");
        }
    }
}
