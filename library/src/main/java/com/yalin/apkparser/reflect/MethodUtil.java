/*
 * Copyright 2017 YaLin Jin <nilaynij@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.yalin.apkparser.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

public class MethodUtil {
    private final static Map<String, Method> sMethodCache = new HashMap<>();

    private static String getKey(final Class<?> cls, final String methodName,
                                 final Class<?>... parameterTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append(cls.toString()).append("#").append(methodName);
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (Class<?> parameterType : parameterTypes) {
                sb.append(parameterType.toString()).append("#");
            }
        } else {
            sb.append(Void.class.toString());
        }
        return sb.toString();
    }

    public static <T> T invokeConstructor(final Class<T> cls, Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        args = Utils.nullToEmpty(args);
        final Class<?> parameterTypes[] = Utils.toClass(args);
        return invokeConstructor(cls, args, parameterTypes);
    }

    public static <T> T invokeConstructor(final Class<T> cls, Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        args = Utils.nullToEmpty(args);
        parameterTypes = Utils.nullToEmpty(parameterTypes);
        final Constructor<T> ctor = getMatchingAccessibleConstructor(cls, parameterTypes);
        if (ctor == null) {
            throw new NoSuchMethodException(
                    "No such accessible constructor on object: " + cls.getName());
        }
        return ctor.newInstance(args);
    }

    public static Object invokeStaticMethod(final Class clazz, final String methodName,
                                            Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        args = Utils.nullToEmpty(args);
        final Class<?>[] parameterTypes = Utils.toClass(args);
        return invokeStaticMethod(clazz, methodName, args, parameterTypes);
    }

    public static Object invokeStaticMethod(final Class clazz, final String methodName,
                                            Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        parameterTypes = Utils.nullToEmpty(parameterTypes);
        args = Utils.nullToEmpty(args);
        final Method method = getMatchingAccessibleMethod(clazz,
                methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + methodName + "() on object: "
                    + clazz.getName());
        }
        return method.invoke(null, args);
    }

    private static Method getMatchingAccessibleMethod(final Class<?> cls,
                                                      final String methodName, final Class<?>... parameterTypes) {

        String key = getKey(cls, methodName, parameterTypes);
        Method cachedMethod;
        synchronized (sMethodCache) {
            cachedMethod = sMethodCache.get(key);
        }
        if (cachedMethod != null) {
            if (!cachedMethod.isAccessible()) {
                cachedMethod.setAccessible(true);
            }
            return cachedMethod;
        }

        try {
            final Method method = cls.getMethod(methodName, parameterTypes);
            MemberUtil.setAccessibleWorkaround(method);
            synchronized (sMethodCache) {
                sMethodCache.put(key, method);
            }
            return method;
        } catch (final NoSuchMethodException e) { // NOPMD - Swallow the exception
        }
        // search through all methods
        Method bestMatch = null;
        final Method[] methods = cls.getMethods();
        for (final Method method : methods) {
            // compare name and parameters
            if (method.getName().equals(methodName)
                    && MemberUtil.isAssignable(parameterTypes, method.getParameterTypes(), true)) {
                // get accessible version of method
                final Method accessibleMethod = getAccessibleMethod(method);
                if (accessibleMethod != null
                        && (bestMatch == null || MemberUtil.compareParameterTypes(
                        accessibleMethod.getParameterTypes(),
                        bestMatch.getParameterTypes(),
                        parameterTypes) < 0)) {
                    bestMatch = accessibleMethod;
                }
            }
        }
        if (bestMatch != null) {
            MemberUtil.setAccessibleWorkaround(bestMatch);
        }
        synchronized (sMethodCache) {
            sMethodCache.put(key, bestMatch);
        }
        return bestMatch;
    }

    public static Object invokeMethod(final Object object, final String methodName,
                                      Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        args = Utils.nullToEmpty(args);
        final Class<?>[] parameterTypes = Utils.toClass(args);
        return invokeMethod(object, methodName, args, parameterTypes);
    }

    public static Object invokeMethod(final Object object, final String methodName,
                                      Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        parameterTypes = Utils.nullToEmpty(parameterTypes);
        args = Utils.nullToEmpty(args);
        final Method method = getMatchingAccessibleMethod(object.getClass(),
                methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + methodName + "() on object: "
                    + object.getClass().getName());
        }
        return method.invoke(object, args);
    }

    public static Method getAccessibleMethod(final Class<?> cls, final String methodName,
                                             final Class<?>... parameterTypes) throws NoSuchMethodException {
        String key = getKey(cls, methodName, parameterTypes);
        Method method;
        synchronized (sMethodCache) {
            method = sMethodCache.get(key);
        }
        if (method != null) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method;
        }

        Method accessibleMethod = getAccessibleMethod(cls.getMethod(methodName,
                parameterTypes));
        synchronized (sMethodCache) {
            sMethodCache.put(key, accessibleMethod);
        }
        return accessibleMethod;

    }

    private static Method getAccessibleMethod(Method method) {
        if (!MemberUtil.isAccessible(method)) {
            return null;
        }
        // If the declaring class is public, we are done
        final Class<?> cls = method.getDeclaringClass();
        if (Modifier.isPublic(cls.getModifiers())) {
            return method;
        }
        final String methodName = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();

        // Check the implemented interfaces and sub interfaces
        method = getAccessibleMethodFromInterfaceNest(cls, methodName,
                parameterTypes);

        // Check the superclass chain
        if (method == null) {
            method = getAccessibleMethodFromSuperclass(cls, methodName,
                    parameterTypes);
        }
        return method;
    }

    private static Method getAccessibleMethodFromInterfaceNest(
            Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        // Search up the superclass chain
        for (; cls != null; cls = cls.getSuperclass()) {
            // Check the implemented interfaces of the parent class
            final Class<?>[] interfaces = cls.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                // Is this interface public?
                if (!Modifier.isPublic(anInterface.getModifiers())) {
                    continue;
                }
                // Does the method exist on this interface?
                try {
                    return anInterface.getDeclaredMethod(methodName,
                            parameterTypes);
                } catch (final NoSuchMethodException e) { // NOPMD
                    /*
                     * Swallow, if no method is found after the loop then this
                     * method returns null.
                     */
                }
                // Recursively check our parent interfaces
                Method method = getAccessibleMethodFromInterfaceNest(anInterface,
                        methodName, parameterTypes);
                if (method != null) {
                    return method;
                }
            }
        }
        return null;
    }

    private static Method getAccessibleMethodFromSuperclass(
            final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        Class<?> parentClass = cls.getSuperclass();
        while (parentClass != null) {
            if (Modifier.isPublic(parentClass.getModifiers())) {
                try {
                    return parentClass.getMethod(methodName, parameterTypes);
                } catch (final NoSuchMethodException e) {
                    return null;
                }
            }
            parentClass = parentClass.getSuperclass();
        }
        return null;
    }

    public static <T> Constructor<T> getMatchingAccessibleConstructor(final Class<T> cls,
                                                                      final Class<?>... parameterTypes) {
        Assertions.checkArgument(cls != null, "class cannot be null");
        // see if we can find the constructor directly
        // most of the time this works and it's much faster
        try {
            final Constructor<T> ctor = cls.getConstructor(parameterTypes);
            MemberUtil.setAccessibleWorkaround(ctor);
            return ctor;
        } catch (final NoSuchMethodException e) { // NOPMD - Swallow
        }
        Constructor<T> result = null;
        /*
         * (1) Class.getConstructors() is documented to return Constructor<T> so as
         * long as the array is not subsequently modified, everything's fine.
         */
        final Constructor<?>[] ctors = cls.getConstructors();

        // return best match:
        for (Constructor<?> ctor : ctors) {
            // compare parameters
            if (MemberUtil.isAssignable(parameterTypes, ctor.getParameterTypes(), true)) {
                // get accessible version of constructor
                ctor = getAccessibleConstructor(ctor);
                if (ctor != null) {
                    MemberUtil.setAccessibleWorkaround(ctor);
                    if (result == null
                            || MemberUtil.compareParameterTypes(ctor.getParameterTypes(), result
                            .getParameterTypes(), parameterTypes) < 0) {
                        // temporary variable for annotation, see comment above (1)
                        @SuppressWarnings("unchecked")
                        final
                        Constructor<T> constructor = (Constructor<T>) ctor;
                        result = constructor;
                    }
                }
            }
        }
        return result;
    }

    private static <T> Constructor<T> getAccessibleConstructor(final Constructor<T> ctor) {
        Assertions.checkArgument(ctor != null, "constructor cannot be null");
        return MemberUtil.isAccessible(ctor)
                && isAccessible(ctor.getDeclaringClass()) ? ctor : null;
    }

    private static boolean isAccessible(final Class<?> type) {
        Class<?> cls = type;
        while (cls != null) {
            if (!Modifier.isPublic(cls.getModifiers())) {
                return false;
            }
            cls = cls.getEnclosingClass();
        }
        return true;
    }
}
