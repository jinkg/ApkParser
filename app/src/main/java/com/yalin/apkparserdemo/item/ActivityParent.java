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

package com.yalin.apkparserdemo.item;

import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.TextView;

import com.yalin.apkparserdemo.R;

import java.util.List;

/**
 * @author jinyalin
 * @since 2017/3/13.
 */

public class ActivityParent extends ParentItem<ActivityInfo> {

    public ActivityParent(List<ActivityInfo> children) {
        super(children);
    }

    @Override
    public void bindParentView(View convertView) {
        TextView parentName = (TextView) convertView.findViewById(R.id.parent_name);
        parentName.setText("ActivityInfo");
    }

    @Override
    public void bindChildView(int childPosition, View convertView) {
        TextView childName = (TextView) convertView.findViewById(R.id.child_name);
        childName.setText(mChildren.get(childPosition).name);
    }


}
