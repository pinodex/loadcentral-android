package com.pinodex.loadcentral.Util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by pinodex on 6/10/15.
 */
public class IndeterminateLoader extends ProgressDialog {

    public IndeterminateLoader(Context ctx) {
        super(ctx);

        this.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.setIndeterminate(true);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }

}
