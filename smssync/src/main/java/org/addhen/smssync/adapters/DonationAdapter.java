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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.addhen.smssync.R;
import org.addhen.smssync.models.Donation;
import org.addhen.smssync.models.Log;

/**
 * Logs adapter
 */
public class DonationAdapter extends BaseListAdapter<Donation> {

    public DonationAdapter(Context context) {
        super(context);
    }

    @Override
    public void refresh() {

    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Widgets widgets;
        if (view == null) {
            view = inflater.inflate(R.layout.list_donation_amounts, null);
            widgets = new Widgets(view);
            view.setTag(widgets);
        } else {
            widgets = (Widgets) view.getTag();
        }

        // initialize view with content
        widgets.title.setText(getItem(position).getSkuDetails().getTitle());
        widgets.amount.setText(getItem(position).getSkuDetails().getTitle());

        return view;
    }

    public class Widgets extends org.addhen.smssync.views.View {

        TextView title;

        TextView amount;

        public Widgets(View convertView) {
            super(convertView);
            title = (TextView) convertView
                    .findViewById(R.id.donation_level_title);

            amount = (TextView) convertView.findViewById(R.id.donation_amount);
        }

    }
}
