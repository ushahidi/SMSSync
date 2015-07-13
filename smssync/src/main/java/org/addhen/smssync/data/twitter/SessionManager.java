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

package org.addhen.smssync.data.twitter;

import java.util.Map;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface SessionManager<T extends Session> {

    /**
     * @return the active session, restoring saved session if available
     */
    T getActiveSession();

    /**
     * Sets the active session.
     */
    void setActiveSession(T session);

    /**
     * Clears the active session.
     */
    void clearActiveSession();

    /**
     * @return the session associated with the id.
     */
    T getSession(long id);

    /**
     * Sets the session to associate with the id. If there is no active session, this session also
     * becomes the active session.
     */
    void setSession(long id, T session);

    /**
     * Clears the session associated with the id.
     */
    void clearSession(long id);

    /**
     * @return the session map containing all managed sessions
     */
    Map<Long, T> getSessionMap();
}
