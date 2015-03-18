package org.addhen.smssync.fragments;

import android.view.View;

import org.addhen.smssync.R;
import org.addhen.smssync.adapters.DonationAdapter;
import org.addhen.smssync.models.Donation;
import org.addhen.smssync.views.DonationView;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class DonationFragment extends BaseListFragment<DonationView, Donation, DonationAdapter> implements
        android.view.View.OnClickListener {

    /**
     * BaseListActivity
     *
     */
    public DonationFragment() {
        super(DonationView.class, DonationAdapter.class, R.layout.list_donation, 0, android.R.id.list);
    }

    @Override
    public void onClick(View v) {

    }
}
