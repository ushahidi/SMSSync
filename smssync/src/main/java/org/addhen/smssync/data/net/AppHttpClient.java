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

package org.addhen.smssync.data.net;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import java.io.File;

/**
 * A Singleton class for accessing RequestQueue instance. It instantiates the
 * RequestQueue using the application context. This way possible memory leaks
 * are avoided in case user passes in an Activity's context.
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class AppHttpClient {

    private Context mContext;

    private RequestQueue mRequestQueue;

    private static AppHttpClient mAppHttpClient;

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "smssync-volley";

    private AppHttpClient(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized AppHttpClient getInstance(Context context) {
        if (mAppHttpClient == null) {
            mAppHttpClient = new AppHttpClient(context);
        }
        return mAppHttpClient;
    }

    /**
     * Creates an instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @return A started {@link RequestQueue} instance.
     */
    @SuppressWarnings("deprecation")
    public RequestQueue getRequestQueue() {
        File cacheDir = new File(mContext.getCacheDir(), DEFAULT_CACHE_DIR);
        if (mRequestQueue == null) {
            StringBuilder userAgent = new StringBuilder("SMSSync-Android/");
            userAgent.append("v");
            try {
                // Add version name to user agent
                String packageName = mContext.getPackageName();
                PackageInfo packageInfo = mContext.getPackageManager()
                        .getPackageInfo(packageName, 0);
                userAgent.append(packageInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                // Ignore exception for now
            }

            HttpStack stack;
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent.toString()));
            }
            Network network = new BasicNetwork(stack);

            // getApplicationContext() is key, it keeps the app from leaking the
            // Activity or BroadcastReceiver if someone should pass one in.
            mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir), network);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    /**
     * Make a Typed base request. Regular string and Json requests
     *
     * @param req The request type
     * @param <T> The Typed request
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
