package com.koti.dailymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DialogBox.DialogListener{

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    Menu menu;
    RecyclerView recyclerView;
    List<DashboardItem> items;
    DashboardAdapter adapter;
    ProgressDialog progressBar;
    ProgressBar circle;
    FirebaseAuth auth;
    DatabaseReference reference;int count=0;
    private ValueEventListener DBListener;

    public static StorageReference storageReference;
    public static FirebaseStorage storage;
    public static DatabaseReference databaseReference;
    boolean b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        navigationView= findViewById(R.id.nav_view);
        drawerLayout= findViewById(R.id.drawer);
        toolbar= findViewById(R.id.toolbar);
        auth= FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference("users");
        recyclerView= findViewById(R.id.recycler);
        circle= findViewById(R.id.progressCircle);

        setSupportActionBar(toolbar);
        menu= navigationView.getMenu();
        MenuItem profileItem= menu.getItem(2);
        Menu subMenu =profileItem.getSubMenu();
        profileItem=subMenu.getItem(0);
        MenuItem emailItem= subMenu.getItem(1);
        MenuItem phoneItem= subMenu.getItem(2);
        MenuItem professionItem= subMenu.getItem(3);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        b= getIntent().getBooleanExtra("bool",false);


        storage= FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("users/"+auth.getCurrentUser().getUid()+"/dashboard");
        databaseReference= FirebaseDatabase.getInstance().getReference("users/"+auth.getCurrentUser().getUid()+"/dashboard");


        final String[] username1 = new String[1];
        final String[] email1 = new String[1];
        final String[] phone1 = new String[1];
        final String[] profession1 = new String[1];
        String UID;
        FirebaseUser user= auth.getCurrentUser();
        User userOb=new User();
        final String[] username = {""};
        final String[] email = {""};
        final String[] phone = {""};
        final String[] profession = {""};

        if(b ){
                String[] extras = getIntent().getStringArrayExtra("user object");
             username[0] = extras[0];
              email[0] = extras[1];
             phone[0] = extras[2];
              profession[0] = extras[3];
                UID= extras[4];
            userOb=new User(username[0], email[0], phone[0], profession[0]);}

        else {

            UID= user.getUid();
        }



            count++;
        User finalUserOb = userOb;
        MenuItem finalProfileItem = profileItem;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.hasChild(UID)){
                        reference.child(UID+"/credentials").setValue(finalUserOb);
                    }else{
                        username1[0] = (String) snapshot.child(UID+"/credentials/username").getValue();
                        email1[0] = (String) snapshot.child(UID+"/credentials/email").getValue();
                        phone1[0] = (String) snapshot.child(UID+"/credentials/phoneNumber").getValue();
                        profession1[0] = (String) snapshot.child(UID+"/credentials/profession").getValue();
                    }
                    if(!b){
                        username[0] =username1[0];
                        email[0] = email1[0];
                        phone[0] = phone1[0];
                        profession[0] = profession1[0];
                    }

                    if(!username[0].isEmpty()){
                        finalProfileItem.setTitle("username: "+ username[0]);
                    }
                    if(email[0]==null){
                        email[0]="login through facebook";
                    }

                    if(phone[0]==null){
                        phone[0]="login through facebook";
                    }
                    if(!email[0].isEmpty()){
                        emailItem.setTitle("email :"+ email[0]);
                    }else{
                        emailItem.setTitle("email : login through facebook");
                    }
                    if(!phone[0].isEmpty()){
                        phoneItem.setTitle("phone :"+ phone[0]);
                    }else{
                        phoneItem.setTitle("phone : login through facebook");
                    }
                    if(!profession[0].isEmpty()){
                        professionItem.setTitle("profession: "+ profession[0]);
                    }else{
                        professionItem.setTitle("profession : login through facebook");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        items=new ArrayList<>();

        adapter= new DashboardAdapter(items,this);
        GridLayoutManager manager= new GridLayoutManager(this,2,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        DBListener =databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for(DataSnapshot category : snapshot.getChildren()){
                    DashboardItem upload = category.getValue(DashboardItem.class);
                    upload.setKey(category.getKey());
                    items.add(upload);
                }
                adapter.notifyDataSetChanged();
                circle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                circle.setVisibility(View.INVISIBLE);
                Toast.makeText(DashboardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_addItem:
                openDialog();
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                auth.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(DashboardActivity.this,LoginActivity.class));
                finish();
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openDialog() {
        DialogBox d= new DialogBox();
        d.show(getSupportFragmentManager(),"category dialog");
    }


    @Override
    public void apply(String title, Uri imageUri) {
        progressBar= new ProgressDialog(this);
        progressBar.show();
        progressBar.setContentView(R.layout.progress_dialog);
        progressBar.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );


        uploadFile(title, imageUri);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));

    }

    private void uploadFile(String title, Uri imageUri) {
        String  time= System.currentTimeMillis()+"";
        if(imageUri!=null){
            StorageReference fileRef= storageReference.child(time+"."+getFileExtension(imageUri));
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    DashboardItem upload = new DashboardItem(title.trim(),uri.toString());
                                    databaseReference.child(time).setValue(upload);

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            progressBar.dismiss();
                                        }
                                    },1000);


                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            progressBar.dismiss();
                                            Toast.makeText(DashboardActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                        }
                                    });



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.dismiss();
                            Toast.makeText(DashboardActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

        }
        else{

            DashboardItem upload = new DashboardItem(title.trim(),"https://acadianakarate.com/wp-content/uploads/2017/04/default-image.jpg");
            databaseReference.child(time).setValue(upload);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.dismiss();
                }
            },1000);
        }
    }
}