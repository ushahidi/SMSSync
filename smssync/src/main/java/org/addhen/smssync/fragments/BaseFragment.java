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

import com.google.analytics.tracking.android.EasyTracker;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.util.LogUtil;
import org.addhen.smssync.util.Logger;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Toast;

public class BaseFragment extends SherlockFragment {

    /**
     * Menu resource id
     */
    protected final int menu;

    /**
     * BaseActivity
     *
     * @param menu menu resource id
     */
    protected BaseFragment(int menu) {

        this.menu = menu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");

        setHasOptionsMenu(true);
        EasyTracker.getInstance().setContext(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (this.menu != 0) {
            inflater.inflate(this.menu, menu);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        log("onStart");
        EasyTracker.getInstance().activityStart(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");
        EasyTracker.getInstance().activityStop(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        return super.onContextItemSelected(item);
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    protected void toastLong(int message) {
        Toast.makeText(getActivity(), getText(message), Toast.LENGTH_LONG)
                .show();
    }

    protected void toastLong(String format, Object... args) {
        Toast.makeText(getActivity(), String.format(format, args),
                Toast.LENGTH_LONG).show();
    }

    protected void toastLong(CharSequence message) {
        Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_LONG)
                .show();
    }

    protected void toastShort(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(String format, Object... args) {
        Toast.makeText(getActivity(), String.format(format, args),
                Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(int message) {
        Toast.makeText(getActivity(), getActivity().getString(message),
                Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(CharSequence message) {
        Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_SHORT)
                .show();
    }

    protected void logActivities(String message) {
        if (Prefs.enableLog) {
            new LogUtil(DateFormat.getDateFormatOrder(getActivity())).appendAndClose(message);
        }
    }

}
