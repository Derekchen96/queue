package com.bignerdranch.android.queueApp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.queueApp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;

public class ServiceProviderMenu extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserName, textViewUserEmail, textViewUserCompany, pagetitle, pagesubtitle;
    private Button buttonLogout;
    LinearLayout LLSPList, LLStartService, LLUserProfile;
    Animation atg, atgtwo, atgthree;
    ImageView imageView3, mProfileImage;
    String emailA;
    private String currentUserEmail, currentUserName, currentUserID, currentCompanyName;
    final Activity activity= this;
    final Context context = this;
    private String mProfileImageUrl;
    private DatabaseReference mCustomerDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_menu);

        atg = AnimationUtils.loadAnimation(this, R.anim.atg);
        atgtwo = AnimationUtils.loadAnimation(this, R.anim.atgtwo);
        atgthree = AnimationUtils.loadAnimation(this, R.anim.atgthree);
        pagetitle = findViewById(R.id.pagetitle);
        pagesubtitle = findViewById(R.id.pagesubtitle);
        LLSPList = findViewById(R.id.LLSPList);
        LLUserProfile = findViewById(R.id.LLUserProfile);
        LLStartService = findViewById(R.id.LLSService);
        mProfileImage= findViewById(R.id.imageView2);


        buttonLogout= (Button) findViewById(R.id.buttonLogout);
        imageView3 = findViewById(R.id.imageView3);
        textViewUserName= (TextView) findViewById(R.id.nameuser);
        textViewUserEmail = (TextView) findViewById(R.id.emailuser);
        textViewUserCompany = (TextView) findViewById(R.id.CpName);


        firebaseAuth = FirebaseAuth.getInstance();


        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();


        currentUserEmail=user.getEmail();
        currentUserID=user.getUid();
        textViewUserEmail.setText(currentUserEmail);

        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        currentUserName = map.get("name").toString();
                        textViewUserName.setText(currentUserName);
                    }
                    if(map.get("companyName")!=null){
                        currentCompanyName = map.get("companyName").toString();
                        textViewUserCompany.setText(currentCompanyName);
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

        imageView3.startAnimation(atg);

        pagetitle.startAnimation(atgtwo);
        pagesubtitle.startAnimation(atgtwo);

        buttonLogout.startAnimation(atgthree);

        LLStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent UserIntent = new Intent(getApplicationContext(),SPChooseService.class);
                UserIntent.putExtra("CompanyName", currentCompanyName);
                startActivity(UserIntent);
            }
        });

        LLSPList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent UserIntent = new Intent(getApplicationContext(),CompanyProfileActivity.class);
                UserIntent.putExtra("UserCompany", currentCompanyName);
                startActivity(UserIntent);
            }
        });

        LLUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent UserIntent = new Intent(getApplicationContext(),ProfileSettingActivity.class);
                UserIntent.putExtra("UserID",currentUserID);
                startActivity(UserIntent);
            }
        });



        buttonLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_logout);
                dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                //TextView text = (TextView) dialog.findViewById(R.id.text);
                //text.setText("Android custom dialog example!");
                //ImageView image = (ImageView) dialog.findViewById(R.id.image);
                //image.setImageResource(R.drawable.userphoto);

                Button YesButton = dialog.findViewById(R.id.ok);
                // if button is clicked, close the custom dialog
                YesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        signOut();

                    }
                });

                dialog.show();
            }
        });

    }


    public void signOut() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(intentResult != null){
            if (intentResult.getContents() !=  null){
                alert(intentResult.getContents().toString());
                String currentCompanyName = intentResult.getContents().toString();

                Intent CompanyIntent = new Intent(getApplicationContext(),ServiceActivity.class);
                CompanyIntent.putExtra("CompanyName",currentCompanyName);
                CompanyIntent.putExtra("UserEmail",currentUserEmail);
                CompanyIntent.putExtra("UserName",currentUserName);
                startActivity(CompanyIntent);
            }else{
                alert("Scan cancel");
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
    private void alert(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View v) {

    }
}
