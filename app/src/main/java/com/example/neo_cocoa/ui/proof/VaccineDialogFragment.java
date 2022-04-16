package com.example.neo_cocoa.ui.proof;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.neo_cocoa.R;

import java.util.Calendar;
import java.util.Locale;

public class VaccineDialogFragment extends DialogFragment {
    private ProofFragment proofFragment;
    private EditText numOfVacEditText;
    private String msg;
    public VaccineDialogFragment(ProofFragment pf){
        this.proofFragment = pf;//
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        vacDateTextView = new TextView(getActivity());
//        vacDateTextView.setText(R.string.proof_dialog_message_vaccine_date);

//        numOfVacTextView = new TextView(getActivity());
//        numOfVacTextView.setText(R.string.proof_dialog_message_num_of_vaccine);

        numOfVacEditText = new EditText(getActivity());
        if(proofFragment.getNumOfVaccine() > 0) {
            numOfVacEditText.setHint(String.valueOf(proofFragment.getNumOfVaccine()));
        }else {
            numOfVacEditText.setHint("0");
        }
        numOfVacEditText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.proof_dialog_message_num_of_vaccine);
        builder.setPositiveButton(R.string.ok, new DialogButtonClickListener());
        builder.setNegativeButton(R.string.ng, new DialogButtonClickListener());

        LinearLayout layoutNum = new LinearLayout(getActivity());
        layoutNum.addView(numOfVacEditText);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.addView(numOfVacTextView);
        layout.addView(layoutNum);
        builder.setView(layout);
        //ダイアログオブジェクトを生成し、リターン。
        AlertDialog dialog = builder.create();
        return dialog;
    }


    private class DialogButtonClickListener implements DialogInterface.OnClickListener {
        Context context = getContext();
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if(numOfVacEditText.length() > 0 && numOfVacEditText.length() < 3) {
                        proofFragment.setNumOfVaccine(Integer.valueOf(numOfVacEditText.getText().toString()));
                        if(Integer.valueOf(numOfVacEditText.getText().toString()) > 0) {
                            Calendar calendar = Calendar.getInstance();

                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    getActivity(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    proofFragment.setVaccineDate(String.format(Locale.JAPAN, "%02d/%02d/%02d", year, month + 1, dayOfMonth));
                                    msg = context.getString(R.string.proof_message_success_register);
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                    proofFragment.refreshProofFragment();
                                }

                            },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                            );
                            datePickerDialog.show();
                            msg = getString(R.string.proof_dialog_message_vaccine_date);
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        }else {
                            msg = getString(R.string.proof_message_success_register);
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            proofFragment.refreshProofFragment();
                        }

                    }else if(numOfVacEditText.length() > 0 && Integer.valueOf(numOfVacEditText.getText().toString()) == 0) {
                        msg = getString(R.string.proof_message_success_register);
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }else {
                        msg = getString(R.string.proof_message_failed_register);
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    msg = getString(R.string.proof_message_cancel_register);
                    break;
            }
        }
    }
}