/** 
 ** Copyright (c) 2010 - 2011 Ushahidi Inc
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

package org.addhen.smssync;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MessagesTabActivity extends ActivityGroup {

    private TabHost mTabHost;

    private void setupTabHost() {
        mTabHost = (TabHost)findViewById(R.id.tabhost);
        mTabHost.setup(getLocalActivityManager());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // construct the tabhost
        setContentView(R.layout.messages_tab);

        setupTabHost();

        mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

        setupTab(new TextView(this), getString(R.string.pending_messages), new Intent(
                MessagesTabActivity.this, PendingMessagesActivity.class));
        setupTab(new TextView(this), getString(R.string.sent_messages), new Intent(
                MessagesTabActivity.this, SentMessagesActivity.class));
    }

    private void setupTab(final View view, final String tag, final Intent intent) {
        View tabview = createTabView(mTabHost.getContext(), tag);

        TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
        mTabHost.addTab(setContent);

    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        TextView tv = (TextView)view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }
}
