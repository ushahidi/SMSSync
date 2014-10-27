package org.addhen.smssync;

import net.smssync.survey.dialog.UrlHelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UrlHelperImpl implements UrlHelper {

    private String mUrl;

    public UrlHelperImpl(String url) {
        mUrl = url;
    }

    public void goToUrl(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrl()));
        context.startActivity(intent);
    }

    @Override
    public String getUrl() {
        return mUrl;
    }
}