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

package com.yalin.apkparser.parser;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;

import com.yalin.apkparser.reflect.MethodUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

@TargetApi(22)
class ApkParserApi22 extends ApkParserApi21 {
    ApkParserApi22(Context context) throws Exception {
        super(context);
    }

    @Override
    public PackageInfo generatePackageInfo(int[] gids, int flags,
                                           long firstInstallTime, long lastUpdateTime,
                                           HashSet<String> grantedPermissions) throws Exception {
        // public static PackageInfo generatePackageInfo(PackageParser.Package p,
        // int gids[], int flags, long firstInstallTime, long lastUpdateTime,
        // HashSet<String> grantedPermissions, PackageUserState state, int userId)
        try {
            return super.generatePackageInfo(gids, flags,
                    firstInstallTime, lastUpdateTime, grantedPermissions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Method method = MethodUtil.getAccessibleMethod(sPackageParserClass,
                "generatePackageInfo", mPackage.getClass(), int[].class,
                int.class, long.class, long.class,
                sArraySetClass, sPackageUserStateClass, int.class);
        Object grantedPermissionsArray = null;
        try {
            Constructor constructor = sArraySetClass.getConstructor(Collection.class);
            grantedPermissionsArray = constructor.newInstance(constructor, grantedPermissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (grantedPermissionsArray == null) {
            grantedPermissionsArray = grantedPermissions;
        }
        return (PackageInfo) method.invoke(null, mPackage, gids, flags,
                firstInstallTime, lastUpdateTime, grantedPermissionsArray,
                mDefaultPackageUserState, mUserId);
    }
}
