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

package com.yalin.apkparser.utils;

import android.content.ComponentName;
import android.text.TextUtils;

import java.util.Comparator;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

public class ComponentNameComparator implements Comparator<ComponentName> {

    @Override
    public int compare(ComponentName lhs, ComponentName rhs) {
        if (lhs == null && rhs == null) {
            return 0;
        } else if (lhs != null && rhs == null) {
            return 1;
        } else if (lhs == null) {
            return -1;
        } else {
            if (TextUtils.equals(lhs.getPackageName(), rhs.getPackageName()) && TextUtils.equals(lhs.getShortClassName(), rhs.getShortClassName())) {
                return 0;
            } else {
                return lhs.compareTo(rhs);
            }
        }
    }
}