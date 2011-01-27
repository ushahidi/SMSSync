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

import org.addhen.smssync.data.Messages;
import org.addhen.smssync.data.SmsSyncDatabase;
 
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
 
public class SmsSyncOutbox extends Activity
{
  
	/** Called when the activity is first created. */
	private static ListView listMessages = null;
	private static List<Messages> mOldMessages;
	private static ListMessagesAdapter ila;
	private static TextView emptyListText;
	private static final int SMSSYNC_SYNC = Menu.FIRST+1;
	private static final int DELETE = Menu.FIRST+2;
	private static final int SETTINGS = Menu.FIRST+3;
	private final Handler mHandler = new Handler();
	public static SmsSyncDatabase mDb;	
  
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setTitle(R.string.outbox);
		setContentView( R.layout.list_messages );
		listMessages = (ListView) findViewById( R.id.view_messages );
		emptyListText = (TextView) findViewById(R.id.empty);
		mOldMessages = new ArrayList<Messages>();
		ila = new ListMessagesAdapter(SmsSyncOutbox.this);
		mHandler.post(mDisplayMessages);
		displayEmptyListText();
	}
	
	public static void displayEmptyListText() {
		if(ila.getCount() == 0 ) {
			emptyListText.setVisibility(View.VISIBLE);
		} else {
			emptyListText.setVisibility(View.GONE);
		}
	}
  
	@Override
	protected void onResume(){
		super.onResume();
			mHandler.post(mDisplayMessages);
	}
  
	@Override
	protected void onPause() {
		super.onPause();
		mHandler.post(mDisplayMessages);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.post(mDisplayMessages);
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
	
	final Runnable mSyncMessages = new Runnable() {
		public void run() {
			setProgressBarIndeterminateVisibility(true);
			int result = syncMessages();
			try {
				if( result == 0 ){
					Util.showToast(SmsSyncOutbox.this, R.string.sending_succeeded);
					
				}else {
					Util.showToast(SmsSyncOutbox.this, R.string.sending_failed);
				}
				setProgressBarIndeterminateVisibility(false);
			}catch(Exception e) {
				return ; 
			}
		}
	};
	
	final Runnable mDeleteAllMessages = new Runnable() {
		public void run() {
			setProgressBarIndeterminateVisibility(true);
			boolean result = deleteAllMessages();
			try {
				if( result ){
					Util.showToast(SmsSyncOutbox.this, R.string.messages_deleted);
					ila.removeItems();
					ila.notifyDataSetChanged();
					displayEmptyListText();
				}else {
					Util.showToast(SmsSyncOutbox.this, R.string.messages_deleted_failed);
				}
				setProgressBarIndeterminateVisibility(false);
			}catch(Exception e) {
				return ; 
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
		return(applyMenuChoice(item) ||
				super.onOptionsItemSelected(item));
	}
 
	public boolean onContextItemSelected(MenuItem item) {
		return(applyMenuChoice(item) ||
        super.onContextItemSelected(item));
	}
  
	private void populateMenu(Menu menu) {
		MenuItem i;i = menu.add( Menu.NONE, SMSSYNC_SYNC, Menu.NONE, R.string.menu_sync );
		i.setIcon(android.R.drawable.ic_menu_send);
		
		i = menu.add(Menu.NONE, DELETE, Menu.NONE, R.string.menu_delete);
		i.setIcon(android.R.drawable.ic_menu_delete);
		
		i = menu.add( Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
		i.setIcon(android.R.drawable.ic_menu_preferences);
		  
	  
	}
  
	private boolean applyMenuChoice(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
    		case SMSSYNC_SYNC:
    			SyncTask syncTask = new SyncTask();
    			syncTask.execute();
    			return(true); 
        
    		case SETTINGS:
    			intent = new Intent( SmsSyncOutbox.this,  Settings.class);
    			startActivity(intent);
    			return(true);
    			
    		case DELETE:
    			mHandler.post(mDeleteAllMessages);
    			return(true);
        
		}
		return(false);
	}
	
	// get messages from the db
	public static void showMessages() {
		Cursor cursor;
		cursor = SmsSyncApplication.mDb.fetchAllMessages();
	  
		String messagesFrom;
		String messagesDate;
		String messagesBody;
			
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
		displayEmptyListText();
	}
	
	// get messages from the db
	public int syncMessages() {
		Cursor cursor;
		cursor = SmsSyncApplication.mDb.fetchAllMessages();
		String messagesFrom;
		String messagesBody;
		String messagesDate;
		
		int deleted = 0;
		mOldMessages.clear();
		if (cursor.moveToFirst()) {
			int messagesIdIndex = cursor.getColumnIndexOrThrow( 
				SmsSyncDatabase.MESSAGES_ID);
			int messagesFromIndex = cursor.getColumnIndexOrThrow(
				SmsSyncDatabase.MESSAGES_FROM);
				
			int messagesBodyIndex = cursor.getColumnIndexOrThrow(
				SmsSyncDatabase.MESSAGES_BODY);
			
			int messagesDateIndex = cursor.getColumnIndexOrThrow(SmsSyncDatabase.MESSAGES_DATE);

			do {
			  
				Messages messages = new Messages();
				mOldMessages.add( messages );
			  
				int messageId = Util.toInt(cursor.getString(messagesIdIndex));
				messages.setMessageId(messageId);
				
				
				messagesFrom = Util.capitalizeString(cursor.getString(messagesFromIndex));
				messages.setMessageFrom(messagesFrom);
				
				messagesDate = cursor.getString(messagesDateIndex);
				messages.setMessageDate(messagesDate);
			  
				messagesBody = cursor.getString(messagesBodyIndex);
				messages.setMessageBody(messagesBody);
				
				// post to web service
				if( Util.postToAWebService(messagesFrom, messagesBody,SmsSyncOutbox.this) ) {
					//if it successfully pushes message, delete message from db
					ila.removeItems();
					ila.notifyDataSetChanged();
					SmsSyncApplication.mDb.deleteMessagesById(messageId);
					deleted = 0;
				} else {
					deleted = 1;
				}
				
			  
			} while (cursor.moveToNext());
		}
		cursor.close();
		ila.notifyDataSetChanged();
		return deleted;
	}
	
	// Delete all messages from outbox
	public boolean deleteAllMessages() {
		return SmsSyncApplication.mDb.deleteAllMessages();
	}
	
  
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	 //thread class
	private class SyncTask extends AsyncTask <Void, Void, Integer> {
		
		protected Integer status;
		@Override
		protected void onPreExecute() {
		}
		
		@Override 
		protected Integer doInBackground(Void... params) {
			status = 0 ;
			mHandler.post(mSyncMessages);
			return status;
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			showMessages();
		}
	}
}