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

    public static class Payload {

        public List<Message> messages;

        public String task;

        public String secret;

        public String error;

        public boolean success;

        public static class Message {

            public String to;

            public String message;

            public String uuid;

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
