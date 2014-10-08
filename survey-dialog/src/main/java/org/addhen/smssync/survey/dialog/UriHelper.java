package org.addhen.smssync.survey.dialog;

import android.net.Uri;

final class UriHelper {

    private static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=";

    private UriHelper() {
    }

    static Uri getGooglePlay(String packageName) {
        return packageName == null ? null : Uri.parse(GOOGLE_PLAY + packageName);
    }

}