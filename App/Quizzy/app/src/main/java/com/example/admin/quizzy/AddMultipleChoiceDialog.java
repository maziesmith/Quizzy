package com.example.admin.quizzy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.NumberPicker;

public class AddMultipleChoiceDialog extends DialogFragment {

    public interface AddMultipleChoiceDialogListener {
        public void onConfirm(DialogFragment dialog, int chosenValue);
    }

    AddMultipleChoiceDialogListener _listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = (Activity)context;

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DialogListener so we can send events to the host
            _listener = (AddMultipleChoiceDialog.AddMultipleChoiceDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddMultipleChoiceDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogBody = getActivity().getLayoutInflater().inflate(R.layout.add_multiple_choice_dialog, null);
        NumberPicker picker = (NumberPicker)dialogBody.findViewById(R.id.responses_picker);
        picker.setMinValue(3);
        picker.setMaxValue(SurveyItem.MAX_RESPONSES);

        builder.setTitle(R.string.pick_num_responses)
                .setView(dialogBody)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NumberPicker picker = (NumberPicker)((Dialog)dialogInterface).findViewById(R.id.responses_picker);
                        int num = picker.getValue();
                        _listener.onConfirm(AddMultipleChoiceDialog.this, num);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddMultipleChoiceDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
