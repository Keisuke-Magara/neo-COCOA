package com.example.neo_cocoa.ui.proof;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.neo_cocoa.R;

public class BodyTemperatureDialogFragment extends DialogFragment {
    private ProofFragment proofFragment;
    private EditText btEditText;

    public BodyTemperatureDialogFragment(ProofFragment pf){
        this.proofFragment = pf;//
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        btEditText = new EditText(getActivity());
        btEditText.setHint(R.string.proof_dialog_message_body_temperature);
        btEditText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.proof_title_body_temperature);
        builder.setPositiveButton(R.string.ok, new DialogButtonClickListener());
        builder.setNegativeButton(R.string.ng, new DialogButtonClickListener());
        builder.setView(btEditText);
        //ダイアログオブジェクトを生成し、リターン。
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private class DialogButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String msg = "";
            switch(which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if(btEditText.length() != 0) {
                        proofFragment.setBodyTemperature(Float.valueOf(btEditText.getText().toString()));
                        proofFragment.refreshProofFragment();
                        msg = "体温を登録しました";
                    }else {
                        msg = "入力に間違いがあります";
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    msg = "登録をキャンセルしました";
                    break;
            }
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        }
    }
}