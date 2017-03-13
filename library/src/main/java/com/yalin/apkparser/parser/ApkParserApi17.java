/*
 * Copyright 2017 YaLin Jin
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

package com.yalin.apkparser.parser;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;

import com.yalin.apkparser.compat.UserHandleCompat;
import com.yalin.apkparser.reflect.MethodUtil;

import java.lang.reflect.Method;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

@TargetApi(17)
class ApkParserApi17 extends ApkParserApi16 {
    protected Class<?> sPackageUserStateClass;

    protected Object mDefaultPackageUserState;

    ApkParserApi17(Context context) throws Exception {
        super(context);

        sPackageUserStateClass = Class.forName("android.content.pm.PackageUserState");
        mDefaultPackageUserState = sPackageUserStateClass.newInstance();
        mUserId = UserHandleCompat.getCallingUserId();
    }

    @Override
    public ActivityInfo generateActivityInfo(Object activity, int flags) throws Exception {
        //  public static final ActivityInfo generateActivityInfo(Activity a, int flags, PackageUserState state, int userId)
        Method method = MethodUtil.getAccessibleMethod(sPackageParserClass, "generateActivityInfo",
                sActivityClass, int.class, sPackageUserStateClass, int.class);
        return (ActivityInfo) method.invoke(null, activity, flags,
                mDefaultPackageUserState, mUserId);
    }
}
