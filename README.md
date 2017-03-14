# ApkParser
A library to parse .apk file, which can get activities,services ... in apk

### Screenshots
<img src="https://github.com/jinkg/Screenshots/blob/master/ApkParser/apkparser.png" width="180" height="320">

### Usage

```java
 String apkFile = Environment.getExternalStorageDirectory() + "/ApkParser/weixin.apk";
 Parser parser = new Parser(getApplicationContext(), apkFile);

 parser.collectCertificates(0);
 PackageInfo apkPackageInfo = parser.getPackageInfo(PackageManager.GET_PERMISSIONS
            | PackageManager.GET_SIGNATURES);

 String packageName = parser.getPackageName();
 List<ActivityInfo> activityInfos = parser.getActivities();
 List<ServiceInfo> serviceInfos = parser.getServices();
 List<ActivityInfo> receiverInfos = parser.getReceivers();
 List<ProviderInfo> providerInfos = parser.getProviders();
 List<InstrumentationInfo> instrumentationInfos = parser.getInstrumentations();
 List<PermissionInfo> permissionInfos = parser.getPermissions();
 List<String> requestPermissions = parser.getRequestedPermissions();
```

You can see a complete usage in the demo app.

### Feedback

nilaynij@gmail.com.

#License

    Copyright 2017 YaLin Jin

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
