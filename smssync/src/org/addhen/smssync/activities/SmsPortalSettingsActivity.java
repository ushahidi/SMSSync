package org.addhen.smssync.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;

import org.addhen.smssync.R;

public class SmsPortalSettingsActivity extends SherlockActivity implements
		OnClickListener {

	private final String SMSPORTAL_1_PACKAGE = "com.smssync.portal.one";
	private final String SMSPORTAL_2_PACKAGE = "com.smssync.portal.two";
	private final String SMSPORTAL_3_PACKAGE = "com.smssync.portal.three";

	private View installSmsPortal1Button;
	private View installSmsPortal2Button;
	private View installSmsPortal3Button;

	private View smsPortal1Installed;
	private View smsPortal2Installed;
	private View smsPortal3Installed;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_portals);
		init();
		checkInstalledVersion();
	}

	private void init() {
		installSmsPortal1Button = (ImageButton) findViewById(R.id.install_sms_portal_1);
		installSmsPortal2Button = (ImageButton) findViewById(R.id.install_sms_portal_2);
		installSmsPortal3Button = (ImageButton) findViewById(R.id.install_sms_portal_3);

		smsPortal1Installed = (ImageView) findViewById(R.id.sms_portal_1_installed);
		smsPortal2Installed = (ImageView) findViewById(R.id.sms_portal_2_installed);
		smsPortal3Installed = (ImageView) findViewById(R.id.sms_portal_3_installed);
		
		installSmsPortal1Button.setOnClickListener(this);
		installSmsPortal2Button.setOnClickListener(this);
		installSmsPortal3Button.setOnClickListener(this);
	}

	private void checkInstalledVersion() {
		PackageManager pm = getPackageManager();
		ArrayList<PackageInfo> apps = (ArrayList<PackageInfo>) pm
				.getInstalledPackages(0);
		ArrayList<String> packages = new ArrayList<String>();
		for (PackageInfo p : apps) {
			packages.add(p.packageName);
		}
		// sms_portal_1
		if (packages.contains(SMSPORTAL_1_PACKAGE)) {
			this.smsPortal1Installed.setVisibility(View.VISIBLE);
			this.installSmsPortal1Button.setVisibility(View.INVISIBLE);
		} else {
			this.smsPortal1Installed.setVisibility(View.INVISIBLE);
			this.installSmsPortal1Button.setVisibility(View.VISIBLE);
		}
		// sms_portal_2
		if (packages.contains(SMSPORTAL_2_PACKAGE)) {
			this.smsPortal2Installed.setVisibility(View.VISIBLE);
			this.installSmsPortal2Button.setVisibility(View.INVISIBLE);
		} else {
			this.smsPortal2Installed.setVisibility(View.INVISIBLE);
			this.installSmsPortal2Button.setVisibility(View.VISIBLE);
		}
		// sms_portal_3
		if (packages.contains(SMSPORTAL_3_PACKAGE)) {
			this.smsPortal3Installed.setVisibility(View.VISIBLE);
			this.installSmsPortal3Button.setVisibility(View.INVISIBLE);
		} else {
			this.smsPortal3Installed.setVisibility(View.INVISIBLE);
			this.installSmsPortal3Button.setVisibility(View.VISIBLE);
		}
	}

	public void onClick(View v) {
		Intent intent;
		if (v == findViewById(R.id.install_sms_portal_1)) {
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id="+SMSPORTAL_1_PACKAGE));
			startActivity(intent);
		}
		if (v == findViewById(R.id.install_sms_portal_2)) {
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id="+SMSPORTAL_2_PACKAGE));
			startActivity(intent);
		}
		if (v == findViewById(R.id.install_sms_portal_3)) {
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id="+SMSPORTAL_3_PACKAGE));
			startActivity(intent);
		}
	}
}
