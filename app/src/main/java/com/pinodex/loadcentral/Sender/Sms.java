package com.pinodex.loadcentral.Sender;


import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pinodex.loadcentral.R;
import com.pinodex.loadcentral.Util.Preferences;
import com.pinodex.loadcentral.Util.TelephonyInfo;

import java.lang.reflect.Method;

/**
 * Created by pinodex on 6/10/15.
 */
public class Sms extends Sender {

    protected TelephonyInfo telephonyInfo;

    protected boolean preferSim = false;

    protected int simId = 0;

    public Sms(Context ctx) {
        super(ctx);

        telephonyInfo = TelephonyInfo.getInstance(ctx);
        password = Preferences.getString("sms_password");

        if (Preferences.getBoolean("use_preferred_access_number")) {
            String preferredAccessNumber = Preferences.getString("preferred_access_number");

            if (preferredAccessNumber != null) {
                accessNumber = preferredAccessNumber;
            }
        }
    }

    public void send() {
        if (accessNumber == null) {
            final String[] accessNumberArray = TextUtils.split(Preferences.getString("access_numbers"), ",");

            ListView accessNumberList = new ListView(context);
            ArrayAdapter<String> accessNumberAdapter = new ArrayAdapter<>(context, R.layout.default_list_layout, accessNumberArray);

            AlertDialog.Builder accessNumberListDialogBuilder = new AlertDialog.Builder(context);
            accessNumberListDialogBuilder
                    .setTitle(R.string.select_access_number)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setView(accessNumberList);

            final AlertDialog accessNumberListDialog = accessNumberListDialogBuilder.create();

            accessNumberList.setAdapter(accessNumberAdapter);
            accessNumberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    accessNumber = accessNumberArray[position];
                    accessNumberListDialog.dismiss();

                    send();
                }
            });
            accessNumberListDialog.show();

            return;
        }

        if (!preferSim && telephonyInfo.isDualSIM()) {
            String preferredSim = Preferences.getString("preferred_sim");

            if (preferredSim != null) {
                simId = Integer.valueOf(preferredSim);
            }

            if (simId == 0) {
                ListView simList = new ListView(context);
                ArrayAdapter<String> accessNumberAdapter = new ArrayAdapter<>(context, R.layout.default_list_layout, new String[] {
                        "Sim 1", "Sim 2"
                });

                AlertDialog.Builder simListDialogBuilder = new AlertDialog.Builder(context);
                simListDialogBuilder
                        .setTitle(R.string.select_sim)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                accessNumber = null;
                                dialog.dismiss();
                            }
                        })
                        .setView(simList);

                final AlertDialog simListDialog = simListDialogBuilder.create();

                simList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        simId = position + 1;
                        preferSim = true;

                        simListDialog.dismiss();
                        send();
                    }
                });

                simList.setAdapter(accessNumberAdapter);
                simListDialog.show();

                return;
            }
        }

        if (progressDialog != null) {
            progressDialog.show();
        }

        PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED"), 0);

        String name = "isms";
        String message = "";

        if (command.equals("LOAD")) {
            message = product[2] + " " + password + " " + recipientNumber;
        }

        if (command.equals("BAL")) {
            message = "BAL " + password;
        }

        if (simId > 1) {
            name = "isms" + simId;
        }

        try {
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);

            Object param = method.invoke(null, name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);

            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, accessNumber, null, message, sentIntent, deliveryIntent);

                return;
            }

            method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
            method.invoke(stubObj, context.getPackageName(), accessNumber, null, message, sentIntent, deliveryIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
