package net.smssync.survey.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UriHelperImpl implements UriHelper {

    private static final String GOOGLE_SPREAD_SHEET_URL
            = "https://play.google.com/store/apps/details?id=";

    public UriHelperImpl() {
    }

    public void goToUri(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, getUri());
        context.startActivity(intent);
    }

    @Override
    public Uri getUri() {
        return Uri.parse(GOOGLE_SPREAD_SHEET_URL);
    }
}