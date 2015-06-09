package com.pinodex.loadcentral;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;


public class AboutScreen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pretty much like Facebook about screen lel.
        setContentView(R.layout.activity_about_screen);

        PackageManager manager = getPackageManager();
        String appVersion = "Unknown";

        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            appVersion = info.versionName;
        } catch (NameNotFoundException e) {
            appVersion = "Error";
        }

        Resources resources = getResources();

        TextView appVersionText = (TextView) findViewById(R.id.app_version);
        appVersionText.setText(resources.getString(R.string.version, appVersion));
    }

}
