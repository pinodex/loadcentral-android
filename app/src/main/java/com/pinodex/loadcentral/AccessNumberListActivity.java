package com.pinodex.loadcentral;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class AccessNumberListActivity extends ActionBarActivity {

    private AccessNumberListAdapter accessNumberListAdapter;

    private ArrayList<String> accessNumbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_numbers_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView accessNumberList = (ListView) findViewById(R.id.access_numbers_list);

        String[] accessNumbersArray = TextUtils.split(Preferences.getString("access_numbers"), ",");
        for (int i = 0; i < accessNumbersArray.length; ++i) {
            accessNumbers.add(accessNumbersArray[i]);
        }

        accessNumberListAdapter = new AccessNumberListAdapter(this, accessNumbers);
        accessNumberList.setAdapter(accessNumberListAdapter);
    }

    private void addAccessNumber() {
        AlertDialog.Builder addAccessNumberDialogBuilder = new AlertDialog.Builder(this);

        final EditText userInput = new EditText(this);
        userInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        addAccessNumberDialogBuilder
                .setCancelable(true)
                .setView(userInput)
                .setTitle(R.string.add_access_number)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog addAccessNumberDialog = addAccessNumberDialogBuilder.create();
        addAccessNumberDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = addAccessNumberDialog
                        .getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String customNumber = userInput.getText().toString();

                        if (!customNumber.matches("(0|63)[0-9]{10}")) {
                            Toast.makeText(AccessNumberListActivity.this, "Invalid number",
                                    Toast.LENGTH_SHORT).show();

                            return;
                        }

                        if (accessNumbers.contains(customNumber)) {
                            Toast.makeText(AccessNumberListActivity.this, "Number already exists",
                                    Toast.LENGTH_SHORT).show();

                            return;
                        }

                        accessNumbers.add(customNumber);
                        accessNumberListAdapter.update(accessNumbers);
                        Preferences.putString("access_numbers", TextUtils.join(",", accessNumbers));

                        addAccessNumberDialog.dismiss();
                        SettingsActivity.doRefresh = true;
                    }
                });
            }
        });

        addAccessNumberDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_access_numbers_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_number) {
            addAccessNumber();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
