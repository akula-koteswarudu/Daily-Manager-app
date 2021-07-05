package com.koti.dailymanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.koti.dailymanager.commonActivity.catNames;

public class add_item extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST=1;
    private static final int CAMERA_REQUEST=2;
    public static final int CAMERA_REQUEST_CODE = 3;
    private ProgressBar progressBar;
    private Uri imageUri;
    private String  time;
    boolean b=false;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask UploadTask;
    OutputStream outputStream;
    String currentPhotoPath;
    FirebaseAuth auth;

    ArrayList<String> categories= new ArrayList<>();

    private Button chooseImage, takePhoto, save;
    private ImageView itemImage ;
    private DataSnapshot snapShot;
    TextInputEditText itemName, itemQuantity,note;
    Spinner category;
    CheckBox checkBox;
    LinearLayout layout;
    DatePicker datePicker=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        chooseImage= findViewById(R.id.uploadBtn);
        takePhoto =findViewById(R.id.takePhoto);
        itemImage=findViewById(R.id.uploadImage);
        save = findViewById(R.id.save);
        itemName= findViewById(R.id.name);
        itemQuantity = findViewById(R.id.quantity);
        note = findViewById(R.id.note);
        category = findViewById(R.id.spinner);
        checkBox= findViewById(R.id.checkbox);
        layout = findViewById(R.id.li_use);
        progressBar= findViewById(R.id.progressBar);
        datePicker = findViewById(R.id.calendarView);
        auth= FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("users/"+auth.getCurrentUser().getUid());
        databaseReference= FirebaseDatabase.getInstance().getReference("users/"+auth.getCurrentUser().getUid());
        String CatName= getIntent().getStringExtra("catName");
        int Index = 0;
        boolean e = false;

        categories=new ArrayList<>();

        for(int i=0;i<catNames.size();i++){
            categories.add(catNames.get(i));
            System.out.println(catNames.get(i));
            if(catNames.get(i).equals(CatName)) {
                Index = i;
                e=true;
            }
        }

        if(!e){
            categories.add(CatName);
            Index= categories.size()-1;
        }



        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(add_item.this, android.R.layout.simple_list_item_1,categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(Index);





        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){

                    datePicker=new DatePicker(add_item.this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(22,10,10,10);
                    datePicker.setLayoutParams(params);
                    datePicker.setId(R.id.calendarView);
                    layout.addView(datePicker,9);

                }
                else{
                    layout.removeViewAt(9);
                    datePicker=null;
                }


            }
        });


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = itemName.getText().toString();
                String quantity = itemQuantity.getText().toString();
                save.setEnabled(!(name.trim()).isEmpty() && !(quantity.trim()).isEmpty());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        itemName.addTextChangedListener(textWatcher);
        itemQuantity.addTextChangedListener(textWatcher);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openFileChooser();

            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermissions();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                save.setEnabled(false);

                if(UploadTask!=null && UploadTask.isInProgress()){
                    Toast.makeText(add_item.this,"upload is in progress",Toast.LENGTH_SHORT).show();
                }else{
                    if(imageUri!=null && b){
                        File file= new File(SiliCompressor.with(add_item.this)
                                .compress(FileUtils.getPath(add_item.this,imageUri),new File(add_item.this.getCacheDir(),"temp")));
                        imageUri= Uri.fromFile(file);

                    }
                    uploadfile();
                }

            }
        });

    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST);
        }else{
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CAMERA_REQUEST){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(add_item.this,"Camera permission is required to use camera",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK &&
                data!=null && data.getData()!=null){

            imageUri= data.getData();
            Picasso.with(this).load(imageUri).into(itemImage);
            b=false;

        }
        if(requestCode==CAMERA_REQUEST_CODE && resultCode==RESULT_OK ){

            File f= new File(currentPhotoPath);
            itemImage.setImageURI(Uri.fromFile(f));
            imageUri= Uri.fromFile(f);
            b=true;

        }

    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        if(!b){
            return mime.getExtensionFromMimeType(cr.getType(uri));
        }
        return "jpg";
    }

    private void uploadfile() {
        String name= itemName.getText().toString();
        String quantity = itemQuantity.getText().toString();
        String Note= note.getText().toString();
        String Category = category.getSelectedItem().toString();
        String date;
        if(datePicker!=null){
            date = datePicker.getDayOfMonth()+"-"+datePicker.getMonth()+"-"+datePicker.getYear();
        }else {
            date="";
        }
        time= System.currentTimeMillis()+"";
        if(imageUri!=null){
            StorageReference fileRef= storageReference.child(Category+"/"+time+"."+getFileExtension(imageUri));
            UploadTask=fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Upload upload = new Upload(name.trim(),quantity.trim(),Note.trim(),date,uri.toString());
                                    upload.setCategory(Category);
                                    databaseReference.child(Category+"/"+time).setValue(upload);

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    },1000);


                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(add_item.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                            save.setEnabled(true);

                                        }
                                    });



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(add_item.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            save.setEnabled(true);

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });


        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            Upload upload = new Upload(name.trim(),quantity.trim(),Note.trim(),date,"https://acadianakarate.com/wp-content/uploads/2017/04/default-image.jpg");
            databaseReference.child(Category+"/"+time).setValue(upload);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    setResult(RESULT_OK);
                    finish();

                }
            },1000);
        }
    }
}