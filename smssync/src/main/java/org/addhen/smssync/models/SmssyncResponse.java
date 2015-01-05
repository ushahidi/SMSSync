package org.addhen.smssync.models;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SmssyncResponse implements Serializable {

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

        public class Message {

            private String to;

            private String message;

            private String uuid;

            public String getTo() {
                return to;
            }

            public String getMessage() {
                return message;
            }

            public String getUuid() {
                return uuid;
            }

            @Override
            public String toString() {
                return "Message{" +
                        "to='" + to + '\'' +
                        ", message='" + message + '\'' +
                        ", uuid='" + uuid + '\'' +
                        '}';
            }

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
