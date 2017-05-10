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

public enum UpdateErrors {

    /**
     * No internet connection.
     */

    NETWORK_NOT_AVAILABLE,


    /**
     * An error occurred when downloading updates.
     */

    ERROR_DOWNLOADING_UPDATES,


    /**
     * An error occurred while checking for updates.
     */

    ERROR_CHECKING_UPDATES,


    /**
     * Json file is missing.
     */

    JSON_FILE_IS_MISSING,


    /**
     * "The file containing information about the updates are empty.
     */

    FILE_JSON_NO_DATA

}
