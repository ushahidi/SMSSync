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

package org.addhen.smssync.domain.entity;

import org.addhen.smssync.models.Model;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SyncUrlEntity extends Model {

    private String title;

    private String keywords;

    private String url;

    private String secret;

    private SyncUrlEntity syncScheme;

    private Status status;

    @Override
    public String toString() {
        return "SyncUrlEntity{" +
                "title='" + title + '\'' +
                ", keywords='" + keywords + '\'' +
                ", url='" + url + '\'' +
                ", secret='" + secret + '\'' +
                ", syncScheme=" + syncScheme +
                ", status=" + status +
                '}';
    }

    public enum Status {
        ENABLED, DISABLED
    }
}
