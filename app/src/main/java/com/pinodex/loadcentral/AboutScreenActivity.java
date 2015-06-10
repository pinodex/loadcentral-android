package com.pinodex.loadcentral;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;


public class AboutScreenActivity extends ActionBarActivity {

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

        TextView privacyPolicyLink = (TextView) findViewById(R.id.privacy_policy);
        privacyPolicyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AboutScreenActivity.this)
                    .setTitle(R.string.app_privacy_policy)
                    .setMessage(R.string.app_privacy_policy_content)
                        .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                    .create()
                    .show();
            }
        });
    }

}
