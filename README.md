AppVerUpdater
---------------------

<p align="left">
 <a target="_blank" href="https://android-arsenal.com/api?level=14"><img src="https://img.shields.io/badge/API-14+-orange.svg"></a>
 <a href="https://github.com/atzcx/AppVerUpdater"><img src="https://jitpack.io/v/atzcx/AppVerUpdater.svg"></a>
 <a target="_blank" href="https://android-arsenal.com/details/1/5050"><img src="https://img.shields.io/badge/Android%20Arsenal-AppVerUpdater-blue.svg"></a>
 </p>

A library that checks for your apps' updates on your own server. If you want to publish your app in Google Play it is best not to use the library. Google Play prohibits self-renewal. API 14+ required.

<p align="center">
<br><img src="https://github.com/atzcx/AppVerUpdater/blob/master/screenshots/screenshot.png" >
</p>

How to include
--------------

Add the repository to your project build.gradle:
```JavaScript
repositories {
    maven {
        url "https://jitpack.io"
    }
}
```
Maven:
```xml
<dependency>
	<groupId>com.github.atzcx</groupId>
	<artifactId>AppVerUpdater</artifactId>
	<version>1.0.10</version>
</dependency>
```


or Gradle:
```JavaScript
compile 'com.github.atzcx:AppVerUpdater:1.0.10'
```

Usage
--------

Add permissions to your app's Manifest:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

Basic Usage
-------------------
Activity and Fragment

```Java
AppVerUpdater appVerUpdater = null;
```

```Java
appVerUpdater = new AppVerUpdater(this)
	.setUpdateJSONUrl("http://example.com/update.json")
	.setShowNotUpdated(true)
	.setViewNotes(true)
	.build();
```
To track the status of your network, add this code *

```Java
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
```

[Example usage](https://github.com/atzcx/AppVerUpdater/blob/master/app/src/main/java/com/github/atzcx/appverupdater/sample/MainActivity.java)


Example JSON
------------------

```Json
{
  "newVersion": "4.4.0",
  "apkUrl": "https://example.net/example.apk",
  "versionNotes": [
    "- Bug fixes"
  ]
}
```

String Resourses
---------------------

```xml
<!-- AlertDialog Update Available -->
<string name="appverupdate_update_available">Available a new update!</string>
<string name="appverupdater_content_update_available">Update %1$s mobile app to version %2$s</string>
<string name="appverupdater_notes_update_available">Update %1$s mobile app to version %2$s \nFeatures: \n%3$s</string>
<string name="appverupdater_positivetext_update_available">Update</string>
<string name="appverupdater_negativetext_update_available">Later</string>
<!-- AlertDialog Not Update Available -->
<string name="appverupdate_not_update_available">No updates available!</string>
<string name="appverupdater_content_not_update_available">You have the latest version of the application</string>
<!-- ProgressDialog Update Available -->
<string name="appverupdater_progressdialog_message_update_available">Download</string>
<!-- AlertDialog Denied Permissin -->
<string name="appverupdater_denied_message">If you reject permission,you can not use this service</string>
```

License
----------
    Copyright 2016 Aleksandr Tarakanov
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
