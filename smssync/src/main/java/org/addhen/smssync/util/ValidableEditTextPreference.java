package org.addhen.smssync.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;


import com.actionbarsherlock.R;

/**
 * Created by mburas@soldevelo.com on 4/16/14.
 *
 * Validable edit text preference.
 * Native version, blocks OK button when text field is empty.
 * Override validateAfterTextChanged method to create your own type of validation
 */
public class ValidableEditTextPreference extends EditTextPreference {
    public ValidableEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


    }

    public ValidableEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ValidableEditTextPreference(Context context) {
        super(context);
    }


    private class EditTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            validateAfterTextChanged();
        }
    }

    protected EditTextWatcher validationWatcher = new EditTextWatcher();

    protected boolean hasValue(String str){
        return (str.length() > 0) && (Integer.valueOf(str)>0);
    }



    protected void validateAfterTextChanged(){
        boolean enable = hasValue(getEditText().getText().toString());
        Dialog dlg = getDialog();

        if(dlg instanceof AlertDialog){
            ((AlertDialog)dlg).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enable);
        }
    }


    @Override
    protected void onAddEditTextToDialogView(View dialogView, EditText editText) {
        editText.removeTextChangedListener(validationWatcher);
        editText.addTextChangedListener(validationWatcher);
        super.onAddEditTextToDialogView(dialogView, editText);
    }
}






