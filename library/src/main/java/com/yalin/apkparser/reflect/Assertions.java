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

import android.os.Looper;
import android.text.TextUtils;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

public final class Assertions {
    private static final boolean ASSERTIONS_ENABLE = true;

    private Assertions() {

    }

    public static void checkArgument(boolean expression) {
        if (ASSERTIONS_ENABLE && !expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, Object errorMessage) {
        if (ASSERTIONS_ENABLE && !expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(final boolean expression, final String message,
                              final Object... values) {
        if (ASSERTIONS_ENABLE && !expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static int checkIndex(int index, int start, int limit) {
        if (index < start || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        return index;
    }

    public static void checkState(boolean expression) {
        if (ASSERTIONS_ENABLE && !expression) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean expression, Object errorMessage) {
        if (ASSERTIONS_ENABLE && !expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    public static <T> T checkNotNull(T reference) {
        if (ASSERTIONS_ENABLE && reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (ASSERTIONS_ENABLE && reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static String checkNotEmpty(String string) {
        if (ASSERTIONS_ENABLE && TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException();
        }
        return string;
    }

    public static String checkNotEmpty(String string, Object errorMessage) {
        if (ASSERTIONS_ENABLE && TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return string;
    }

    public static void checkMainThread() {
        if (ASSERTIONS_ENABLE && Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("Not in applications main thread");
        }
    }
}
