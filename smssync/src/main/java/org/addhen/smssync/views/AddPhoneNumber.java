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

package org.addhen.smssync.views;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.models.Filter;
import org.addhen.smssync.models.Filter.Status;

import android.view.View;
import android.widget.EditText;

public class AddPhoneNumber {

    public EditText phoneNumber;

    Filter filter;

    /**
     * Handles views for the add dialog box
     */
    public AddPhoneNumber(final View dialogViews) {
        phoneNumber = (EditText) dialogViews.findViewById(R.id.phone_number);

    }

    /**
     * Add a new phone number
     *
     * @return boolean
     */
    public boolean add(Status status) {
        filter = new Filter();
        filter.setPhoneNumber(phoneNumber.getText().toString());
        filter.setStatus(status);
        save(filter);
        return true;
    }

    /**
     * Update an existing phone number
     *
     * @return boolean
     */
    public boolean update(Long id, Status status) {
        filter = new Filter();
        filter.setId(id);
        filter.setPhoneNumber(phoneNumber.getText().toString());
        filter.setStatus(status);
        save(filter);
        return true;
    }

    private void save(Filter filter) {
        App.getDatabaseInstance().getFilterInstance()
                .put(filter, new BaseDatabseHelper.DatabaseCallback<Void>() {
                    @Override
                    public void onFinished(Void result) {
                        // Do nothig
                    }

                    @Override
                    public void onError(Exception exception) {
                        // Do nothing
                    }
                });
    }
}
