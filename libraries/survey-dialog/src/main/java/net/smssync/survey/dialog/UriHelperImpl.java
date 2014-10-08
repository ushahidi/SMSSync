package net.smssync.survey.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class UriHelperImpl implements UriHelper {

    private static final String GOOGLE_FORM_URL
            = "https://docs.google.com/forms/d/1lL4IEksja3r-ClEtCgBma4mB9iT1tcaxSJnriJgW2sM/formResponse";

    private Context mContext;

    public UriHelperImpl(Context context) {
        mContext = context;
    }

    public void goToUrl() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrl()));
        mContext.startActivity(intent);
    }

    @Override
    public String getUrl() {
        return GOOGLE_FORM_URL;
    }
}