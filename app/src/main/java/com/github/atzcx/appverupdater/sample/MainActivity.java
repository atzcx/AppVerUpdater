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

package com.github.atzcx.appverupdater.sample;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.atzcx.appverupdater.AppVerUpdater;
import com.github.atzcx.appverupdater.callback.Callback;
import com.github.atzcx.appverupdater.UpdateErrors;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

public class MainActivity extends AppCompatActivity {


    private AppVerUpdater appVerUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("");

        findViewById(R.id.btnUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        FloatingActionButton fab = (FloatingActionButton)  findViewById(R.id.fab);
        fab.setImageDrawable(new IconicsDrawable(this).icon(MaterialDesignIconic.Icon.gmi_github).color(Color.WHITE).sizeDp(24));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/atzcx/AppVerUpdater")));
            }
        });

        update();

    }

    private void update(){

        appVerUpdater = new AppVerUpdater()
                .setUpdateJSONUrl("http://192.168.1.12/update.json")
                .setShowNotUpdated(true)
                .setViewNotes(false)
                .setCallback(new Callback() {
                    @Override
                    public void onFailure(UpdateErrors error) {

                        if (error == UpdateErrors.NETWORK_NOT_AVAILABLE) {
                            Toast.makeText(MainActivity.this, "No internet connection.", Toast.LENGTH_LONG).show();
                        }
                        else if (error == UpdateErrors.ERROR_CHECKING_UPDATES) {
                            Toast.makeText(MainActivity.this, "An error occurred while checking for updates.", Toast.LENGTH_LONG).show();
                        }
                        else if (error == UpdateErrors.ERROR_DOWNLOADING_UPDATES) {
                            Toast.makeText(MainActivity.this, "An error occurred when downloading updates.", Toast.LENGTH_LONG).show();
                        }
                        else if (error == UpdateErrors.JSON_FILE_IS_MISSING) {
                            Toast.makeText(MainActivity.this, "Json file is missing.", Toast.LENGTH_LONG).show();
                        }
                        else if (error == UpdateErrors.FILE_JSON_NO_DATA) {
                            Toast.makeText(MainActivity.this, "The file containing information about the updates are empty.", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onDownloadSuccess() {
                        // for example, record/reset license
                    }

                    @Override
                    public void onUpdateChecked(boolean downloading) {
                        // Happens after an update check, immediately after if update check was successful and there
                        // were no dialogs, or, when an update dialog is presented and user explicitly dismissed the dialog.
                        // "downloading" is true if user accepted the update
                        // Typically used for resetting next update check time
                    }
                })
                .setAlertDialogCancelable(true)
                .build(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        appVerUpdater.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        appVerUpdater.onStop(this);
    }
}
