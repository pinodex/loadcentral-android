package com.pinodex.loadcentral;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.pinodex.loadcentral.Sender.Sender;
import com.pinodex.loadcentral.Sender.Sms;
import com.pinodex.loadcentral.Util.IndeterminateLoader;
import com.pinodex.loadcentral.Util.Prompt;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class SellProductActivity extends ActionBarActivity {

    static ArrayList<String[]> productsList = new ArrayList<>();

    private long mLastClickTime = 0;

    Resources resources;

    Spinner productListDropdown;

    Sender sender;

    ProgressDialog progressDialog;

    BroadcastReceiver smsSentReciever;

    BroadcastReceiver smsDeliveredReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_product);

        resources = getResources();

        Bundle bundle = getIntent().getExtras();

        String networkId = bundle.getString("networkId");
        String networkName = bundle.getString("networkName");
        String networkBg = bundle.getString("networkBg");
        String networkTc = bundle.getString("networkTc");

        sender = new Sms(this).setCommand("LOAD");

        setTitle(networkName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor(networkBg));
        toolbar.setTitleTextColor(Color.parseColor(networkTc));

        final Button sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setBackgroundColor(Color.parseColor(networkBg));
        sendButton.setTextColor(Color.parseColor(networkTc));

        productsList.clear();

        try {
            XmlPullParser productsXml = resources.getXml(
                    resources.getIdentifier(networkId, "xml", getPackageName())
            );

            while (productsXml.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (productsXml.getEventType() == XmlPullParser.START_TAG) {
                    if (productsXml.getName().equals("product")) {
                        productsList.add(new String[]{
                                productsXml.getAttributeValue(null, "type"),
                                productsXml.getAttributeValue(null, "name"),
                                productsXml.getAttributeValue(null, "code")
                        });
                    }
                }

                productsXml.next();
            }
        } catch (Throwable t) {
            Toast.makeText(this, "ERROR: " + t.toString(), Toast.LENGTH_LONG).show();
            finish();
        }

        productListDropdown = (Spinner) findViewById(R.id.selectProductView);
        final RelativeLayout quantity = (RelativeLayout) findViewById(R.id.quantityForm);

        productListDropdown.setAdapter(new ArrayAdapter<>(this, R.layout.product_list_layout, getProductsName()));
        productListDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (productsList.get(position)[0].equals("variable")) {
                    quantity.setVisibility(View.VISIBLE);
                } else {
                    quantity.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Edi wala!
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }

                mLastClickTime = SystemClock.elapsedRealtime();

                sendButton.setEnabled(false);
                sendButton.setClickable(false);

                processLoad();

                sendButton.setEnabled(true);
                sendButton.setClickable(true);
            }
        });

        progressDialog = new IndeterminateLoader(this);
        progressDialog.setMessage(resources.getString(R.string.sending_sms));

        smsSentReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressDialog.dismiss();

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Prompt.showSuccess(SellProductActivity.this, R.string.sms_sent, true);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Prompt.showError(SellProductActivity.this, resources.getString(R.string.sms_not_sent_code, getResultCode()), false);
                        break;
                }
            }
        };

        smsDeliveredReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressDialog.dismiss();

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Prompt.showSuccess(SellProductActivity.this, R.string.sms_sent, true);
                        break;
                    case Activity.RESULT_CANCELED:
                        Prompt.showError(SellProductActivity.this, R.string.sms_not_sent, false);
                        break;
                }
            }
        };
    }

    private ArrayList<String> getProductsName() {
        int productsCount = productsList.size();
        final ArrayList<String> products = new ArrayList<>();

        for (int i = 0; i < productsCount; ++i) {
            products.add(productsList.get(i)[1]);
        }

        return products;
    }

    private void processLoad() {
        final String[] productSelection = productsList.get(productListDropdown.getSelectedItemPosition());
        final String quantity = ((EditText) findViewById(R.id.quantityInput)).getText().toString();
        final String number = ((EditText) findViewById(R.id.mobileNumberInput)).getText().toString();

        if (productSelection[0].equals("variable") && (quantity.isEmpty() || quantity.equals("0"))) {
            Prompt.showError(this, R.string.invalid_quantity, false);
            return;
        }

        if (!number.matches("(0|63)[0-9]{10}")) {
            Prompt.showError(this, R.string.invalid_number, false);
            return;
        }

        String confirmationText = "Error";

        if (productSelection[0].equals("fixed")) {
            confirmationText = resources.getString(R.string.send_confirmation_fixed,
                    productSelection[1], productSelection[2], number);
        }

        if (productSelection[0].equals("variable")) {
            productSelection[2] = String.format(productSelection[2], quantity);

            confirmationText = resources.getString(R.string.send_confirmation_variable,
                    quantity, productSelection[2], number);
        }

        sender.setProduct(productSelection);
        sender.setRecipientNumber(number);

        new AlertDialog.Builder(this).setTitle(R.string.confirmation)
            .setMessage(confirmationText)
            .setCancelable(false)
            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sender.setProgressDialog(progressDialog);
                    sender.send();
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_leave_in, R.anim.activity_leave_out);
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(smsSentReciever);
        unregisterReceiver(smsDeliveredReciever);
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(smsSentReciever, new IntentFilter("SMS_SENT"));
        registerReceiver(smsDeliveredReciever, new IntentFilter("SMS_DELIVERED"));
    }

}
