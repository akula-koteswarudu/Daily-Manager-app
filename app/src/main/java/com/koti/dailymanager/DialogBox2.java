package com.koti.dailymanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogBox2 extends AppCompatDialogFragment {

    private EditText quantity;
    private DialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogbox2,null);
        builder.setView(view)
                .setTitle("change quantity")
                .setNegativeButton("cancel",null)
                .setPositiveButton("change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String Quantity = quantity.getText().toString();
                        listener.applyText(Quantity,getTag());
                    }
                });
        quantity= view.findViewById(R.id.editQuantity0);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener= (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement dialog listener");
        }
    }

    public interface DialogListener{
        void applyText(String quantity,String position);
    }
}

