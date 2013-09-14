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

package org.addhen.smssync.fragments;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.adapters.FilterAdapter;
import org.addhen.smssync.adapters.LogAdapter;
import org.addhen.smssync.models.Filter;
import org.addhen.smssync.models.Log;
import org.addhen.smssync.views.LogView;
import org.addhen.smssync.views.WhitelistView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

public class LogFragment extends BaseListFragment<LogView, Log, LogAdapter> implements
        View.OnClickListener, AdapterView.OnItemClickListener {

    Log model;
    public LogFragment() {
        super(LogView.class, LogAdapter.class, R.layout.list_logs,
                R.menu.log_menu, android.R.id.list);
        model = new Log();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(this);

        view.enableLogs.setChecked(Prefs.enableWhitelist);
        view.enableLogs.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
