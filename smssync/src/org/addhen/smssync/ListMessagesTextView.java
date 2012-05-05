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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListMessagesTextView extends LinearLayout {
    private TextView messagesFrom;

    private TextView messagesBody;

    private TextView messagesDate;

    private float fontSize = 13.5f;

    private LinearLayout textLayout;

    private LinearLayout msgFromAndDateLayout;

    public ListMessagesTextView(Context context, ListMessagesText listText) {
        super(context);

        this.setOrientation(VERTICAL);
        this.initComponent(context, listText);
    }

    public void initComponent(Context context, ListMessagesText listText) {
        textLayout = new LinearLayout(context);
        msgFromAndDateLayout = new LinearLayout(context);

        textLayout.setOrientation(VERTICAL);
        textLayout.setPadding(0, 2, 0, 2);

        msgFromAndDateLayout.setOrientation(HORIZONTAL);
        msgFromAndDateLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        msgFromAndDateLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        textLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        messagesFrom = new TextView(context);
        messagesFrom.setTextColor(Color.WHITE);
        messagesFrom.setTextSize(fontSize);
        messagesFrom.setSingleLine(false);
        messagesFrom.setTypeface(Typeface.DEFAULT_BOLD);
        messagesFrom.setPadding(0, 0, 2, 2);
        messagesFrom.setText(listText.getMessageFrom());
        messagesFrom.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        messagesFrom.setGravity(Gravity.LEFT);
        msgFromAndDateLayout.addView(messagesFrom, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        messagesDate = new TextView(context);
        messagesDate.setTextColor(Color.DKGRAY);
        messagesDate.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        messagesDate.setText(listText.getMessageDate());
        messagesDate.setGravity(Gravity.RIGHT);
        msgFromAndDateLayout.addView(messagesDate, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        textLayout.addView(msgFromAndDateLayout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        messagesBody = new TextView(context);
        messagesBody.setTextColor(Color.GRAY);
        messagesBody.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        messagesBody.setText(listText.getMessageBody());

        textLayout.addView(messagesBody, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        listText.getMessageId();

        addView(textLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

    }

    public void setMessageFrom(String messageFrom) {
        this.messagesFrom.setText(messageFrom);
    }

    public void setMessageDate(String messagesDate) {
        this.messagesDate.setText(messagesDate);
    }

    public void setMessageBody(String messagesBody) {
        this.messagesBody.setText(messagesBody);
    }

    public void setId(int messagesId) {
    }
}
