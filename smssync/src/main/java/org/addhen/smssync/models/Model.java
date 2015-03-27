package org.addhen.smssync.models;

import java.io.Serializable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public abstract class Model implements Serializable {

    public Long _id;

    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "Model{" +
                "_id=" + _id +
                '}';
    }
}
