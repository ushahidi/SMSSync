/**
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 **/

package org.addhen.smssync.adapters;

import static org.addhen.smssync.models.NavDrawerItem.NO_ICON_RES_ID;

import org.addhen.smssync.R;
import org.addhen.smssync.navdrawer.BaseNavDrawerItem;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author eyedol
 */
public class NavDrawerAdapter extends BaseListAdapter<BaseNavDrawerItem> {

    /**
     * @param context
     */
    public NavDrawerAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Widgets widget;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.nav_drawer_item,
                    null);
            widget = new Widgets(convertView);
            convertView.setTag(widget);
        } else {
            widget = (Widgets) convertView.getTag();
        }
        widget.title.setText(this.getItem(position).getTitleRes());

        if (getItem(position).getIconRes() != NO_ICON_RES_ID) {

            widget.title.setCompoundDrawablesWithIntrinsicBounds(getItem(position).getIconRes(), 0,
                    0, 0);

        }

       // if (getItem(position).getCounter() != NO_COUNTER) {
         //   widget.counter.setText(getItem(position).getCounter());
       // }

        if (getItem(position).getCounterBgColor() != null) {
            widget.counter.setBackgroundColor(Color.parseColor(getItem(position)
                    .getCounterBgColor().trim()));
        }

        if (getItem(position).isSelected()) {
            int bottom = convertView.getPaddingBottom();
            int top = convertView.getPaddingTop();
            int right = convertView.getPaddingRight();
            int left = convertView.getPaddingLeft();
            convertView.setBackgroundResource(R.drawable.nav_drawer_selected);
            convertView.setPadding(left, top, right, bottom);
        }

        return convertView;
    }

    /*
     * (non-Javadoc)
     * @see com.ushahidi.android.app.adapters.BaseListAdapter#refresh()
     */
    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    private class Widgets {
        TextView title;
        TextView counter;

        public Widgets(View convertView) {
            title = (TextView) convertView.findViewById(R.id.nav_drawer_title);
            counter = (TextView) convertView.findViewById(R.id.nav_drawer_counter);
        }
    }

}
