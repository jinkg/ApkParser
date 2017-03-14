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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

import com.yalin.apkparser.compat.UserHandleCompat;
import com.yalin.apkparser.reflect.MethodUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

    @Override
    public ServiceInfo generateServiceInfo(Object service, int flags) throws Exception {
        // public static final ServiceInfo generateServiceInfo(Service s, int flags, PackageUserState state, int userId)
        Method method = MethodUtil.getAccessibleMethod(sPackageParserClass,
                "generateServiceInfo", sServiceClass, int.class,
                sPackageUserStateClass, int.class);
        return (ServiceInfo) method.invoke(null, service, flags,
                mDefaultPackageUserState, mUserId);
    }

    @Override
    public ProviderInfo generateProviderInfo(Object provider, int flags) throws Exception {
        // public static final ProviderInfo generateProviderInfo(Provider p, int flags, PackageUserState state, int userId)
        Method method = MethodUtil.getAccessibleMethod(sPackageParserClass,
                "generateProviderInfo", sProviderClass, int.class,
                sPackageUserStateClass, int.class);
        return (ProviderInfo) method.invoke(null, provider, flags,
                mDefaultPackageUserState, mUserId);
    }

    @Override
    public ApplicationInfo generateApplicationInfo(int flags) throws Exception {
        // public static ApplicationInfo generateApplicationInfo(Package p, int flags, PackageUserState state, int userId)
        Method method = MethodUtil.getAccessibleMethod(sPackageParserClass,
                "generateApplicationInfo", mPackage.getClass(), int.class,
                sPackageUserStateClass, int.class);
        return (ApplicationInfo) method.invoke(null, mPackage, flags,
                mDefaultPackageUserState, mUserId);
    }

    @Override
    public PackageInfo generatePackageInfo(int[] gids, int flags, long firstInstallTime, long lastUpdateTime, HashSet<String> grantedPermissions) throws Exception {
        // public static PackageInfo generatePackageInfo(PackageParser.Package p,
        // int gids[], int flags, long firstInstallTime, long lastUpdateTime,
        // HashSet<String> grantedPermissions, PackageUserState state, int userId)
        try {
            Method method = MethodUtil.getAccessibleMethod(sPackageParserClass,
                    "generatePackageInfo", mPackage.getClass(), int[].class,
                    int.class, long.class, long.class, Set.class,
                    sPackageUserStateClass, int.class);
            return (PackageInfo) method.invoke(null, mPackage, gids, flags,
                    firstInstallTime, lastUpdateTime, grantedPermissions,
                    mDefaultPackageUserState, mUserId);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            Method method = MethodUtil.getAccessibleMethod(sPackageParserClass,
                    "generatePackageInfo", mPackage.getClass(), int[].class,
                    int.class, long.class, long.class, HashSet.class,
                    sPackageUserStateClass, int.class);
            return (PackageInfo) method.invoke(null, mPackage, gids, flags,
                    firstInstallTime, lastUpdateTime, grantedPermissions,
                    mDefaultPackageUserState, mUserId);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            Method method = MethodUtil.getAccessibleMethod(sPackageParserClass,
                    "generatePackageInfo", mPackage.getClass(), int[].class,
                    int.class, long.class, long.class,
                    sArraySetClass, sPackageUserStateClass, int.class);

            Object grantedPermissionsArray = null;
            try {
                Constructor constructor = sArraySetClass.getConstructor(Collection.class);
                grantedPermissionsArray = constructor.newInstance(grantedPermissions);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (grantedPermissionsArray == null) {
                grantedPermissionsArray = grantedPermissions;
            }
            return (PackageInfo) method.invoke(null, mPackage, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissionsArray, mDefaultPackageUserState, mUserId);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        throw new NoSuchMethodException("Can not found method generatePackageInfo");
    }
}
