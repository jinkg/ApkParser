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
import android.util.DisplayMetrics;

import com.yalin.apkparser.reflect.FieldUtil;
import com.yalin.apkparser.reflect.MethodUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

@TargetApi(9)
class ApkParserApi9 extends ApkParser {
    protected Class<?> sPackageParserClass;
    protected Class<?> sActivityClass;
    protected Class<?> sServiceClass;
    protected Class<?> sProviderClass;
    protected Class<?> sInstrumentationClass;
    protected Class<?> sPermissionClass;
    protected Class<?> sPermissionGroupClass;
    protected Class<?> sArraySetClass;

    protected Object mPackage;

    ApkParserApi9(Context context) throws Exception {
        super(context);
        initClasses();
    }

    private void initClasses() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        sPackageParserClass = Class.forName("android.content.pm.PackageParser");
        sActivityClass = Class.forName("android.content.pm.PackageParser$Activity");
        sServiceClass = Class.forName("android.content.pm.PackageParser$Service");
        sProviderClass = Class.forName("android.content.pm.PackageParser$Provider");
        sInstrumentationClass = Class.forName("android.content.pm.PackageParser$Instrumentation");
        sPermissionClass = Class.forName("android.content.pm.PackageParser$Permission");
        sPermissionGroupClass = Class.forName("android.content.pm.PackageParser$PermissionGroup");
        try {
            sArraySetClass = Class.forName("android.util.ArraySet");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parsePackage(File file, int flags) throws Exception {
        // public Package parsePackage(File sourceFile, String destCodePath, DisplayMetrics metrics, int flags)
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        String destCodePath = file.getPath();
        mPackageParser = MethodUtil.invokeConstructor(sPackageParserClass, destCodePath);
        mPackage = MethodUtil.invokeMethod(mPackageParser, "parsePackage",
                file, destCodePath, metrics, flags);
    }

    @Override
    public void collectCertificates(int flags) throws Exception {

    }

    @Override
    public ActivityInfo generateActivityInfo(Object activity, int flags) throws Exception {
        // public static final ActivityInfo generateActivityInfo(Activity a, int flags)
        Method method = MethodUtil.getAccessibleMethod(sPackageParserClass,
                "generateActivityInfo", sActivityClass, int.class);
        return (ActivityInfo) method.invoke(null, activity, flags);
    }

    @Override
    public ServiceInfo generateServiceInfo(Object service, int flags) throws Exception {
        // public static final ServiceInfo generateServiceInfo(Service s, int flags)
        Method method = MethodUtil.getAccessibleMethod(sPackageParserClass,
                "generateServiceInfo", sServiceClass, int.class);
        return (ServiceInfo) method.invoke(null, service, flags);
    }

    @Override
    public ProviderInfo generateProviderInfo(Object provider, int flags) throws Exception {
        return null;
    }

    @Override
    public InstrumentationInfo generateInstrumentationInfo(
            Object instrumentation, int flags) throws Exception {
        return null;
    }

    @Override
    public ApplicationInfo generateApplicationInfo(int flags) throws Exception {
        return null;
    }

    @Override
    public PermissionGroupInfo generatePermissionGroupInfo(
            Object permissionGroup, int flags) throws Exception {
        return null;
    }

    @Override
    public PermissionInfo generatePermissionInfo(Object permission, int flags) throws Exception {
        return null;
    }

    @Override
    public PackageInfo generatePackageInfo(
            int[] gids, int flags, long firstInstallTime, long lastUpdateTime,
            HashSet<String> grantedPermissions) throws Exception {
        return null;
    }

    @Override
    public List getActivities() throws Exception {
        // PackageParser.Package.activities
        return (List) FieldUtil.readField(mPackage, "activities");
    }

    @Override
    public List getServices() throws Exception {
        // PackageParser.Package.services
        return (List) FieldUtil.readField(mPackage, "services");
    }

    @Override
    public List getProviders() throws Exception {
        // PackageParser.Package.providers
        return (List) FieldUtil.readField(mPackage, "providers");
    }

    @Override
    public List getPermissions() throws Exception {
        // PackageParser.Package.permissions
        return (List) FieldUtil.readField(mPackage, "permissions");
    }

    @Override
    public List getPermissionGroups() throws Exception {
        // PackageParser.Package.permissionGroups
        return (List) FieldUtil.readField(mPackage, "permissionGroups");
    }

    @Override
    public List getRequestedPermissions() throws Exception {
        // PackageParser.Package.requestedPermissions
        return (List) FieldUtil.readField(mPackage, "requestedPermissions");
    }

    @Override
    public List getReceivers() throws Exception {
        // PackageParser.Package.requestedPermissions
        return (List) FieldUtil.readField(mPackage, "receivers");
    }

    @Override
    public List getInstrumentations() throws Exception {
        // PackageParser.Package.instrumentation
        return (List) FieldUtil.readField(mPackage, "instrumentation");
    }

    @Override
    public String getPackageName() throws Exception {
        // PackageParser.Package.packageName
        return (String) FieldUtil.readField(mPackage, "packageName");
    }

    @Override
    public String readNameFromComponent(Object data) throws Exception {
        return (String) FieldUtil.readField(data, "className");
    }

    @Override
    public List<IntentFilter> readIntentFilterFromComponent(Object data) throws Exception {
        //noinspection unchecked
        return (List<IntentFilter>) FieldUtil.readField(data, "intents");
    }

    @Override
    public void writeSignature(Signature[] signatures) throws Exception {

    }
}
