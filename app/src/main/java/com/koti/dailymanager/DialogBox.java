package com.koti.dailymanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogBox extends AppCompatDialogFragment implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextTitle;
    private ImageView imageView;
    private Button button;
    private DialogListener listener;
    private Context context;
    private Uri imageUri=null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogbox,null);
        builder.setView(view)
                .setTitle("Add Category")
                .setNegativeButton("cancel",null)
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String title= editTextTitle.getText().toString();
                        listener.apply(title,imageUri);
                    }
                });
        editTextTitle= view.findViewById(R.id.dialogText);
        imageView= view.findViewById(R.id.dialogImage);
        button= view.findViewById(R.id.dialogUpload);
        button.setOnClickListener(this);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context= context;
        try {
            listener= (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement dialog listener");
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    public interface DialogListener{
        void apply(String title, Uri imageUri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK &&
                data!=null && data.getData()!=null){

            imageUri= data.getData();
            imageView.setImageURI(imageUri);
        }
    }
}

