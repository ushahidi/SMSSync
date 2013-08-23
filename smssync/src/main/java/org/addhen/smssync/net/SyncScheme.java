/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
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
 *****************************************************************************/
package org.addhen.smssync.net;


/**
 *
 * Class: SyncScheme
 * Description: Specifies a synchronization scheme that
 *              formats messages in the way that the sever expects them.
 * Author: Salama A.B. <devaksal@gmail.com>
 *
 */
public class SyncScheme {

    public static enum SyncMethod { POST, PUT };
    public static enum SyncDataFormat { URLEncoded, JSON, XML, YAML};
    public static enum SyncDataKey { SECRET, FROM, MESSAGE, SENT_TIMESTAMP, MESSAGE_ID, SENT_TO };


    private SyncMethod method;
    private SyncDataFormat format;

    private String keySecret;
    private String keyFrom;
    private String keyMessage;
    private String keySentTimeStamp;
    private String keySentTo;
    private String keyMessageID;


    public SyncScheme(){
        init(
                SyncMethod.POST,
                SyncDataFormat.URLEncoded,
                "secret","from","message",
                "message_id","sent_timestamp","sent_to");
    }

    public SyncScheme(SyncMethod method, SyncDataFormat dataFormat){
        init(
                method,
                dataFormat,
                "secret","from",
                "message","message_id",
                "sent_timestamp","sent_to");
    }

    /**
     * Initialize sync scheme with custom method, data format and keys
     *
     * @param method
     * @param dataFormat
     * @param kSecret
     * @param kFrom
     * @param kMessage
     * @param keyMessageID
     * @param kSentTimestamp
     * @param kSentTo
     */
    public void init(SyncMethod method, SyncDataFormat dataFormat,
                            String kSecret, String kFrom, String kMessage,
                            String keyMessageID, String kSentTimestamp,
                            String kSentTo){
        this.method = method;
        this.format = dataFormat;

        this.keySecret = kSecret;
        this.keyFrom = kFrom;
        this.keySentTimeStamp = kSentTimestamp;
        this.keyMessage = kMessage;
        this.keySentTo = kSentTo;
        this.keyMessageID = keyMessageID;
    }

    /**
     * Get the HTTP method the server is expecting
     * @return Http method; POST or PUT
     */
    public SyncMethod getMethod(){
        return method;
    }

    /**
     * Get the data format the server is expecting
     * @return serialization format; JSON, XML, YAML, etc.
     */
    public SyncDataFormat getDataFormat(){
        return format;
    }

    /**
     * Get the mime type of expected data format
     * @return
     */
    public String getContentType(){
        switch (format){
            case JSON:
                return "application/json";
            case XML:
                return "application/xml";
            case YAML:
                return "application/yaml";
            default:
                return "application/x-www-form-urlencoded";
        }
    }

    /**
     * Get server expected key for particular data item
     * @param key
     * @return
     */
    public String getKey(SyncDataKey key){
        switch (key){
            case SECRET:
                return keySecret;
            case FROM:
                return keyFrom;
            case MESSAGE:
                return keyMessage;
            case SENT_TIMESTAMP:
                return keySentTimeStamp;
            case MESSAGE_ID:
                return keyMessageID;
            case SENT_TO:
                return keySentTo;
            default:
                return "value";
        }
    }

    @Override
    public String toString() {
        return "SyncScheme {" +
                "method:" + method.toString() +
                ", dataFormat:" + format.toString() +
                ", keys: [" + keyFrom + "," + keySecret + "," + keyMessage + "," + keySentTo + "," +
                keySentTimeStamp + "," + keyMessageID + "] " +
                "}";
    }

}
