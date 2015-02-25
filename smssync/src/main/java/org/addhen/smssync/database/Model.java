package org.addhen.smssync.database;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public abstract class Model  {

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
