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
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Build;

import java.io.File;
import java.util.HashSet;
import java.util.List;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

public abstract class ApkParser {

    protected Context mContext;

    protected Object mPackageParser;

    ApkParser(Context context) {
        mContext = context;
    }

    public static ApkParser newApkParser(Context context) throws Exception {
        if (Build.VERSION.SDK_INT >= 22) {
            return new ApkParserApi22(context);
        } else if (Build.VERSION.SDK_INT >= 21) {
            return new ApkParserApi21(context);
        } else if (Build.VERSION.SDK_INT >= 17) {
            return new ApkParserApi17(context);
        } else if (Build.VERSION.SDK_INT >= 16) {
            return new ApkParserApi16(context);
        } else if (Build.VERSION.SDK_INT >= 14) {
            return new ApkParserApi14(context);
        } else if (Build.VERSION.SDK_INT >= 11) {
            return new ApkParserApi11(context);
        } else if (Build.VERSION.SDK_INT >= 9) {
            return new ApkParserApi9(context);
        } else {
            return new ApkParserApi9(context);
        }
    }

    public abstract void parsePackage(File file, int flags) throws Exception;

    public abstract void collectCertificates(int flags) throws Exception;

    public abstract ActivityInfo generateActivityInfo(Object activity, int flags) throws Exception;

    public abstract ServiceInfo generateServiceInfo(Object service, int flags) throws Exception;

    public abstract ProviderInfo generateProviderInfo(Object provider, int flags) throws Exception;

    public ActivityInfo generateReceiverInfo(Object receiver, int flags) throws Exception {
        return generateActivityInfo(receiver, flags);
    }

    public abstract InstrumentationInfo generateInstrumentationInfo(Object instrumentation,
                                                                    int flags) throws Exception;

    public abstract ApplicationInfo generateApplicationInfo(int flags) throws Exception;

    public abstract PermissionGroupInfo generatePermissionGroupInfo(Object permissionGroup,
                                                                    int flags) throws Exception;

    public abstract PermissionInfo generatePermissionInfo(Object permission,
                                                          int flags) throws Exception;

    public abstract PackageInfo generatePackageInfo(
            int gids[], int flags, long firstInstallTime, long lastUpdateTime,
            HashSet<String> grantedPermissions) throws Exception;

    public abstract List getActivities() throws Exception;

    public abstract List getServices() throws Exception;

    public abstract List getProviders() throws Exception;

    public abstract List getPermissions() throws Exception;

    public abstract List getPermissionGroups() throws Exception;

    public abstract List getRequestedPermissions() throws Exception;

    public abstract List getReceivers() throws Exception;

    public abstract List getInstrumentations() throws Exception;

    public abstract String getPackageName() throws Exception;

    public abstract String readNameFromComponent(Object data) throws Exception;

    public abstract List<IntentFilter> readIntentFilterFromComponent(Object data) throws Exception;

    public abstract void writeSignature(Signature[] signatures) throws Exception;
}
