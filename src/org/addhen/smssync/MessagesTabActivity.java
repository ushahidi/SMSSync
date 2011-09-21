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

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class MessagesTabActivity extends TabActivity {
    private TabHost tabHost;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_tab);

        tabHost = getTabHost();
        
        loadTabContent();

        tabHost.setCurrentTab(0);

        // set tab colors
        final int tabSelectedColor = getResources().getColor(R.color.tab_selected);
        final int tabUnselectedColor = getResources().getColor(R.color.tab_unselected);
        setTabColor(tabHost, tabSelectedColor, tabUnselectedColor);

        // set tab colors on tab change as well
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            public void onTabChanged(String arg0) {
                setTabColor(tabHost, tabSelectedColor, tabUnselectedColor);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setTabColor(TabHost tabhost, int selectedColor, int unselectedColor) {
        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            // unselected
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(unselectedColor);
            TextView tv = (TextView)tabhost.getTabWidget().getChildAt(i)
                    .findViewById(android.R.id.title); // Unselected Tabs
            tv.setTextColor(getResources().getColor(R.color.tab_text));
            tv.setPadding(0, 0,0, 0);
        }
        // selected
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
                .setBackgroundColor(selectedColor);
        TextView tv = (TextView)tabhost.getCurrentTabView().findViewById(android.R.id.title);
        tv.setTextColor(getResources().getColor(R.color.tab_text));
        
    }
    
    //load tab content
    public void loadTabContent() {
        //failed messages
        tabHost.addTab(tabHost
                .newTabSpec("pending_messages")
                .setIndicator(getString(R.string.pending_messages),
                        getResources().getDrawable(android.R.drawable.ic_menu_recent_history))
                .setContent(new Intent(MessagesTabActivity.this, PendingMessagesActivity.class)));

        //sent messages
        tabHost.addTab(tabHost
                .newTabSpec("sent_messages")
                .setIndicator(getString(R.string.sent_messages),
                        getResources().getDrawable(android.R.drawable.ic_menu_send))
                .setContent(new Intent(MessagesTabActivity.this, SentMessagesActivity.class)));
    }
}
