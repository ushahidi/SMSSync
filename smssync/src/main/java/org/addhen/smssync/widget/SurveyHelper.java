package org.addhen.smssync.widget;

import org.addhen.smssync.survey.dialog.UriHelper;

import android.net.Uri;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SurveyHelper implements UriHelper {

    private static final String GOOGLE_SPREAD_SHEET = "";
    @Override
    public Uri getUri(String packageName) {
        return null;
    }

    public boolean postToGoogleDocs() {
        return false;
    }
}
