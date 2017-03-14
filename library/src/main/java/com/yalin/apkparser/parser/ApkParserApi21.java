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

import com.yalin.apkparser.reflect.MethodUtil;

import java.io.File;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

@TargetApi(21)
class ApkParserApi21 extends ApkParserApi17 {
    ApkParserApi21(Context context) throws Exception {
        super(context);
    }

    @Override
    public void parsePackage(File file, int flags) throws Exception {
        // public Package parsePackage(File packageFile, int flags) throws PackageParserException
        mPackageParser = sPackageParserClass.newInstance();
        mPackage = MethodUtil.invokeMethod(mPackageParser, "parsePackage", file, flags);
    }
}
