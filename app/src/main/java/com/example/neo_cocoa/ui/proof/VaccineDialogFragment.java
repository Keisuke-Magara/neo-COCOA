package com.example.neo_cocoa.ui.proof;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.neo_cocoa.R;

import java.util.Calendar;

public class VaccineDialogFragment extends DialogFragment {
    private ProofFragment proofFragment;
    private EditText numOfVacEditText;
    private EditText vacYearEditText;
    private EditText vacMonthEditText;
    private EditText vacDayEditText;
    private TextView numOfVacTextView;
    private TextView vacDateTextView;
    public VaccineDialogFragment(ProofFragment pf){
        this.proofFragment = pf;//
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        vacDateTextView = new TextView(getActivity());
        vacDateTextView.setText(R.string.proof_dialog_message_vaccine_date);

        numOfVacTextView = new TextView(getActivity());
        numOfVacTextView.setText(R.string.proof_dialog_message_num_of_vaccine);

        numOfVacEditText = new EditText(getActivity());
        if(proofFragment.getNumOfVaccine() > 0) {
            numOfVacEditText.setHint(String.valueOf(proofFragment.getNumOfVaccine()));
        }else {
            numOfVacEditText.setHint("0");
        }
        numOfVacEditText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");
        vacYearEditText = new EditText(getActivity());
        vacYearEditText.setHint(year.format(c.getTime()));
        vacYearEditText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        vacMonthEditText = new EditText(getActivity());
        vacMonthEditText.setHint(month.format(c.getTime()));
        vacMonthEditText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        vacDayEditText = new EditText(getActivity());
        vacDayEditText.setHint(day.format(c.getTime()));
        vacDayEditText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.proof_title_num_of_vaccine);
        builder.setPositiveButton(R.string.ok, new DialogButtonClickListener());
        builder.setNegativeButton(R.string.ng, new DialogButtonClickListener());

        LinearLayout layoutNum = new LinearLayout(getActivity());
        layoutNum.addView(numOfVacEditText);
        LinearLayout layoutDate = new LinearLayout(getActivity());
        layoutDate.addView(vacYearEditText);
        layoutDate.addView(vacMonthEditText);
        layoutDate.addView(vacDayEditText);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(numOfVacTextView);
        layout.addView(layoutNum);
        layout.addView(vacDateTextView);
        layout.addView(layoutDate);
        builder.setView(layout);
        //ダイアログオブジェクトを生成し、リターン。
        AlertDialog dialog = builder.create();
        return dialog;
    }


    private class DialogButtonClickListener implements DialogInterface.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String msg = "";
            switch(which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if(numOfVacEditText.length() > 0
                            && numOfVacEditText.length() < 3
                            && vacYearEditText.length() > 0
                            && vacYearEditText.length() < 5
                            && vacMonthEditText.length() > 0
                            && vacMonthEditText.length() < 3
                            && vacDayEditText.length() > 0
                            && vacDayEditText.length() < 3
                    )
                    {
                        proofFragment.setNumOfVaccine(Integer.valueOf(numOfVacEditText.getText().toString()));
                        proofFragment.setVaccineDate(
                                Integer.valueOf(vacYearEditText.getText().toString())*10000 +
                                        Integer.valueOf(vacMonthEditText.getText().toString())*100 +
                                        Integer.valueOf(vacDayEditText.getText().toString()));
                        proofFragment.refreshProofFragment();
                        msg = getString(R.string.proof_message_success_register);

                    }else if(Integer.valueOf(numOfVacEditText.getText().toString()) == 0) {
                        msg = getString(R.string.proof_message_success_register);
                    }else {
                        msg = getString(R.string.proof_message_failed_register);
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    msg = getString(R.string.proof_message_cancel_register);
                    break;
            }
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        }
    }
}