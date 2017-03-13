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

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;

import com.yalin.apkparser.reflect.MethodUtil;

import java.lang.reflect.Method;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

class ApkParserApi16 extends ApkParserApi14 {
    private boolean mStopped;
    private int mEnabledState;

    protected int mUserId;

    ApkParserApi16(Context context) throws Exception {
        super(context);
        mStopped = false;
        mEnabledState = 0;
    }

    @Override
    public ActivityInfo generateActivityInfo(Object activity, int flags) throws Exception {
        // public static final ActivityInfo generateActivityInfo(Activity a, int flags, boolean stopped, int enabledState, int userId)
        Method method = MethodUtil.getAccessibleMethod(sPackageParserClass,
                "generateActivityInfo", sActivityClass, int.class,
                boolean.class, int.class, int.class);
        return (ActivityInfo) method.invoke(null, activity, flags,
                mStopped, mEnabledState, mUserId);
    }

    @Override
    public ServiceInfo generateServiceInfo(Object service, int flags) throws Exception {
        // public static final ServiceInfo generateServiceInfo(Service s, int flags, boolean stopped, int enabledState, int userId)*/
        Method method = MethodUtil.getAccessibleMethod(sPackageParserClass,
                "generateServiceInfo", sServiceClass, int.class, boolean.class,
                int.class, int.class);
        return (ServiceInfo) method.invoke(null, service, flags, mStopped, mEnabledState, mUserId);
    }
}
