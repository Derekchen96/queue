package com.bignerdranch.android.queueApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bignerdranch.android.queueApp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileSettingActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mEmailField;
    private ProgressDialog progressDialog;
    private ImageView mBack, mEdit;
    private Button mConfirm;
    LinearLayout emailLL, phoneLL, nameLL;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;

    private String userID;
    private String mName;
    private String mEmail;
    private String mPhone;
    private String mProfileImageUrl;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        progressDialog= new ProgressDialog(this);
        userID = getIntent().getExtras().get("UserID").toString();

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mEmailField = (EditText) findViewById(R.id.email);

        emailLL = (LinearLayout) findViewById(R.id.LLEmail);
        phoneLL = (LinearLayout) findViewById(R.id.LLPhone);


        mNameField.setEnabled(false);
        mPhoneField.setEnabled(false);
        phoneLL.setEnabled(false);
        mEmailField.setEnabled(false);
        emailLL.setEnabled(false);


        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        mBack = (ImageView) findViewById(R.id.back);
        mEdit = (ImageView) findViewById(R.id.edit);
        mConfirm = (Button) findViewById(R.id.confirm);

        mConfirm.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                mConfirm.setVisibility(View.VISIBLE);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Saving information....");
                progressDialog.show();
                saveUserInformation();
            }
        });

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameField.setEnabled(true);
                mEmailField.setEnabled(true);
                emailLL.setEnabled(true);
                mPhoneField.setEnabled(true);
                phoneLL.setEnabled(true);
                mConfirm.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Now you can click on the field to edit, remember to press save button after edit.", Toast.LENGTH_SHORT).show();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });

        emailLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailField.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                int pos = mEmailField.getText().length();
                mEmailField.setSelection(pos);
                String str = mEmailField.getText().toString();
                if(str.equals("")){
                    mEmailField.setText(" ");
                }
            }
        });

        phoneLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneField.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                int pos = mPhoneField.getText().length();
                mPhoneField.setSelection(pos);
                String str = mPhoneField.getText().toString();
                if(str.equals("")){
                    mPhoneField.setText(" ");
                }
            }
        });

        mNameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameField.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                int pos = mNameField.getText().length();
                mNameField.setSelection(pos);
                String str = mNameField.getText().toString();
                if(str.equals("")){
                    mNameField.setText(" ");
                }
            }
        });
    }
    private void getUserInfo(){
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                    }
                    if(map.get("phone")!=null){
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }
                    if(map.get("email")!=null){
                        mEmail = map.get("email").toString();
                        mEmailField.setText(mEmail);
                    }
                    if(map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication())
                                .load(mProfileImageUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    private void saveUserInformation() {
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        userInfo.put("email", mEmail);
        mCustomerDatabase.updateChildren(userInfo);

        if(resultUri != null) {

            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mCustomerDatabase.updateChildren(newImage);


                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }else{
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
            //mProfileImageUrl = map.get("profileImageUrl").toString();
            Glide.with(getApplication())
                    .load(resultUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mProfileImage);
        }
        }
    }

