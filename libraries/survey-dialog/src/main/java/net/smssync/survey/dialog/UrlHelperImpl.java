package net.smssync.survey.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UrlHelperImpl implements UrlHelper {

    private static final String GOOGLE_FORM_URL
            = "https://docs.google.com/forms/d/1lL4IEksja3r-ClEtCgBma4mB9iT1tcaxSJnriJgW2sM/formResponse";


    public UrlHelperImpl() {

    }

    public void goToUrl(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrl()));
        context.startActivity(intent);
    }

    @Override
    public String getUrl() {
        return GOOGLE_FORM_URL;
    }
}