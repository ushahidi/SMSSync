/** 
 ** Copyright (c) 2010 Ushahidi Inc
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

import org.addhen.smssync.data.Database;
import org.addhen.smssync.net.MainHttpClient;

import android.app.Application;

/**
 * This class is for maintaining global application state.
 * 
 * @author eyedol
 */
public class MainApplication extends Application {

    public static final String TAG = "SmsSyncApplication";

    public static Database mDb;

    public static MainHttpClient mApi;
    

    @Override
    public void onCreate() {
        super.onCreate();

        // Open database connection when the application starts.
        mDb = new Database(this);
        mDb.open();
        mApi = new MainHttpClient();
    }

    @Override
    public void onTerminate() {

        // Close the database when the application terminates.
        mDb.close();
        super.onTerminate();
    }

}
