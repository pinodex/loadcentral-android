package com.pinodex.loadcentral.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.pinodex.loadcentral.R;

/**
 * Created by pinodex on 6/10/15.
 */
public class Prompt extends AlertDialog.Builder {

    private Context context;

    private boolean finishAfter = false;

    public Prompt(Context ctx) {
        super(ctx);
        context = ctx;

        this.setCancelable(false);
        this.setTitle("Prompt");
        this.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (finishAfter) {
                    ((Activity) context).finish();
                    ((Activity) context).overridePendingTransition(R.anim.activity_leave_in, R.anim.activity_leave_out);
                }
            }
        });
    }

    public Prompt error(int message) {
        this.setTitle(R.string.error);
        this.setMessage(message);

        return this;
    }

    public Prompt error(String message) {
        this.setTitle(R.string.error);
        this.setMessage(message);

        return this;
    }

    public Prompt success(int message) {
        this.setTitle(R.string.success);
        this.setMessage(message);

        return this;
    }

    public Prompt success(String message) {
        this.setTitle(R.string.success);
        this.setMessage(message);

        return this;
    }

    public Prompt finishAfter(boolean val) {
        finishAfter = val;
        return this;
    }

    public static void showError(Context context, int message, boolean fa) {
        new Prompt(context).error(message).finishAfter(fa).show();
    }

    public static void showError(Context context, String message, boolean fa) {
        new Prompt(context).error(message).finishAfter(fa).show();
    }

    public static void showSuccess(Context context, int message, boolean fa) {
        new Prompt(context).success(message).finishAfter(fa).show();
    }

    public static void showSuccess(Context context, String message, boolean fa) {
        new Prompt(context).success(message).finishAfter(fa).show();
    }

}
