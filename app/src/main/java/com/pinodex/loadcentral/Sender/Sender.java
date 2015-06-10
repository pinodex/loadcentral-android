package com.pinodex.loadcentral.Sender;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by pinodex on 6/10/15.
 */
public class Sender {

    protected Context context;

    protected String command;

    protected String[] product;

    protected String accessNumber;

    protected String recipientNumber;

    protected String password;

    protected ProgressDialog progressDialog;

    public Sender(Context ctx) {
        context = ctx;
    }

    public Sender setCommand(String com) {
        command = com;
        return this;
    }

    public Sender setProduct(String[] pt) {
        product = pt;
        return this;
    }

    public Sender setAccessNumber(String number) {
        accessNumber = number;
        return this;
    }

    public Sender setRecipientNumber(String number) {
        recipientNumber = number;
        return this;
    }

    public Sender setPassword(String pass) {
        password = pass;
        return this;
    }

    public Sender setProgressDialog(ProgressDialog dialog) {
        progressDialog = dialog;
        return this;
    }

    public void send() {}

}
