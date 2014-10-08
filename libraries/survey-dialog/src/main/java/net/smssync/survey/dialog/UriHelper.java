package net.smssync.survey.dialog;

import android.content.Context;
import android.net.Uri;

/**
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface UriHelper {

    Uri getUri();

    void goToUri(Context context);
}
