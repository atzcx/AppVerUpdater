/*
 * Copyright 2017 Aleksandr Tarakanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.atzcx.appverupdater;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class JSONParser {

    private static final String KEY_LATEST_VERSION = "newVersion";
    private static final String KEY_RELEASE_NOTES = "versionNotes";
    private static final String KEY_URL = "apkUrl";

    public static UpdateInfo parse(JSONObject jsonObject){
        try {
            UpdateInfo updateModel = new UpdateInfo();

            String JsonNewVersion = jsonObject.getString(KEY_LATEST_VERSION).trim();
            String JsonApkUrl = jsonObject.getString(KEY_URL);

            if (JsonNewVersion == null || JsonNewVersion.length() == 0) {
                throw new IllegalArgumentException("Argument JsonNewVersion cannot be null or empty");
            }

            if (JsonApkUrl == null || JsonApkUrl.length() == 0) {
                throw new IllegalArgumentException("Argument JsonApkUrl cannot be null or empty");
            }

            updateModel.setVersion(JsonNewVersion);
            updateModel.setUrl(JsonApkUrl);

            JSONArray releaseArr = jsonObject.optJSONArray(KEY_RELEASE_NOTES);
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < releaseArr.length(); ++i) {
                builder.append(releaseArr.getString(i).trim());
                builder.append(System.getProperty("line.separator"));
            }
            updateModel.setNotes(builder.toString());

            return updateModel;

        } catch (JSONException e){
            if (BuildConfig.DEBUG){
                Log.e(AppVerUpdater.TAG, "The JSON updater file is mal-formatted.");
            }
        }

        return null;
    }

}
