/*
 * Copyright (C) 2010 yvolk (Yuri Volkov), http://yurivolkov.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.addhen.smssync.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.io.File;

public class SharedPreferencesUtil {
    private static final String TAG = SharedPreferencesUtil.class.getSimpleName();

    public static final String FILE_EXTENSION = ".xml";

    /**
     * @param Context
     * @return Directory for files of SharedPreferences
     */
    public static String prefsDirectory(Context context) {
        File dir1 = new File(Environment.getDataDirectory(), "data/" + context.getPackageName());
        File dir2 = new File(dir1, "shared_prefs");
        return dir2.getAbsolutePath();
    }

    /**
     * Does the preferences file exist?
     */
    public static boolean exists(Context context, String prefsFileName) {
        boolean yes = false;

        if (context == null || prefsFileName == null || prefsFileName.length() == 0) {
            // no
        } else {
            try {
                File prefFile = new File(prefsDirectory(context), prefsFileName + FILE_EXTENSION);
                yes = prefFile.exists();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        return yes;
    }

    /**
     * Delete the preferences file!
     * 
     * @return Was the file deleted?
     */
    public static boolean delete(Context context, String prefsFileName) {
        boolean isDeleted = false;

        if (context == null || prefsFileName == null || prefsFileName.length() == 0) {

            Log.v(TAG, "delete: Nothing to do");

        } else {
            File prefFile = new File(prefsDirectory(context), prefsFileName + FILE_EXTENSION);
            if (prefFile.exists()) {
                // Commit any changes left
                SharedPreferences.Editor prefs = context.getSharedPreferences(prefsFileName,
                        MODE_PRIVATE).edit();
                if (prefs != null) {
                    prefs.commit();
                    prefs = null;
                }

                isDeleted = prefFile.delete();

                Log.v(TAG, "The prefs file '" + prefFile.getAbsolutePath() + "' was "
                        + (isDeleted ? "" : "not ") + " deleted");

            } else {

                Log.d(TAG, "The prefs file '" + prefFile.getAbsolutePath() + "' was not found");

            }
        }
        return isDeleted;
    }

    /**
     * Rename the preferences file
     * 
     * @return Was the file renamed?
     */
    public static boolean rename(Context context, String oldPrefsFileName, String newPrefsFileName) {
        boolean isRenamed = false;

        if (context == null || oldPrefsFileName == null || oldPrefsFileName.length() == 0
                || newPrefsFileName == null || newPrefsFileName.length() == 0) {

            Log.v(TAG, "rename: Nothing to do");

        } else {
            File newPrefFile = new File(prefsDirectory(context), newPrefsFileName + FILE_EXTENSION);
            if (newPrefFile.exists()) {
                try {

                    Log.v(TAG,
                            "rename: New file already exists: \"" + newPrefFile.getCanonicalPath()
                                    + "\"");

                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            } else {
                File oldPrefFile = new File(prefsDirectory(context), oldPrefsFileName
                        + FILE_EXTENSION);
                if (oldPrefFile.exists()) {
                    // Commit any changes left
                    SharedPreferences.Editor prefs = context.getSharedPreferences(oldPrefsFileName,
                            MODE_PRIVATE).edit();
                    if (prefs != null) {
                        prefs.commit();
                        prefs = null;
                    }

                    isRenamed = oldPrefFile.renameTo(newPrefFile);

                    Log.v(TAG, "The prefs file '" + oldPrefFile.getAbsolutePath() + "' was "
                            + (isRenamed ? "" : "not ") + " renamed");

                } else {

                    Log.d(TAG, "The prefs file '" + oldPrefFile.getAbsolutePath()
                            + "' was not found");

                }
            }
        }
        return isRenamed;
    }
}
