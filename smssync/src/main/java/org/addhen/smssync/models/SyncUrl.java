package org.addhen.smssync.models;

import org.addhen.smssync.net.SyncScheme;

import nl.qbusict.cupboard.annotation.Column;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class SyncUrl extends Model {

    @Column("title")
    private String title;

    @Column("keywords")
    private String keywords;

    @Column("url")
    private String url;

    @Column("secret")
    private String secret;

    @Column("syncscheme")
    private String syncScheme;

    @Column("status")
    private Status status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public SyncScheme getSyncScheme() {
        return new SyncScheme(syncScheme);
    }

    public void setSyncScheme(SyncScheme syncScheme) {
        this.syncScheme = syncScheme.toJSONString();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        ENABLED, DISABLED;
    }

    @Override
    public String toString() {
        return "SyncUrl{" +
                "title='" + title + '\'' +
                ", keywords='" + keywords + '\'' +
                ", url='" + url + '\'' +
                ", secret='" + secret + '\'' +
                ", syncScheme=" + syncScheme +
                ", status=" + status +
                '}';
    }
}
