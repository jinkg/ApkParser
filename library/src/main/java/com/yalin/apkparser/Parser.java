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
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;

import com.yalin.apkparser.parser.ApkParser;
import com.yalin.apkparser.utils.ComponentNameComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

public class Parser {
    private final ApkParser mParser;
    private final String mPackageName;

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
        mParser = ApkParser.newApkParser(context);
        mParser.parsePackage(packageFile, 0);
        mPackageName = mParser.getPackageName();

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
                if (TextUtils.isEmpty(value.processName)) {
                    value.processName = value.packageName;
                }
                mServiceInfoCache.put(componentName, value);
            }

            List<IntentFilter> filters = mParser.readIntentFilterFromComponent(data);
            synchronized (mServiceIntentFilterCache) {
                mServiceIntentFilterCache.remove(componentName);
                mServiceIntentFilterCache.put(componentName, new ArrayList<>(filters));
            }
        }
    }

    public List<ActivityInfo> getActivities() throws Exception {
        return new ArrayList<>(mActivityInfoCache.values());
    }

    public List<ServiceInfo> getServices() throws Exception {
        return new ArrayList<>(mServiceInfoCache.values());
    }
}
