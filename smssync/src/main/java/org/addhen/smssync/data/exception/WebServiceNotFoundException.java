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

package org.addhen.smssync.data.exception;

import org.addhen.smssync.data.entity.SyncUrl;

/**
 * Exception thrown by {@link org.addhen.smssync.data.database.WebServiceDatabaseHelper} when a
 * {@link SyncUrl} can't be found from the database.
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class WebServiceNotFoundException extends Exception {

    /**
     * Default exception
     */
    public WebServiceNotFoundException() {
        super();
    }

    /**
     * Initialize the exception with a custom message
     *
     * @param message The message be shown when the exception is thrown
     */
    public WebServiceNotFoundException(final String message) {
        super(message);
    }

    /**
     * Initialize the exception with a custom message and the cause of the exception
     *
     * @param message The message to be shown when the exception is thrown
     * @param cause   The cause of the exception
     */
    public WebServiceNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Initialize the exception with a the cause of the exception
     *
     * @param cause The cause of the exception
     */
    public WebServiceNotFoundException(final Throwable cause) {
        super(cause);
    }
}
