/** 
 ** Copyright (c) 2010 Ushahidi Inc
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
 **/

package org.addhen.smssync;

import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class ListMessagesAdapter extends BaseAdapter {

    private Context iContext;

    private List<ListMessagesText> iItems = new ArrayList<ListMessagesText>();

    public ListMessagesAdapter(Context context) {
        iContext = context;
    }

    public void addItem(ListMessagesText it) {
        iItems.add(it);
    }

    public void removeItems() {
        iItems.clear();
    }

    public void removetItemAt(int location) {
        iItems.remove(location);
    }

    public void setListItems(List<ListMessagesText> lit) {
        iItems = lit;
    }

    public int getCount() {
        return iItems.size();
    }

    public Object getItem(int position) {
        return iItems.get(position);
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isSelectable(int position) {
        return iItems.get(position).isSelectable();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ListMessagesTextView iTv;
        if (convertView == null) {
            iTv = new ListMessagesTextView(iContext, iItems.get(position));

        } else {

            iTv = (ListMessagesTextView)convertView;
            iTv.setMessageFrom(iItems.get(position).getMessageFrom());
            iTv.setMessageBody(iItems.get(position).getMessageBody());
            iTv.setMessageDate(iItems.get(position).getMessageDate());

        }
        return iTv;
    }

}
