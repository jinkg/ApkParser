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

package com.yalin.apkparser;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

import com.yalin.apkparser.parser.ApkParser;
import com.yalin.apkparser.utils.ComponentNameComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

public class Parser {
    private final File mPackageFile;
    private final ApkParser mParser;
    private final String mPackageName;
    private final PackageInfo mHostPackageInfo;

    private final Map<ComponentName, Object> mActivityObjCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, Object> mServiceObjCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, Object> mProviderObjCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, Object> mReceiversObjCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, Object> mInstrumentationObjCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, Object> mPermissionsObjCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, Object> mPermissionGroupObjCache
            = new TreeMap<>(new ComponentNameComparator());
    private final ArrayList<String> mRequestedPermissionsCache = new ArrayList<>();


    private final Map<ComponentName, List<IntentFilter>> mActivityIntentFilterCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, List<IntentFilter>> mServiceIntentFilterCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, List<IntentFilter>> mProviderIntentFilterCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, List<IntentFilter>> mReceiverIntentFilterCache
            = new TreeMap<>(new ComponentNameComparator());


    private final Map<ComponentName, ActivityInfo> mActivityInfoCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, ServiceInfo> mServiceInfoCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, ProviderInfo> mProviderInfoCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, ActivityInfo> mReceiversInfoCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, InstrumentationInfo> mInstrumentationInfoCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, PermissionGroupInfo> mPermissionGroupInfoCache
            = new TreeMap<>(new ComponentNameComparator());
    private final Map<ComponentName, PermissionInfo> mPermissionsInfoCache
            = new TreeMap<>(new ComponentNameComparator());


    public Parser(Context context, String packageFile) throws Exception {
        this(context, new File(packageFile));
    }

    public Parser(Context context, File packageFile) throws Exception {
        mPackageFile = packageFile;
        mParser = ApkParser.newApkParser(context);
        mParser.parsePackage(packageFile, 0);
        mPackageName = mParser.getPackageName();
        mHostPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

        List activities = mParser.getActivities();
        for (Object data : activities) {
            ComponentName componentName =
                    new ComponentName(mPackageName, mParser.readNameFromComponent(data));
            synchronized (mActivityObjCache) {
                mActivityObjCache.put(componentName, data);
            }
            synchronized (mActivityInfoCache) {
                ActivityInfo value = mParser.generateActivityInfo(data, 0);
                mActivityInfoCache.put(componentName, value);
            }

            List<IntentFilter> filters = mParser.readIntentFilterFromComponent(data);
            synchronized (mActivityIntentFilterCache) {
                mActivityIntentFilterCache.remove(componentName);
                mActivityIntentFilterCache.put(componentName, new ArrayList<>(filters));
            }
        }

        List services = mParser.getServices();
        for (Object data : services) {
            ComponentName componentName =
                    new ComponentName(mPackageName, mParser.readNameFromComponent(data));
            synchronized (mServiceObjCache) {
                mServiceObjCache.put(componentName, data);
            }
            synchronized (mServiceInfoCache) {
                ServiceInfo value = mParser.generateServiceInfo(data, 0);
                mServiceInfoCache.put(componentName, value);
            }

            List<IntentFilter> filters = mParser.readIntentFilterFromComponent(data);
            synchronized (mServiceIntentFilterCache) {
                mServiceIntentFilterCache.remove(componentName);
                mServiceIntentFilterCache.put(componentName, new ArrayList<>(filters));
            }
        }

        List providers = mParser.getProviders();
        for (Object data : providers) {
            ComponentName componentName =
                    new ComponentName(mPackageName, mParser.readNameFromComponent(data));
            synchronized (mProviderObjCache) {
                mProviderObjCache.put(componentName, data);
            }
            synchronized (mProviderInfoCache) {
                ProviderInfo value = mParser.generateProviderInfo(data, 0);
                mProviderInfoCache.put(componentName, value);
            }

            List<IntentFilter> filters = mParser.readIntentFilterFromComponent(data);
            synchronized (mProviderIntentFilterCache) {
                mProviderIntentFilterCache.remove(componentName);
                mProviderIntentFilterCache.put(componentName, new ArrayList<>(filters));
            }
        }

        List receivers = mParser.getReceivers();
        for (Object data : receivers) {
            ComponentName componentName =
                    new ComponentName(mPackageName, mParser.readNameFromComponent(data));
            synchronized (mReceiversObjCache) {
                mReceiversObjCache.put(componentName, data);
            }

            synchronized (mReceiversInfoCache) {
                ActivityInfo value = mParser.generateReceiverInfo(data, 0);
                mReceiversInfoCache.put(componentName, value);
            }
            List<IntentFilter> filters = mParser.readIntentFilterFromComponent(data);
            synchronized (mReceiverIntentFilterCache) {
                mReceiverIntentFilterCache.remove(componentName);
                mReceiverIntentFilterCache.put(componentName, new ArrayList<>(filters));
            }
        }

        List instrumentations = mParser.getInstrumentations();
        for (Object data : instrumentations) {
            ComponentName componentName =
                    new ComponentName(mPackageName, mParser.readNameFromComponent(data));
            synchronized (mInstrumentationObjCache) {
                mInstrumentationObjCache.put(componentName, data);
            }

            synchronized (mInstrumentationInfoCache) {
                InstrumentationInfo value = mParser.generateInstrumentationInfo(data, 0);
                mInstrumentationInfoCache.put(componentName, value);
            }
        }

        List permissions = mParser.getPermissions();
        for (Object data : permissions) {
            String cls = mParser.readNameFromComponent(data);
            if (cls != null) {
                ComponentName componentName = new ComponentName(mPackageName, cls);
                synchronized (mPermissionsObjCache) {
                    mPermissionsObjCache.put(componentName, data);
                }
                synchronized (mPermissionsInfoCache) {
                    PermissionInfo value = mParser.generatePermissionInfo(data, 0);
                    mPermissionsInfoCache.put(componentName, value);
                }
            }
        }

        List permissionGroups = mParser.getPermissionGroups();
        for (Object data : permissionGroups) {
            ComponentName componentName =
                    new ComponentName(mPackageName, mParser.readNameFromComponent(data));
            synchronized (mPermissionGroupObjCache) {
                mPermissionGroupObjCache.put(componentName, data);
            }
            synchronized (mPermissionGroupInfoCache) {
                PermissionGroupInfo value = mParser.generatePermissionGroupInfo(data, 0);
                mPermissionGroupInfoCache.put(componentName, value);
            }
        }

        //noinspection unchecked
        List<String> requestedPermissions = mParser.getRequestedPermissions();
        if (requestedPermissions != null && requestedPermissions.size() > 0) {
            synchronized (mRequestedPermissionsCache) {
                mRequestedPermissionsCache.addAll(requestedPermissions);
            }
        }
    }

    public PackageInfo getPackageInfo(int flags) throws Exception {
        return mParser.generatePackageInfo(mHostPackageInfo.gids,
                flags, mPackageFile.lastModified(), mPackageFile.lastModified(),
                new HashSet<>(getRequestedPermissions()));
    }

    public List<ActivityInfo> getActivities() throws Exception {
        return new ArrayList<>(mActivityInfoCache.values());
    }

    public List<ServiceInfo> getServices() throws Exception {
        return new ArrayList<>(mServiceInfoCache.values());
    }

    public List<ProviderInfo> getProviders() throws Exception {
        return new ArrayList<>(mProviderInfoCache.values());
    }

    public List<ActivityInfo> getReceivers() throws Exception {
        return new ArrayList<>(mReceiversInfoCache.values());
    }

    public List<PermissionInfo> getPermissions() throws Exception {
        return new ArrayList<>(mPermissionsInfoCache.values());
    }

    public List<PermissionGroupInfo> getPermissionGroups() throws Exception {
        return new ArrayList<>(mPermissionGroupInfoCache.values());
    }

    public List<InstrumentationInfo> getInstrumentations() {
        return new ArrayList<>(mInstrumentationInfoCache.values());
    }

    public List<String> getRequestedPermissions() throws Exception {
        synchronized (mRequestedPermissionsCache) {
            return new ArrayList<>(mRequestedPermissionsCache);
        }
    }

    public String getPackageName() throws Exception {
        return mPackageName;
    }
}
