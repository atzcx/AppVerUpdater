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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;

import com.github.atzcx.appverupdater.callback.Callback;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.thin.downloadmanager.DownloadRequest;

import java.io.File;
import java.util.ArrayList;

public class AppVerUpdater extends DialogFragment {

    public static final String TAG = "AppVerUpdater";

    private String url;
    private HttpClient.AsyncStringRequest stringRequest;

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

    private UpdateInfo response;

    public AppVerUpdater() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        this.title_available = context.getResources().getString(R.string.appverupdate_update_available);
        this.content_available = context.getResources().getString(R.string.appverupdater_content_update_available);
        this.contentNotes_available = context.getResources().getString(R.string.appverupdater_notes_update_available);
        this.positiveText_available = context.getResources().getString(R.string.appverupdater_positivetext_update_available);
        this.negativeText_available = context.getResources().getString(R.string.appverupdater_negativetext_update_available);
        this.title_not_available = context.getResources().getString(R.string.appverupdate_not_update_available);
        this.content_not_available = context.getResources().getString(R.string.appverupdater_content_not_update_available);
        this.message = context.getResources().getString(R.string.appverupdater_progressdialog_message_update_available);
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
        setAlertDialogUpdateAvailableTitle(getActivity().getText(titleRes));
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailableTitle(@NonNull CharSequence title) {
        this.title_available = title;
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailableContent(@StringRes int contentRes) {
        setAlertDialogUpdateAvailableContent(getActivity().getText(contentRes));
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailableContent(@NonNull CharSequence content) {
        this.content_available = content;
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailablePositiveText(@StringRes int positiveTextRes) {
        setAlertDialogUpdateAvailablePositiveText(getActivity().getText(positiveTextRes));
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailablePositiveText(@NonNull CharSequence positiveText) {
        this.positiveText_available = positiveText;
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailableNegativeText(@StringRes int negativeTextRes) {
        setAlertDialogUpdateAvailableNegativeText(getActivity().getText(negativeTextRes));
        return this;
    }

    public AppVerUpdater setAlertDialogUpdateAvailableNegativeText(@NonNull CharSequence negativeText) {
        this.negativeText_available = negativeText;
        return this;
    }

    public AppVerUpdater setProgressDialogUpdateAvailableMessage(@StringRes int messageRes) {
        setProgressDialogUpdateAvailableMessage(getActivity().getText(messageRes));
        return this;
    }

    public AppVerUpdater setProgressDialogUpdateAvailableMessage(@NonNull CharSequence message) {
        this.message = message;
        return this;
    }

    public AppVerUpdater setAlertDialogNotUpdateAvailableTitle(@StringRes int titleRes) {
        setAlertDialogNotUpdateAvailableTitle(getActivity().getText(titleRes));
        return this;
    }

    public AppVerUpdater setAlertDialogNotUpdateAvailableTitle(@NonNull CharSequence title) {
        this.title_not_available = title;
        return this;
    }

    public AppVerUpdater setAlertDialogNotUpdateAvailableContent(@StringRes int contentRes) {
        setAlertDialogNotUpdateAvailableContent(getActivity().getText(contentRes));
        return this;
    }

    public AppVerUpdater setAlertDialogNotUpdateAvailableContent(@NonNull CharSequence content) {
        this.content_not_available = content;
        return this;
    }

    public AppVerUpdater setAlertDialogDeniedMessage(@StringRes int denied_messageRes) {
        setAlertDialogDeniedMessage(getActivity().getText(denied_messageRes));
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

    public AppVerUpdater build(final Activity context) {
        onAttach(context);
        if (Build.VERSION.SDK_INT >= 23) {
            new TedPermission(context)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            update(context);
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
            update(context);
        }
        return this;
    }

    public static void stop() {
        if (progressDialogFragment.downloadRequest != null && progressDialogFragment.downloadRequest.get() != null
            && !progressDialogFragment.downloadRequest.get().isCancelled())
            progressDialogFragment.downloadRequest.get().cancel();
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
            if (!LibraryUtils.isNetworkAvailable(context)) stop();
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity context = getActivity();
        if (context == null) {
            Log.w(TAG, "App updater's context is not available anymore, cannot proceed");
            return null;
        }

        if (LibraryUtils.isUpdateAvailable(LibraryUtils.appVersion(context), response.getVersion())) {

            if (BuildConfig.DEBUG) {
                Log.v(TAG, "UpdateInfo...");
            }

            if (cancelable){
                return new AlertDialog.Builder(context)
                    .setTitle(title_available)
                    .setMessage(formatContent(context, response))
                    .setPositiveButton(positiveText_available, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            callback.onUpdateChecked();
                            dialogInterface.dismiss();
                            downloadUpdates(context, response.getUrl(), message);
                        }
                    })
                    .setNegativeButton(negativeText_available, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            callback.onUpdateChecked();
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
            } else {
                return new AlertDialog.Builder(context)
                    .setTitle(title_available)
                    .setMessage(formatContent(context, response))
                    .setPositiveButton(positiveText_available, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            callback.onUpdateChecked();
                            dialogInterface.dismiss();
                            downloadUpdates(context, response.getUrl(), message);
                        }
                    })
                    .setCancelable(cancelable)
                    .create();
            }

        } else {
            // show update not available
            return new AlertDialog.Builder(context)
                .setTitle(title_not_available)
                .setMessage(content_not_available)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onUpdateChecked();
                        dialogInterface.dismiss();
                    }
                }).create();
        }
    }

    private void update(final Activity context) {
        // if progressdialogfragment exists, we are still in the middle of downloading, and likely the device was rotated
        if (progressDialogFragment != null) {
            progressDialogFragment.show(context.getFragmentManager(), "downloadprogress");
            return;
        }

        try {
            stringRequest = new HttpClient.AsyncStringRequest(context, url, new HttpCallback<UpdateInfo>() {
                @Override
                public void onSuccess(final UpdateInfo response) {
                    AppVerUpdater.this.response = response;
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (context.isFinishing()) return;
                            if (LibraryUtils.isUpdateAvailable(LibraryUtils.appVersion(context), response.getVersion())
                                || showNotUpdate)
                                show(context.getFragmentManager(), "updater");
                            else callback.onUpdateChecked();
                        }
                    });
                }

                @Override
                public void onFailure(UpdateErrors error) {
                    if (callback != null) {
                        failureCallback(context, error);
                    }
                }

                @Override
                public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
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

    public static class ProgressDialogFragment extends DialogFragment {
        private ProgressDialog progressDialog = null;
        HttpClient.AsyncDownloadRequest downloadRequest;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            setRetainInstance(true);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            return progressDialog;
        }
        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            stop();
        }

        public void updateProgress(String message ) {
            if (progressDialog != null) progressDialog.setMessage(message);
        }
    }

    private static ProgressDialogFragment progressDialogFragment;

    private void downloadUpdates(final Activity context, String url, final CharSequence message) {
        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.downloadRequest = new HttpClient.AsyncDownloadRequest(context, url, message, "update-" + LibraryUtils.currentDate() + ".apk", new HttpCallback<File>() {
            @Override
            public void onSuccess(final File response) {
                callback.onDownloadSuccess();
                if (response != null) {
                    LibraryUtils.installApkAsFile(context, response);
                }
                progressDialogFragment.dismiss();
                progressDialogFragment = null;
            }

            @Override
            public void onFailure(UpdateErrors error) {
                if (callback != null) {
                    failureCallback(context, error);
                }
                progressDialogFragment.dismiss();
                progressDialogFragment = null;
            }

            @Override
            public void onProgress(DownloadRequest downloadRequest, long totalBytes,
                            long downloadedBytes, int progress) {
                progressDialogFragment.updateProgress(message + " - " + progress + "%");
            }
        });
        progressDialogFragment.downloadRequest.execute();
        progressDialogFragment.show(context.getFragmentManager(), "downloadprogress");
    }

    private void failureCallback(Context context, final UpdateErrors error) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(error);
            }
        });
    }
}
