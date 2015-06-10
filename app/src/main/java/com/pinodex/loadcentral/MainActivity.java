package com.pinodex.loadcentral;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pinodex.loadcentral.Sender.Sms;
import com.pinodex.loadcentral.Util.Preferences;

public class MainActivity extends ActionBarActivity {

    public static String[][] mobileNetworks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources resources = getResources();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Todo: Fix action bar being extended when keyboard is shown.
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(resources.getColor(R.color.white));

        if (Preferences.getString("sms_password") == null) {
            LayoutInflater li = LayoutInflater.from(this);
            View dialogView = li.inflate(R.layout.set_sms_password_dialog, null);

            AlertDialog.Builder setSmsPasswordDialogBuilder = new AlertDialog.Builder(this);
            final EditText userInput = (EditText) dialogView.findViewById(R.id.SmsPasswordInput);

            setSmsPasswordDialogBuilder
                    .setCancelable(false)
                    .setView(dialogView)
                    .setTitle(R.string.set_sms_password)
                    .setPositiveButton(R.string.save, null)
                    .setNegativeButton(R.string.cancel, null);

            final AlertDialog setSmsPasswordDialog = setSmsPasswordDialogBuilder.create();
            setSmsPasswordDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = setSmsPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button negativeButton = setSmsPasswordDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                    positiveButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            String newSmsPassword = userInput.getText().toString();

                            if (newSmsPassword.isEmpty()) {
                                Toast.makeText(MainActivity.this, R.string.invalid_password_format, Toast.LENGTH_SHORT).show();

                                return;
                            }

                            Preferences.putString("sms_password", newSmsPassword);
                            setSmsPasswordDialog.dismiss();
                        }

                    });

                    negativeButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            setSmsPasswordDialog.cancel();
                            finish();
                        }

                    });
                }

            });
            setSmsPasswordDialog.show();
        }

        if (Preferences.getString("access_numbers") == null) {
            String[] accessNumbers = resources.getStringArray(R.array.default_access_numbers);
            Preferences.putString("access_numbers", TextUtils.join(",", accessNumbers));
        }

        TypedArray networkMap = resources.obtainTypedArray(R.array.network_map);
        int networkMapLength = networkMap.length();

        mobileNetworks = new String[networkMapLength][];

        for (int i = 0; i < networkMapLength; ++i) {
            int resId = networkMap.getResourceId(i, 0);

            if (resId > 0) {
                mobileNetworks[i] = resources.getStringArray(resId);
            }
        }

        networkMap.recycle();

        ListView productListView = (ListView) findViewById(R.id.productListView);
        productListView.setAdapter(new NetworkListAdapter(this, mobileNetworks));
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, SellProductActivity.class);
                intent.putExtra("networkId", mobileNetworks[position][0]);
                intent.putExtra("networkName", mobileNetworks[position][1]);
                intent.putExtra("networkBg", mobileNetworks[position][2]);
                intent.putExtra("networkTc", mobileNetworks[position][3]);

                startActivity(intent);
                overridePendingTransition(R.anim.activity_go_in, R.anim.activity_go_out);

                // Todo: fix failing transition on some devices
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.action_balance_inquiry) {
            new AlertDialog.Builder(this).setTitle(R.string.confirmation)
                .setMessage(R.string.action_balance_inquiry_confirm)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Sms(MainActivity.this).setCommand("BAL").send();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
