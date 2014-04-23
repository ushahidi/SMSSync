package org.addhen.smssync.models;

/**
 * Created by kkalfas@soldevelo.com on 16.04.14.
 */
public class MessageResult {

    private String id;
    private String type;
    private int resultCode;
    private String message;

    public MessageResult(String id, String type, int resultCode, String message) {
        this.id = id;
        this.type = type;
        this.resultCode = resultCode;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
