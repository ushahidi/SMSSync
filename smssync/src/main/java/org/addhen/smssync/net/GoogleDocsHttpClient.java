package org.addhen.smssync.net;

import com.squareup.otto.Produce;

import org.addhen.smssync.R;
import org.addhen.smssync.util.Util;

import android.content.Context;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Post email address to a google spreadsheet
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class GoogleDocsHttpClient extends MainHttpClient {

    public GoogleDocsHttpClient(String url, Context context) {
        super(url, context);
    }

    public boolean postToGoogleDocs(String email) {
        addParam("entry.1221859611", email);
        try {
            setMethod(HttpMethod.POST);
            execute();
        } catch (Exception e) {
            log("Request failed", e);
            setClientError("Request failed. " + e.getMessage());
        }

        final int statusCode = getResponse().code();

        if (statusCode != 200 && statusCode != 201) {
            setServerError("bad http return code", statusCode);
            return false;
        }

        return true;
    }

    public void setClientError(String error) {
        log("Client error " + error);
        Resources res = context.getResources();
        final String clientError = String.format(Locale.getDefault(), "%s",
                res.getString(R.string.sending_failed_custom_error, error));
        Util.logActivities(context, clientError);
    }

    public void setServerError(String error, int statusCode) {
        log("Server error " + error);
        Resources res = context.getResources();
        final String serverError = String
                .format("%s %s ", res.getString(R.string.sending_failed_custom_error, error),
                        res.getString(R.string.sending_failed_http_code, statusCode));
        Util.logActivities(context, serverError);
    }

    @Produce
    public boolean reloadLog() {
        return true;
    }
}
