package org.addhen.smssync.activities;

import org.addhen.smssync.R;
import org.addhen.smssync.adapters.TabAdapter;
import org.addhen.smssync.fragments.PendingMessages;
import org.addhen.smssync.fragments.SentMessages;
import org.addhen.smssync.fragments.SyncUrl;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MessagesTabActivity extends SherlockFragmentActivity {

	private ViewPager mViewPager;

	private TabAdapter mTabsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_messages_tab);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab pendingTab = getSupportActionBar().newTab().setText(
				getString(R.string.pending_messages));
		ActionBar.Tab sentTab = getSupportActionBar().newTab().setText(
				getString(R.string.sent_messages));
		
		ActionBar.Tab syncTab = getSupportActionBar().newTab().setText(
				getString(R.string.sync_url));

		mViewPager = (ViewPager) findViewById(R.id.pager);

		mTabsAdapter = new TabAdapter(this, getSupportActionBar(), mViewPager);

		mTabsAdapter.addTab(pendingTab, PendingMessages.class);
		mTabsAdapter.addTab(sentTab, SentMessages.class);
		mTabsAdapter.addTab(syncTab, SyncUrl.class);

		if (savedInstanceState != null) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt("index"));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("index", getSupportActionBar()
				.getSelectedNavigationIndex());
	}


}
