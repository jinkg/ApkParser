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

package com.yalin.apkparser.compat;

import android.annotation.TargetApi;
import android.os.UserHandle;

import com.yalin.apkparser.reflect.MethodUtil;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

@TargetApi(17)
public class UserHandleCompat {
    //    UserHandle.getCallingUserId()
    public static int getCallingUserId() {
        try {
            return (int) MethodUtil.invokeStaticMethod(UserHandle.class, "getCallingUserId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
