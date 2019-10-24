package com.bignerdranch.android.queueApp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bignerdranch.android.queueApp.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CompanyProfileActivity extends AppCompatActivity {

    AppBarLayout Appbar;
    CollapsingToolbarLayout CoolToolbar;
    Toolbar toolbar;
    private DatabaseReference mCompanyDatabase;
    private DatabaseReference mServicesDatabase;
    private String currentCompanyName, mAddress, mPhone, mWebsite, mOperatingHour;
    private EditText txtAddress, txtORH, txtPhone, txtWebsite, Services1, Services2, Services3, Services4;
    private FirebaseAuth mAuth;
    private ImageView mEdit;
    private ImageView mCompanyImage;
    private Button mConfirm;
    boolean ExpandedActionBar = true;
    private ProgressDialog progressDialog;
    private String mCompanyImageUrl;
    LinearLayout LLService, sv1L, sv2L, sv3L, sv4L;
    LinearLayout AddressL, PhoneL, WebsiteL, ORHL;

    final Context context = this;
    private Uri resultUri;
    private String sv1, sv2, sv3, sv4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profie);

        currentCompanyName = getIntent().getExtras().get("UserCompany").toString();

        progressDialog= new ProgressDialog(this);

        Appbar = (AppBarLayout)findViewById(R.id.appbar);
        CoolToolbar = (CollapsingToolbarLayout)findViewById(R.id.ctolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtAddress = (EditText) findViewById(R.id.address);
        txtORH = (EditText) findViewById(R.id.ORH);
        txtPhone = (EditText) findViewById(R.id.phone);
        txtWebsite = (EditText) findViewById(R.id.website);
        mEdit = (ImageView) findViewById(R.id.edit);
        mConfirm = (Button) findViewById(R.id.confirm);
        mCompanyImage = (ImageView) findViewById(R.id.CompanyLogo);
        LLService = findViewById(R.id.LLServices);
        AddressL = findViewById(R.id.LLaddress);
        PhoneL = findViewById(R.id.LLphone);
        WebsiteL = findViewById(R.id.LLWebsite);
        ORHL = findViewById(R.id.LLORH);

        txtWebsite.setEnabled(false);
        txtORH.setEnabled(false);
        txtAddress.setEnabled(false);
        txtPhone.setEnabled(false);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) > 200){
                    ExpandedActionBar = false;
                    CoolToolbar.setTitle(currentCompanyName);
                    invalidateOptionsMenu();
                } else {
                    ExpandedActionBar = true;
                    CoolToolbar.setTitle(currentCompanyName);
                    invalidateOptionsMenu();
                }

            }
        });

        mAuth = FirebaseAuth.getInstance();
        mCompanyDatabase = FirebaseDatabase.getInstance().getReference().child("ServiceProvider").child(currentCompanyName).child("Details");

        getCompanyInfo();

        mCompanyImage.setOnClickListener(new View.OnClickListener() {
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

        AddressL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtAddress.setEnabled(true);
                txtAddress.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                int pos = txtAddress.getText().length();
                txtAddress.setSelection(pos);
                String str = txtAddress.getText().toString();
                if(str.equals("")){
                    txtAddress.setText(" ");
                }
            }
        });

        PhoneL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtPhone.setEnabled(true);
                txtPhone.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                int pos = txtPhone.getText().length();
                txtPhone.setSelection(pos);
                String str = txtPhone.getText().toString();
                if(str.equals("")){
                    txtPhone.setText(" ");
                }
            }
        });

        WebsiteL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtWebsite.setEnabled(true);
                txtWebsite.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                int pos = txtWebsite.getText().length();
                txtWebsite.setSelection(pos);
                String str = txtWebsite.getText().toString();
                if(str.equals("")){
                    txtWebsite.setText(" ");
                }
            }
        });

        ORHL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtORH.setEnabled(true);
                txtORH.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                int pos = txtORH.getText().length();
                txtORH.setSelection(pos);
                String str = txtORH.getText().toString();
                if(str.equals("")){
                    txtORH.setText(" ");
                }
            }
        });

        LLService.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dailog_services);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.setTitle("Title...");

                Button YesButton = dialog.findViewById(R.id.Save);
                ImageView Back = dialog.findViewById(R.id.back);
                sv1L = (LinearLayout) dialog.findViewById(R.id.LLsv1);
                sv2L = (LinearLayout) dialog.findViewById(R.id.LLsv2);
                sv3L = (LinearLayout) dialog.findViewById(R.id.LLsv3);
                sv4L = (LinearLayout) dialog.findViewById(R.id.LLsv4);
                Services1 = (EditText) dialog.findViewById(R.id.services1);
                Services2 = (EditText) dialog.findViewById(R.id.services2);
                Services3 = (EditText) dialog.findViewById(R.id.services3);
                Services4 = (EditText) dialog.findViewById(R.id.services4);

                mAuth = FirebaseAuth.getInstance();
                mServicesDatabase = FirebaseDatabase.getInstance().getReference().child("ServiceProvider").child(currentCompanyName).child("Services");
                mServicesDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dts) {
                        if (dts.exists() && dts.getChildrenCount() > 0) {
                            Map<String, Object> sv = (Map<String, Object>) dts.getValue();
                            if (sv.get("Sone") != null) {
                                sv1 = sv.get("Sone").toString();
                                Services1.setText(sv1);
                            }
                            if (sv.get("Stwo") != null) {
                                sv2 = sv.get("Stwo").toString();
                                Services2.setText(sv2);
                            }
                            if (sv.get("Sthree") != null) {
                                sv3 = sv.get("Sthree").toString();
                                Services3.setText(sv3);
                            }
                            if (sv.get("Sfour") != null) {
                                sv4 = sv.get("Sfour").toString();
                                Services4.setText(sv4);
                            }
                          }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                // if button is clicked, close the custom dialog
                YesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.setMessage("Saving information....");
                        progressDialog.show();

                        sv1 = Services1.getText().toString();
                        sv2 = Services2.getText().toString();
                        sv3 = Services3.getText().toString();
                        sv4 = Services4.getText().toString();

                        Map ServiceInfo = new HashMap();
                        ServiceInfo.put("Sone", sv1);
                        ServiceInfo.put("Stwo", sv2);
                        ServiceInfo.put("Sthree", sv3);
                        ServiceInfo.put("Sfour", sv4);
                        mServicesDatabase.updateChildren(ServiceInfo);
                        dialog.dismiss();
                        progressDialog.dismiss();
                    }
                });

                Back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });

                sv1L.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Services1.setEnabled(true);
                        Services1.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        int pos = Services1.getText().length();
                        Services1.setSelection(pos);
                        String str = Services1.getText().toString();
                        if(str.equals("")){
                            Services1.setText("Service");
                        }
                    }
                });

                sv2L.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Services2.setEnabled(true);
                        Services2.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        int pos = Services2.getText().length();
                        Services2.setSelection(pos);
                        String str = Services2.getText().toString();
                        if(str.equals("")){
                            Services2.setText("Service");
                        }
                    }
                });

                sv3L.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Services3.setEnabled(true);
                        Services3.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        int pos = Services3.getText().length();
                        Services3.setSelection(pos);
                        String str = Services3.getText().toString();
                        if(str.equals("")){
                            Services3.setText("Service");
                        }
                    }
                });

                sv4L.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Services4.setEnabled(true);
                        Services4.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        int pos = Services4.getText().length();
                        Services4.setSelection(pos);
                        String str = Services4.getText().toString();
                        if(str.equals("")){
                            Services4.setText("Service");
                        }
                    }
                });


                dialog.show();
            }
        });

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtAddress.setEnabled(true);
                txtAddress.requestFocus();
                txtORH.setEnabled(true);
                txtORH.requestFocus();
                txtPhone.setEnabled(true);
                txtPhone.requestFocus();
                txtWebsite.setEnabled(true);
                txtWebsite.requestFocus();
                mConfirm.setVisibility(View.VISIBLE);
            }
        });


        //Thanks for watching and keep learning with ClipCodes on Youtube

    }


    private void getCompanyInfo() {
        mCompanyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Address") != null) {
                        mAddress = map.get("Address").toString();
                        txtAddress.setText(mAddress);
                    }
                    if (map.get("Operating hours") != null) {
                        mOperatingHour = map.get("Operating hours").toString();
                        txtORH.setText(mOperatingHour);
                    }
                    if (map.get("Phone") != null) {
                        mPhone = map.get("Phone").toString();
                        txtPhone.setText(mPhone);
                    }
                    if (map.get("Website") != null) {
                        mWebsite = map.get("Website").toString();
                        txtWebsite.setText(mWebsite);
                    }
                    if(map.get("CompanyLogoImageUrl")!=null){
                        mCompanyImageUrl = map.get("CompanyLogoImageUrl").toString();
                        Glide.with(getApplication())
                                .load(mCompanyImageUrl)
                                .into(mCompanyImage);
                    }
                    else {

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

       private void saveUserInformation() {
        mAddress = txtAddress.getText().toString();
        mPhone = txtPhone.getText().toString();
        mOperatingHour = txtORH.getText().toString();
        mWebsite = txtWebsite.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("Address", mAddress);
        userInfo.put("Phone", mPhone);
        userInfo.put("Website", mWebsite);
        userInfo.put("Operating hours", mOperatingHour);
        mCompanyDatabase.updateChildren(userInfo);

        if(resultUri != null) {

            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("companyLogo_images").child(currentCompanyName);
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
                            newImage.put("CompanyLogoImageUrl", uri.toString());
                            mCompanyDatabase.updateChildren(newImage);


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
            mCompanyImage.setImageURI(resultUri);
            //mProfileImageUrl = map.get("profileImageUrl").toString();
            Glide.with(getApplication())
                    .load(resultUri)
                    .into(mCompanyImage);
        }
    }
}
