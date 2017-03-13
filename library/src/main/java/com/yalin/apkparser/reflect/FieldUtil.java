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

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

public class FieldUtil {
    private final static Map<String, Field> sFieldCache = new HashMap<>();

    private static String getKey(Class<?> cls, String fieldName) {
        return cls.toString() + "#" + fieldName;
    }

    public static Object readField(final Object target, final String fieldName)
            throws IllegalAccessException {
        Assertions.checkArgument(target != null, "target object must not be null");
        final Class<?> cls = target.getClass();
        final Field field = getField(cls, fieldName, true);
        Assertions.checkArgument(field != null, "Cannot locate field %s on %s", fieldName, cls);
        // already forced access above, don't repeat it here:
        return readField(field, target, false);
    }

    public static Object readField(final Field field, final Object target,
                                   final boolean forceAccess) throws IllegalAccessException {
        Assertions.checkArgument(field != null, "The field must not be null");
        if (forceAccess && !field.isAccessible()) {
            field.setAccessible(true);
        } else {
            MemberUtil.setAccessibleWorkaround(field);
        }
        return field.get(target);
    }

    private static Field getField(Class<?> cls, String fieldName, final boolean forceAccess) {
        Assertions.checkArgument(cls != null, "The class must not be null");
        Assertions.checkArgument(!TextUtils.isEmpty(fieldName), "The field name must not be blank/empty");

        String key = getKey(cls, fieldName);
        Field cachedField;
        synchronized (sFieldCache) {
            cachedField = sFieldCache.get(key);
        }
        if (cachedField != null) {
            if (forceAccess && !cachedField.isAccessible()) {
                cachedField.setAccessible(true);
            }
            return cachedField;
        }

        // check up the superclass hierarchy
        for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
            try {
                final Field field = acls.getDeclaredField(fieldName);
                // getDeclaredField checks for non-public scopes as well
                // and it returns accurate results
                if (!Modifier.isPublic(field.getModifiers())) {
                    if (forceAccess) {
                        field.setAccessible(true);
                    } else {
                        continue;
                    }
                }
                synchronized (sFieldCache) {
                    sFieldCache.put(key, field);
                }
                return field;
            } catch (final NoSuchFieldException ex) { // NOPMD
                // ignore
            }
        }
        // check the public interface case. This must be manually searched for
        // incase there is a public supersuperclass field hidden by a private/package
        // superclass field.
        Field match = null;
        List<Class<?>> classes = Utils.getAllInterfaces(cls);
        if (classes == null) {
            return null;
        }
        for (final Class<?> class1 : classes) {
            try {
                final Field test = class1.getField(fieldName);
                Assertions.checkArgument(match == null, "Reference to field %s is ambiguous relative to %s"
                        + "; a matching field exists on two or more implemented interfaces.", fieldName, cls);
                match = test;
            } catch (final NoSuchFieldException ex) { // NOPMD
                // ignore
            }
        }
        synchronized (sFieldCache) {
            sFieldCache.put(key, match);
        }
        return match;
    }
}
