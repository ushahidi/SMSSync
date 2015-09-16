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

package org.addhen.smssync.data.entity;

import java.util.List;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SmssyncResponse {

    private static final long serialVersionUID = -6696308336215002660L;

    private Payload payload;

    public Payload getPayload() {
        return payload;
    }

    public class Payload {

        private List<Message> messages;

        private String task;

        private String secret;

        private String error;

        private boolean success;

        public List<Message> getMessages() {
            return messages;
        }

        public String getTask() {
            return task;
        }

        public String getSecret() {
            return secret;
        }

        public String getError() {
            return error;
        }

        public boolean isSuccess() {
            return success;
        }

        @Override
        public String toString() {
            return "Payload{" +
                    "messages=" + messages +
                    ", task='" + task + '\'' +
                    ", secret='" + secret + '\'' +
                    ", error='" + error + '\'' +
                    ", success=" + success +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SmssyncResponse{" +
                "payload=" + payload +
                '}';
    }
}
