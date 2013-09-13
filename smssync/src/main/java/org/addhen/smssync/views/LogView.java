package org.addhen.smssync.views;

import org.addhen.smssync.R;

import android.app.Activity;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 *
 */
public class LogView extends View {

    @Widget(R.id.start_logs)
    public CheckBox enableLogs;

    @Widget(R.id.loading_list_progress)
    public ProgressBar listLoadingProgress;

    @Widget(android.R.id.empty)
    public TextView emptyView;

    @Widget(R.id.phone_status_label)
    public TextView phoneStatusLable;

    @Widget(R.id.battery_level_status)
    public TextView batteryLevelStatus;

    @Widget(R.id.can_ping_server_label)
    public TextView canPingServer;

    public LogView(Activity activity) {
        super(activity);
    }
}
