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

package org.addhen.smssync.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;

import org.addhen.smssync.R;
import org.addhen.smssync.Settings;
import org.addhen.smssync.views.MainView;

/**
 * @author eyedol
 */
public class MainActivity extends BaseActivity<MainView> {

    public MainActivity() {
        super(MainView.class, R.layout.main_activity, R.menu.main_activity, R.id.drawer_layout,
                R.id.left_drawer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Context Menu Stuff
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        log("onOptionsItemSelected()");
        Intent intent;
        if (item.getItemId() == R.id.settings) {
            intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
