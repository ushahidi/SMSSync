package org.addhen.smssync.adapters;

import org.addhen.smssync.R;
import org.addhen.smssync.models.Log;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Logs adapter
 */
public class LogAdapter extends BaseListAdapter<Log> {

    public LogAdapter(Context context) {
        super(context);
    }

    @Override
    public void refresh() {

    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Widgets widgets;
        if (view == null) {
            view = inflater.inflate(R.layout.list_log_item, null);
            widgets = new Widgets(view);
            view.setTag(widgets);
        } else {
            widgets = (Widgets) view.getTag();
        }

        // initialize view with content
        widgets.message.setText(getItem(position).getTimestamp());
        widgets.timestamp.setText(getItem(position)
                .getTimestamp());

        return view;
    }

    public class Widgets extends org.addhen.smssync.views.View implements
            View.OnClickListener {

        TextView timestamp;

        TextView message;

        public Widgets(View convertView) {
            super(convertView);
            timestamp = (TextView) convertView
                    .findViewById(R.id.log_timestamp);
            message = (TextView) convertView
                    .findViewById(R.id.log_message);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
