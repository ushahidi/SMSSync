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
 
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.addhen.smssync.data.Messages;
import org.addhen.smssync.data.SmsSyncDatabase;
 
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
 
public class SmsSyncOutbox extends Activity
{
  
	/** Called when the activity is first created. */
	private ListView listMessages = null;
	private List<Messages> mOldMessages;
	private ListMessagesAdapter ila = new ListMessagesAdapter( this );
	private static final int SMSSYNC_SYNC = Menu.FIRST+1;
	private static final int SETTINGS = Menu.FIRST+2;
	private final Handler mHandler = new Handler();
	public static SmsSyncDatabase mDb;
	
  
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView( R.layout.list_messages );
       
		listMessages = (ListView) findViewById( R.id.view_messages );
		mOldMessages = new ArrayList<Messages>();
		mHandler.post(mDisplayMessages);		
		
	}
  
	@Override
	protected void onResume(){
		super.onResume();
		
		if(ila.getCount() == 0 ) {
			mHandler.post(mDisplayMessages);
		}
	}
  
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
  
	final Runnable mDisplayMessages = new Runnable() {
		public void run() {
			setProgressBarIndeterminateVisibility(true);
			showMessages();
			try{
				setProgressBarIndeterminateVisibility(false);
			} catch(Exception e){
				return;  //means that the dialog is not showing, ignore please!
			}
		}
	};

	//menu stuff
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
		populateMenu(menu);
	}
  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
 
		return(super.onCreateOptionsMenu(menu));
	}
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//applyMenuChoice(item);
 
		return(applyMenuChoice(item) ||
				super.onOptionsItemSelected(item));
	}
 
	public boolean onContextItemSelected(MenuItem item) {
 
		return(applyMenuChoice(item) ||
        super.onContextItemSelected(item));
	}
  
	private void populateMenu(Menu menu) {
		MenuItem i;i = menu.add( Menu.NONE, SMSSYNC_SYNC, Menu.NONE, R.string.menu_sync );
		i.setIcon(android.R.drawable.ic_menu_upload);
		
		i = menu.add( Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
		i.setIcon(android.R.drawable.ic_menu_preferences);
		  
	  
	}
  
	private boolean applyMenuChoice(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
    		case SMSSYNC_SYNC:
    			//TODO start the sync activity
    			ReportsTask reportsTask = new ReportsTask();
	            reportsTask.appContext = this;
	            reportsTask.execute();
    			return(true); 
        
    		case SETTINGS:
    			intent = new Intent( SmsSyncOutbox.this,  Settings.class);
			
    			// Make it a subactivity so we know when it returns
    			startActivity(intent);
    			return(true);
        
		}
		return(false);
	}
	
	 //thread class
	private class ReportsTask extends AsyncTask <Void, Void, Integer> {
		
		protected Integer status;
		protected Context appContext;
		
		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);

		}
		
		@Override 
		protected Integer doInBackground(Void... params) {
			status = Util.processMessages(appContext);
			return status;
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			if( result == 4 ){
				
				//Util.showToast(appContext, R.string.internet_connection);
			} else if( result == 0 ) {
				showMessages();
				setProgressBarIndeterminateVisibility(false);
			}
		}

		
	}
  
	// get incidents from the db
	public void showMessages() {
		Cursor cursor;
		cursor = SmsSyncApplication.mDb.fetchAllMessages();
	  
		String messagesFrom;
		String messagesDate;
		String messagesBody;
		String messagesId;
			
		if (cursor.moveToFirst()) {
			int messagesIdIndex = cursor.getColumnIndexOrThrow( 
				SmsSyncDatabase.MESSAGES_ID);
			int messagesFromIndex = cursor.getColumnIndexOrThrow(
				SmsSyncDatabase.MESSAGES_FROM);
			int messagesDateIndex = cursor.getColumnIndexOrThrow(
				SmsSyncDatabase.MESSAGES_DATE);
				
			int messagesBodyIndex = cursor.getColumnIndexOrThrow(
				SmsSyncDatabase.MESSAGES_BODY);
				
				
			ila.removeItems();
			ila.notifyDataSetChanged();
			mOldMessages.clear();
					
			do {
			  
				Messages messages = new Messages();
				mOldMessages.add( messages );
			  
				int id = Util.toInt(cursor.getString(messagesIdIndex));
				messages.setMessageId(id);
				
				
				messagesFrom = Util.capitalizeString(cursor.getString(messagesFromIndex));
				messages.setMessageFrom(messagesFrom);
				
				messagesDate = cursor.getString(messagesDateIndex);
				messages.setMessageDate(messagesDate);
			  
				messagesBody = cursor.getString(messagesBodyIndex);
				messages.setMessageBody(messagesBody);
					
				ila.addItem( new ListMessagesText(messagesFrom, messagesBody, messagesDate, id));
			  
			} while (cursor.moveToNext());
		}
    
		cursor.close();
		ila.notifyDataSetChanged();
		listMessages.setAdapter( ila );
	}
  
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}
  
}