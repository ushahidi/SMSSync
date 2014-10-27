package net.smssync.survey.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

final class DialogManager {

    private DialogManager() {
    }

    static Dialog create(final Context context, final boolean isShowNeutralButton,
            final OnClickButtonListener listener, final View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.rate_dialog_title);
        builder.setMessage(R.string.rate_dialog_message);
        if (view != null) {
            builder.setView(view);
        }
        builder.setPositiveButton(R.string.rate_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceHelper.setAgreeShowDialog(context, false);
                if (listener != null) {
                    listener.onClickButton(which);
                }
            }
        });
        if (isShowNeutralButton) {
            builder.setNeutralButton(R.string.rate_dialog_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PreferenceHelper.setRemindInterval(context);
                            if (listener != null) {
                                listener.onClickButton(which);
                            }
                        }
                    });
        }
        builder.setNegativeButton(R.string.rate_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceHelper.setAgreeShowDialog(context, false);
                if (listener != null) {
                    listener.onClickButton(which);
                }
            }
        });
        return builder.create();
    }

}