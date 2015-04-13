/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.activities;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import net.smssync.survey.dialog.AppRate;
import net.smssync.survey.dialog.OnClickButtonListener;
import net.smssync.survey.dialog.UrlHelper;

import org.addhen.smssync.BuildConfig;
import org.addhen.smssync.R;
import org.addhen.smssync.Settings;
import org.addhen.smssync.UrlHelperImpl;
import org.addhen.smssync.net.GoogleDocsHttpClient;
import org.addhen.smssync.util.Util;
import org.addhen.smssync.views.MainView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author eyedol
 */
public class MainActivity extends BaseActivity<MainView> implements OnClickButtonListener {

    private AutoCompleteTextView mEmailAddress;

    public MainActivity() {
        super(MainView.class, R.layout.main_activity, R.menu.main_activity, R.id.drawer_layout,
                R.id.left_drawer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initSurveyDialog() {
        android.view.View root = getLayoutInflater().inflate(R.layout.survey_dialog_form, null);

        mEmailAddress = (AutoCompleteTextView) root.findViewById(R.id.editText);

        // Suggest email address as user types.
        final Account[] accounts = AccountManager.get(this).getAccounts();
        final Set<String> emailSet = new HashSet<>();
        for (Account account : accounts) {
            if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
                emailSet.add(account.name);
            }
        }
        List<String> emails = new ArrayList<>(emailSet);
        mEmailAddress.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                        emails)
        );

        // Custom view
        AppRate.with(this)
                .setInstallDays(2)
                .setLaunchTimes(2)
                .setRemindInterval(2)
                .setShowNeutralButton(true)
                .setView(root)
                .setOnClickButtonListener(this)
                .monitor();

        // Show a dialog if meets conditions.
        AppRate.showRateDialogIfMeetsConditions(this);
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

    @Override
    public void onClickButton(int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            final String email = mEmailAddress.getText().toString();
            if (Util.validateEmail(email)) {
                PostToGoogleTask postToGoogleTask = new PostToGoogleTask();
                postToGoogleTask.execute(email);
            } else {
                toastLong(R.string.in_valid_email_address);
            }
        }

    }

    public class PostToGoogleTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... emails) {
            final UrlHelper uriHelper = new UrlHelperImpl(BuildConfig.GOOGLE_FORM_URL);
            final GoogleDocsHttpClient client = new GoogleDocsHttpClient(uriHelper.getUrl(),
                    MainActivity.this);
            client.postToGoogleDocs(emails[0]);
            return null;
        }

    }

}
