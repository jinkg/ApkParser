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

package com.yalin.apkparserdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.yalin.apkparser.Parser;

import java.util.ArrayList;
import java.util.List;


/**
 * @author jinyalin
 * @since 2017/3/13.
 */

public class MainActivity extends Activity {
    private ExpandableListView mExpandableListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mExpandableListView = (ExpandableListView) findViewById(R.id.expand_list_view);
    }

    public void parse(View view) {
        if (Build.VERSION.SDK_INT >= 16
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            doParse();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doParse();
        }
    }

    private void doParse() {
        try {
            Parser parser = new Parser(getApplicationContext(),
                    Environment.getExternalStorageDirectory() + "/ApkParser/wifi.apk");

            List<ActivityInfo> activityInfos = parser.getActivities();

            List<ParentItem> parents = new ArrayList<>();
            parents.add(new ActivityParent(activityInfos));
            mExpandableListView.setAdapter(new ListAdapter(this, parents));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ListAdapter extends BaseExpandableListAdapter {
        private LayoutInflater mInflater;
        private List<ParentItem> mParents;

        ListAdapter(Context context, List<ParentItem> parents) {
            mInflater = LayoutInflater.from(context);
            mParents = parents;
        }

        @Override
        public int getGroupCount() {
            return mParents == null ? 0 : mParents.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return getGroup(groupPosition).getChildCount();
        }

        @Override
        public ParentItem getGroup(int groupPosition) {
            return mParents.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_parent, parent, false);
            }
            getGroup(groupPosition).bindParentView(convertView);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_child, parent, false);
            }
            getGroup(groupPosition).bindChildView(childPosition, convertView);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
