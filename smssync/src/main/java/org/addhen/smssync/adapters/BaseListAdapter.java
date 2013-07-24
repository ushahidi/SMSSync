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

package org.addhen.smssync.adapters;

import java.util.ArrayList;
import java.util.List;

import org.addhen.smssync.models.Model;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

/**
 * BaseListAdapter Base class for all list adapters for a specific BaseModel
 * class
 * 
 * @param <M> Model class
 */
public abstract class BaseListAdapter<M extends Model> extends BaseAdapter {

    protected final Context context;
    protected final LayoutInflater inflater;
    protected final List<M> items = new ArrayList<M>();

    public BaseListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    public int getCount() {
        return this.items.size();
    }

    public void setItems(List<M> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(M item) {
        this.items.add(item);
        notifyDataSetChanged();
    }

    public M getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int indexOf(M item) {
        return items.indexOf(item);
    }

    public void clearItems() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        this.items.remove(position);
        notifyDataSetChanged();
    }

    public void removeItem(M item) {
        this.items.remove(item);
        notifyDataSetChanged();
    }

    /**
     * Set the date of the message.
     * 
     * @param String messageDate - The timestamp of the message. To be changed
     *            into human readable.
     * @return void
     */
    protected String formatDate(String messageDate) {
        try {
            return Util.formatDateTime(Long.parseLong(messageDate),
                    "MMM dd, yyyy 'at' hh:mm a");

        } catch (NumberFormatException e) {
            return messageDate;
        }
    }

    public abstract void refresh();

    protected void log(String message) {
        Logger.log(getClass().getName(), message);
    }

    protected void log(String format, Object... args) {
        Logger.log(getClass().getName(), format, args);
    }

    protected void log(String message, Exception ex) {
        Logger.log(getClass().getName(), message, ex);
    }

}
