package com.example.admin.quizzy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AddSurveyItemDialog extends DialogFragment {

    public interface AddSurveyItemDialogListener {
        public void onOpenResponseClick(DialogFragment dialog);
        public void onTrueFalseClick(DialogFragment dialog);
    }

    AddSurveyItemDialogListener _listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = (Activity)context;

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DialogListener so we can send events to the host
            _listener = (AddSurveyItemDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddSurveyItemDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_item_dialog_title)
                .setItems(R.array.itemTypes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                _listener.onOpenResponseClick(AddSurveyItemDialog.this);
                                break;
                            case 1:
                                _listener.onTrueFalseClick(AddSurveyItemDialog.this);
                                break;
                            default:
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
