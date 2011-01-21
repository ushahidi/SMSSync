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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ListMessagesTextView extends LinearLayout{
	private TextView messagesFrom;
	private TextView messagesBody;
	private TextView messagesDate;
	private float fontSize = 13.5f;
	private LinearLayout textLayout;
	private TableLayout tblLayout;
	private TableRow tblRow;
	
	public ListMessagesTextView( Context context, ListMessagesText listText ) {
		super(context);
		
		this.setOrientation(VERTICAL);
		this.initComponent( context, listText);
	}
	
	public void initComponent( Context context, ListMessagesText listText ) {
		this.textLayout = new LinearLayout(context);
		
		this.tblLayout = new TableLayout(context);
		
		this.tblLayout.setLayoutParams(new TableLayout.LayoutParams(
				TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
		this.tblLayout.setColumnStretchable(1, true);
		this.tblRow =  new TableRow(context);
		this.tblRow.setLayoutParams(new TableRow.LayoutParams(
				TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		textLayout.setOrientation(VERTICAL);
		textLayout.setPadding(0, 2, 0, 2);
		
		this.textLayout.setLayoutParams(
				new TableRow.LayoutParams(
						TableRow.LayoutParams.FILL_PARENT,
						TableRow.LayoutParams.WRAP_CONTENT)
		);
		
		messagesFrom = new TextView( context);	
		messagesFrom.setTextColor(Color.rgb(255, 0, 0));
		messagesFrom.setTextSize(fontSize);
		messagesFrom.setSingleLine(false);
		messagesFrom.setTypeface(Typeface.DEFAULT_BOLD);
		messagesFrom.setPadding(0, 0, 2, 2);
		messagesFrom.setText( listText.getMessageFrom() );
		messagesFrom.setLayoutParams( new TableRow.LayoutParams( 
				TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		textLayout.addView(messagesFrom, new TableRow.LayoutParams( 
				TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		messagesBody = new TextView( context);
		messagesBody.setTextColor(Color.WHITE);
		messagesBody.setLayoutParams( new LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, 
				TableRow.LayoutParams.WRAP_CONTENT));
		
		messagesBody.setText( listText.getMessageBody() );
		
		textLayout.addView( messagesBody, new TableRow.LayoutParams( 
				TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT) );
		
		messagesDate = new TextView( context);
		messagesDate.setTextColor(Color.GREEN);
		messagesDate.setLayoutParams(new LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, 
				TableRow.LayoutParams.WRAP_CONTENT));
		
		messagesDate.setText(listText.getMessageDate());
		
		textLayout.addView(messagesDate, new TableRow.LayoutParams( 
				TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		
		listText.getMessageId();
		
		tblRow.addView( textLayout);
		tblLayout.addView(tblRow);
		
		addView(tblLayout, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
	}
	
	public void setMessageFrom( String messageFrom ) {
		this.messagesFrom.setText(messageFrom);
	}
	
	public void setMessageDate( String messagesDate ) {
		this.messagesDate.setText(messagesDate);
	}
	
	public void setMessageBody( String messagesBody ) {
		this.messagesBody.setText(messagesBody);
	}
	
	public void setId( int messagesId ) {
	}
}
