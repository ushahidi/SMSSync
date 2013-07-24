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

package org.addhen.smssync.fragments;

import org.addhen.smssync.R;
import org.addhen.smssync.adapters.BaseListAdapter;
import org.addhen.smssync.models.Model;
import org.addhen.smssync.tasks.ProgressTask;
import org.addhen.smssync.util.Logger;
import org.addhen.smssync.util.Objects;
import org.addhen.smssync.views.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

/**
 * @author eyedol
 */
public abstract class BaseListFragment<V extends View, M extends Model, L extends BaseListAdapter<M>>
		extends SherlockListFragment {

	/**
	 * ListView resource id
	 */
	private final int listViewId;

	/**
	 * ListAdpater class
	 */
	private final Class<L> adapterClass;

	/**
	 * ListAdapter
	 */
	protected L adapter;

	/**
	 * ListView
	 */
	protected ListView listView;

	/**
	 * Menu resource id
	 */
	protected final int menu;

	/**
	 * Layout resource id
	 */
	protected final int layout;

	/**
	 * View class
	 */
	protected final Class<V> viewClass;

	/**
	 * View
	 */
	protected V view;

	/**
	 * BaseListActivity
	 * 
	 * @param view
	 *            View class type
	 * @param adapter
	 *            List adapter class type
	 * @param layout
	 *            layout resource id
	 * @param menu
	 *            menu resource id
	 * @param listView
	 *            list view resource id
	 */
	protected BaseListFragment(Class<V> view, Class<L> adapter, int layout,
			int menu, int listView) {
		this.adapterClass = adapter;
		this.listViewId = listView;
		this.viewClass = view;
		this.menu = menu;
		this.layout = layout;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		if (listViewId != 0) {
			listView = getListView();
			view = Objects.createInstance(viewClass, Activity.class,
					getSherlockActivity());
			adapter = Objects.createInstance(adapterClass, Context.class,
					getSherlockActivity());

			listView.setAdapter(adapter);
			listView.setFocusable(true);
			listView.setFocusableInTouchMode(true);

		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		if (this.menu != 0) {
			inflater.inflate(this.menu, menu);
		}

	}

	@Override
	public android.view.View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		android.view.View root = null;
		if (layout != 0) {
			root = inflater.inflate(layout, container, false);
		}
		return root;
	}

	/**
	 * Called after ListAdapter has been loaded
	 * 
	 * @param success
	 *            true is successfully loaded
	 */
	protected abstract void onLoaded(boolean success);

	@SuppressWarnings("unchecked")
	protected M getSelectedItem() {
		return (M) listView.getSelectedItem();
	}

	public void onItemSelected(AdapterView<?> adapterView,
			android.view.View view, int position, long id) {
	}

	public void onNothingSelected(AdapterView<?> adapterView) {
	}

	/**
	 * ProgressTask sub-class for showing Loading... dialog while the
	 * BaseListAdapter loads the data
	 */
	protected class LoadingTask extends ProgressTask {
		public LoadingTask(Activity activity) {
			super(activity, R.string.loading);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.cancel();
		}

		@Override
		protected Boolean doInBackground(String... args) {

			adapter.refresh();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);

			onLoaded(success);
			listView.setAdapter(adapter);
		}
	}

	protected void log(String message) {
		Logger.log(getClass().getName(), message);
	}

	protected void log(String format, Object... args) {

		Logger.log(getClass().getName(), String.format(format, args));
	}

	protected void log(String message, Exception ex) {

		Logger.log(getClass().getName(), message, ex);
	}

	protected void toastLong(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
	}

	protected void toastLong(int message) {
		Toast.makeText(getActivity(), getText(message), Toast.LENGTH_LONG)
				.show();
	}

	protected void toastShort(int message) {
		Toast.makeText(getActivity(), getText(message), Toast.LENGTH_SHORT)
				.show();
	}

	protected void toastShort(CharSequence message) {
		Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_SHORT)
				.show();
	}

}