/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/

package org.addhen.smssync.adapters;

import org.addhen.smssync.R;
import org.addhen.smssync.navdrawer.BaseNavDrawerItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static org.addhen.smssync.models.NavDrawerItem.NO_ICON_RES_ID;

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
        widget.title.setText(getItem(position).getTitleRes());

        if (getItem(position).getIconRes() != NO_ICON_RES_ID) {

            widget.title.setCompoundDrawablesWithIntrinsicBounds(getItem(position).getIconRes(), 0,
                    0, 0);

        }

        // set counter
        if (getItem(position).getCounter() > 0) {
            widget.counter.setText(String.valueOf(getItem(position).getCounter()));
        }

        return convertView;
    }

    /*
     * (non-Javadoc)
     * @see com.ushahidi.android.app.adapters.BaseListAdapter#refresh()
     */
    @Override
    public void refresh() {
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
