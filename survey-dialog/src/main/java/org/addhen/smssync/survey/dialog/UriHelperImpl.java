package org.addhen.smssync.survey.dialog;

import android.net.Uri;

public class UriHelperImpl implements UriHelper {

    private static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=";

    public UriHelperImpl() {
    }

    static Uri getGooglePlay(String packageName) {
        return packageName == null ? null : Uri.parse(GOOGLE_PLAY + packageName);
    }

    @Override
    public Uri getUri(String packageName) {
        return packageName == null ? null : Uri.parse(GOOGLE_PLAY + packageName);
    }
}