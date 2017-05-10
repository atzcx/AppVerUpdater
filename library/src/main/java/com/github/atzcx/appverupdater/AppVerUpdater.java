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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;

import com.github.atzcx.appverupdater.callback.Callback;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

public class AppVerUpdater {

    public static final String TAG = "AppVerUpdater";

    private Context context;
    private String url;
    private HttpClient.AsyncStringRequest stringRequest;
    private HttpClient.AsyncDownloadRequest downloadRequest;

    private CharSequence title_available;
    private CharSequence content_available;
    private CharSequence contentNotes_available;
    private CharSequence positiveText_available;
    private CharSequence negativeText_available;
    private CharSequence title_not_available;
    private CharSequence content_not_available;
    private CharSequence message;
    private CharSequence denied_message;

    private boolean viewNotes = false;
    private boolean showNotUpdate = false;
    private boolean cancelable = true;

    private Callback callback;

    private AlertDialog alertDialog;

    public AppVerUpdater(@NonNull Context context) {
        this.context = context;

        this.title_available = this.context.getResources().getString(R.string.appverupdate_update_available);
        this.content_available = this.context.getResources().getString(R.string.appverupdater_content_update_available);
        this.contentNotes_available = this.context.getResources().getString(R.string.appverupdater_notes_update_available);
        this.positiveText_available = this.context.getResources().getString(R.string.appverupdater_positivetext_update_available);
        this.negativeText_available = this.context.getResources().getString(R.string.appverupdater_negativetext_update_available);
        this.title_not_available = this.context.getResources().getString(R.string.appverupdate_not_update_available);
        this.content_not_available = this.context.getResources().getString(R.string.appverupdater_content_not_update_available);
        this.message = this.context.getResources().getString(R.string.appverupdater_progressdialog_message_update_available);
        this.denied_message = context.getResources().getString(R.string.appverupdater_denied_message);

    }

    public AppVerUpdater setUpdateJSONUrl(@NonNull String url) {
        this.url = url;
        return this;
    }

    public AppVerUpdater setShowNotUpdated(boolean showNotUpdate) {
        this.showNotUpdate = showNotUpdate;
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailableTitle(@StringRes int titleRes) {
        setAlertDialogUpdateAvailableTitle(this.context.getText(titleRes));
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailableTitle(@NonNull CharSequence title) {
        this.title_available = title;
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailableContent(@StringRes int contentRes) {
        setAlertDialogUpdateAvailableContent(this.context.getText(contentRes));
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailableContent(@NonNull CharSequence content) {
        this.content_available = content;
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailablePositiveText(@StringRes int positiveTextRes) {
        setAlertDialogUpdateAvailablePositiveText(this.context.getText(positiveTextRes));
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailablePositiveText(@NonNull CharSequence positiveText) {
        this.positiveText_available = positiveText;
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailableNegativeText(@StringRes int negativeTextRes) {
        setAlertDialogUpdateAvailableNegativeText(this.context.getText(negativeTextRes));
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailableNegativeText(@NonNull CharSequence negativeText) {
        this.negativeText_available = negativeText;
        return this;
    }

    public AppVerUpdater setProgressDialogUpdateAvailableMessage(@StringRes int messageRes) {
        setProgressDialogUpdateAvailableMessage(this.context.getText(messageRes));
        return this;
    }

    public AppVerUpdater setProgressDialogUpdateAvailableMessage(@NonNull CharSequence message) {
        this.message = message;
        return this;
    }

    public AppVerUpdater setAlertDialogNotUpdateAvailableTitle(@StringRes int titleRes) {
        setAlertDialogNotUpdateAvailableTitle(this.context.getText(titleRes));
        return this;
    }

    public AppVerUpdater setAlertDialogNotUpdateAvailableTitle(@NonNull CharSequence title) {
        this.title_not_available = title;
        return this;
    }

    public AppVerUpdater setAlertDialogNotUpdateAvailableContent(@StringRes int contentRes) {
        setAlertDialogNotUpdateAvailableContent(this.context.getText(contentRes));
        return this;
    }

    public AppVerUpdater setAlertDialogNotUpdateAvailableContent(@NonNull CharSequence content) {
        this.content_not_available = content;
        return this;
    }

    public AppVerUpdater setAlertDialogDeniedMessage(@StringRes int denied_messageRes) {
        setAlertDialogDeniedMessage(this.context.getText(denied_messageRes));
        return this;
    }

    public AppVerUpdater setAlertDialogDeniedMessage(@NonNull CharSequence denied_message) {
        this.denied_message = denied_message;
        return this;
    }

    public AppVerUpdater setViewNotes(boolean viewNotes) {
        this.viewNotes = viewNotes;
        return this;
    }

    public AppVerUpdater setCallback(Callback listener) {
        this.callback = listener;
        return this;
    }

    public AppVerUpdater setAlertDialogCancelable(boolean isCancelable) {
        this.cancelable = isCancelable;
        return this;
    }

    public AppVerUpdater build() {
        if (Build.VERSION.SDK_INT >= 23) {
            new TedPermission(context)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            update();
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        }
                    })
                    .setDeniedMessage(String.valueOf(denied_message))
                    .setDeniedCloseButtonText(android.R.string.ok)
                    .setGotoSettingButton(false)
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } else {
            update();
        }
        return this;
    }

    public void stop() {
        if (downloadRequest != null && downloadRequest.get() != null && !downloadRequest.get().isCancelled()) {
            downloadRequest.get().cancel();
        }
    }

    public void dismiss() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public void onResume(Context context) {
        if (networkReceiver == null) {
            context.registerReceiver(networkReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    public void onStop(Context context) {
        if (networkReceiver != null) {
            try {
                context.unregisterReceiver(networkReceiver);
                networkReceiver = null;
            } catch (IllegalArgumentException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Exception: ", e);
                }
            }
        }
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!LibraryUtils.isNetworkAvailable(context)) {
                if (downloadRequest != null && downloadRequest.get() != null && !downloadRequest.get().isCancelled()) {
                    downloadRequest.get().cancel();
                }
            }
        }
    };

    private void update() {
        try {
            stringRequest = new HttpClient.AsyncStringRequest(context, url, new HttpCallback<UpdateInfo>() {
                @Override
                public void onSuccess(final UpdateInfo response) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (LibraryUtils.isUpdateAvailable(LibraryUtils.appVersion(context), response.getVersion())) {

                                if (BuildConfig.DEBUG) {
                                    Log.v(TAG, "UpdateInfo...");
                                }

                                if (cancelable){
                                    alertDialog = new AlertDialog.Builder(context)
                                            .setTitle(title_available)
                                            .setMessage(formatContent(context, response))
                                            .setPositiveButton(positiveText_available, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    downloadUpdates(context, response.getUrl(), message);
                                                }
                                            })
                                            .setNegativeButton(negativeText_available, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .create();
                                } else {
                                    alertDialog = new AlertDialog.Builder(context)
                                            .setTitle(title_available)
                                            .setMessage(formatContent(context, response))
                                            .setPositiveButton(positiveText_available, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    downloadUpdates(context, response.getUrl(), message);
                                                }
                                            })
                                            .setCancelable(cancelable)
                                            .create();
                                }

                                alertDialog.show();

                            } else if (showNotUpdate) {

                                alertDialog = new AlertDialog.Builder(context)
                                        .setTitle(title_not_available)
                                        .setMessage(content_not_available)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        }).create();


                                alertDialog.show();

                            }

                        }
                    });
                }

                @Override
                public void onFailure(UpdateErrors error) {
                    if (callback != null) {
                        failureCallback(error);
                    }
                }
            });

            stringRequest.execute();
        } catch (Exception e){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "Exception: ", e);
            }
        }
    }

    private CharSequence formatContent(Context context, UpdateInfo update) {
        if (content_available != null && contentNotes_available != null) {
            if (this.viewNotes) {
                if (update.getNotes() != null && !TextUtils.isEmpty(update.getNotes())) {
                    return String.format(String.valueOf(contentNotes_available), LibraryUtils.appName(context), update.getVersion(), update.getNotes());
                }
            } else {
                return String.format(String.valueOf(content_available), LibraryUtils.appName(context), update.getVersion());
            }
        }
        return content_available;
    }

    private void downloadUpdates(final Context context, String url, CharSequence message) {
        downloadRequest = new HttpClient.AsyncDownloadRequest(context, url, message, "update-" + LibraryUtils.currentDate() + ".apk", new HttpCallback<File>() {
            @Override
            public void onSuccess(final File response) {
                if (response != null) {
                    LibraryUtils.installApkAsFile(context, response);
                }

            }

            @Override
            public void onFailure(UpdateErrors error) {
                if (callback != null) {
                    failureCallback(error);
                }
            }
        });

        downloadRequest.execute();
    }

    private void failureCallback(final UpdateErrors error) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(error);
            }
        });
    }
}
