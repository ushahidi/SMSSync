/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
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
 *****************************************************************************/

package org.addhen.smssync.activities;

import org.addhen.smssync.R;
import org.addhen.smssync.adapters.NavDrawerAdapter;
import org.addhen.smssync.navdrawer.BaseNavDrawerItem;
import org.addhen.smssync.navdrawer.PendingMessagesNavDrawerItem;
import org.addhen.smssync.navdrawer.SentMessagesNavDrawerItem;
import org.addhen.smssync.navdrawer.SyncUrlNavDrawerItem;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Objects;
import org.addhen.smssync.views.View;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * BaseActivity Add shared functionality that exists between all Activities
 */
public abstract class BaseActivity<V extends View> extends SherlockFragmentActivity {

    /**
     * Layout resource id
     */
    protected final int layout;

    /**
     * Menu resource id
     */
    protected final int menu;

    /**
     * View class
     */
    protected final Class<V> viewClass;

    /**
     * View
     */
    protected V view;

    protected ActionBar actionBar;

    protected NavDrawerAdapter navDrawerAdapter;

    protected ActionBarDrawerToggle drawerToggle;

    protected final int drawerLayoutId;

    protected DrawerLayout drawerLayout;

    protected ListView listView;

    /**
     * ListView resource id
     */
    protected final int listViewId;

    /**
     * BaseActivity
     * 
     * @param view View class
     * @param layout layout resource id
     * @param menu menu resource id
     * @param drawerLayoutId resource id for the drawerLayout
     * @param listViewId the resource id for the list view
     */
    protected BaseActivity(Class<V> view, int layout, int menu, int drawerLayoutId, int listViewId) {

        this.viewClass = view;
        this.layout = layout;
        this.menu = menu;
        this.drawerLayoutId = drawerLayoutId;
        this.listViewId = listViewId;
    }

    /**
     * BaseActivity
     * 
     * @param view View class
     * @param layout layout resource id
     * @param menu menu resource id
     */
    protected BaseActivity(Class<V> view, int layout, int menu) {
        this(view, layout, menu, 0, 0);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");

        if (layout != 0) {
            setContentView(layout);
        }

        if (drawerLayoutId != 0)
            drawerLayout = (DrawerLayout) findViewById(drawerLayoutId);

        if (listViewId != 0)
            listView = (ListView) findViewById(listViewId);

        view = Objects.createInstance(viewClass, Activity.class, this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (drawerLayout != null) {

            // default deployment is not set

            createNavDrawer();

            if (savedInstanceState == null) {
                selectItem(0);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        log("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy");
    }

    protected void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            log("onKeyDown KEYCODE_BACK");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log("onActivityResult");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.menu != 0) {
            getSupportMenuInflater().inflate(this.menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (drawerLayout != null) {

                    if (drawerLayout.isDrawerOpen(listView)) {
                        drawerLayout.closeDrawer(listView);
                    } else {
                        drawerLayout.openDrawer(listView);
                    }

                } else {
                    finish();
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        return super.onContextItemSelected(item);
    }

    public void openActivityOrFragment(Intent intent) {
        // Default implementation simply calls startActivity
        startActivity(intent);
    }

    protected void createNavDrawer() {
        initNavDrawer();
        navDrawerAdapter.addItem(new PendingMessagesNavDrawerItem(
                getString(R.string.pending_messages),
                R.drawable.pending, BaseActivity.this));

        navDrawerAdapter.addItem(new SentMessagesNavDrawerItem(
                getString(R.string.sent_messages),
                R.drawable.sent, BaseActivity.this));

        navDrawerAdapter.addItem(new SyncUrlNavDrawerItem(getString(R.string.sync_url),
                R.drawable.sync_url, BaseActivity.this));

    }

    private class NavDrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
            selectItem(position);
            view.getFocusables(position);
            view.setSelected(true);
        }

    }

    protected void selectItem(int position) {
        if (navDrawerAdapter != null) {
            BaseNavDrawerItem item = navDrawerAdapter.getItem(position);

            // Perform selection only if item is not selected
            if (!item.isSelected()) {
                item.selectItem();
            }

            // update selected item and title, then close the drawer
            listView.setItemChecked(position, true);
            drawerLayout.closeDrawer(listView);

        }
    }

    private void initNavDrawer() {
        navDrawerAdapter = new NavDrawerAdapter(this);
        listView.setOnItemClickListener(new NavDrawerItemClickListener());
        listView.setAdapter(navDrawerAdapter);

        if (drawerLayout != null) {
            drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                    GravityCompat.START);
            // ActionBarDrawerToggle ties together the the proper interactions
            // between the sliding drawer and the action bar app icon
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer,
                    R.string.open, R.string.close) {
                public void onDrawerClosed(android.view.View view) {
                    getSupportActionBar().setTitle(getTitle());
                    super.onDrawerClosed(view);
                }

                public void onDrawerOpened(android.view.View drawerView) {
                    getSupportActionBar().setTitle(getTitle());

                    super.onDrawerOpened(drawerView);
                }
            };
        }

        drawerLayout.setDrawerListener(drawerToggle);

    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (drawerToggle != null)
            drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        if (drawerToggle != null)
            drawerToggle.onConfigurationChanged(newConfig);
    }

    protected EditText findEditTextById(int id) {
        return (EditText) findViewById(id);
    }

    protected ListView findListViewById(int id) {
        return (ListView) findViewById(id);
    }

    protected TextView findTextViewById(int id) {
        return (TextView) findViewById(id);
    }

    protected Spinner findSpinnerById(int id) {
        return (Spinner) findViewById(id);
    }

    protected TimePicker findTimePickerById(int id) {
        return (TimePicker) findViewById(id);
    }

    protected Button findButtonById(int id) {
        return (Button) findViewById(id);
    }

    protected ImageView findImageViewById(int id) {
        return (ImageView) findViewById(id);
    }

    protected void log(String message) {
        Logger.log(getClass().getName(), message);
    }

    protected void log(String format, Object... args) {

        Logger.log(getClass().getName(), String.format(format, args));
    }

    protected void log(String message, Exception ex) {

        Logger.log(getClass().getName(), message, ex);
    }

    protected void toastLong(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void toastLong(int message) {
        Toast.makeText(this, getText(message), Toast.LENGTH_LONG).show();
    }

    protected void toastLong(String format, Object... args) {
        Toast.makeText(this, String.format(format, args), Toast.LENGTH_LONG)
                .show();
    }

    protected void toastLong(CharSequence message) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
    }

    protected void toastShort(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(String format, Object... args) {
        Toast.makeText(this, String.format(format, args), Toast.LENGTH_SHORT)
                .show();
    }

    protected void toastShort(int message) {
        Toast.makeText(this, getText(message), Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(CharSequence message) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show();
    }

}
