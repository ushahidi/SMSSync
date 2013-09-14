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

package org.addhen.smssync.models;

/**
 * Navigation Drawer menu item
 */
public class NavDrawerItem extends Model {

    public static int NO_ICON_RES_ID = -1;

    // Resource id for the title string
    protected String mTitle;

    // Resource id for the icon drawable
    protected int mIconRes;

    // counter
    protected int mCounter;

    /**
     * Creates a NavDrawerItem with the title, string resource id
     */
    public NavDrawerItem(String title, int iconRes) {
        mTitle = title;
        mIconRes = iconRes;
        mCounter = 0;
    }

    public String toString() {
        return "NavDrawerItem{title:" + mTitle + ", iconRes" + mIconRes + ", counter:" + mCounter
                + "}";
    }

    /**
     * The resource id to use for the menu item's title
     */
    public String getTitleRes() {
        return mTitle;
    }

    /**
     * The resource id to use for the menu item's icon
     */
    public int getIconRes() {
        return mIconRes;
    }

    /**
     * Get the counter attached to a
     *
     * @return int
     */
    public int getCounter() {
        return mCounter;
    }

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public boolean save() {
        return false;
    }

}
