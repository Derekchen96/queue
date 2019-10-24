package com.bignerdranch.android.queueApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.queueApp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TicketActivity extends AppCompatActivity {

    private String currentServiceName;
    private String currentCompany;
    private String currentUserEmail;
    private String ticNum;
    Animation atg;
    Button Cancel;
    TextView txtname,txtcontact, txtTime, txtService, txtServiceP, txtTicketNumber;
    LinearLayout PpaperTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket2);

        atg = AnimationUtils.loadAnimation(this, R.anim.atg);
        txtTicketNumber=(TextView) findViewById(R.id.txtTicketNumber);
        txtname=(TextView) findViewById(R.id.txtname);
        txtcontact=(TextView) findViewById(R.id.txtcontact);
        txtTime=(TextView) findViewById((R.id.txtTime));
        txtService=(TextView) findViewById(R.id.txtService);
        txtServiceP=(TextView) findViewById(R.id.txtServiceP);
        PpaperTicket=(LinearLayout)  findViewById(R.id.PpaperTicket);
        Cancel=(Button) findViewById(R.id.btnCancel);

        PpaperTicket.startAnimation(atg);


        currentServiceName = getIntent().getExtras().get("ServiceName").toString();
        currentCompany = getIntent().getExtras().get("CompanyName").toString();
        currentUserEmail = getIntent().getExtras().get("UserEmail").toString();

        txtService.setText(currentServiceName);
        txtServiceP.setText(currentCompany);
        txtname.setText(currentUserEmail);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Queue").child(currentCompany).child(currentServiceName);
        ref.orderByChild("name").equalTo(currentUserEmail).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String keys = ds.getKey();
                    txtTicketNumber.setText(keys);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"You are not allowed to go back",Toast.LENGTH_SHORT).show();

    }
 }
