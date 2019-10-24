package com.bignerdranch.android.queueApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.queueApp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class CfmTicketActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtname,txtcontact, txtTime, txtService, txtServiceP, txtCancelRequest;
    Button btnSave;
    DatabaseReference reff;
    Ticket ticket;
    long maxid=0;
    Animation atg;
    private String currentServiceName;
    private long currentServiceNo;
    private String currentCompany;
    private String currentUserName;
    private String currentUserID;
    private String currentUserEmail;
    LinearLayout TicketPaper;
    private DatabaseReference mCustomerDatabase;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        getSupportActionBar().setTitle("Pre-Ticket"); // for set actionbar title


        atg = AnimationUtils.loadAnimation(this, R.anim.atg);
        txtCancelRequest=(TextView)  findViewById(R.id.txtCancel);
        txtname=(TextView) findViewById(R.id.txtname);
        txtcontact=(TextView) findViewById(R.id.txtcontact);
        txtTime=(TextView) findViewById((R.id.txtTime));
        txtService=(TextView) findViewById(R.id.txtService);
        txtServiceP=(TextView) findViewById(R.id.txtServiceP);
        btnSave=(Button)findViewById(R.id.btnSave);
        TicketPaper=(LinearLayout) findViewById(R.id.TicketPaper);


        TicketPaper.startAnimation(atg);


        GregorianCalendar gCalender = new GregorianCalendar();
        String Date = String.valueOf(gCalender.get(Calendar.DATE));
        String Month = String.valueOf(gCalender.get(Calendar.MONTH));
        String Year = String.valueOf(gCalender.get(Calendar.YEAR));
        String hour = String.valueOf(gCalender.get(gCalender.HOUR));
        String min = String.valueOf(gCalender.get(gCalender.MINUTE));


        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        currentUserID=user.getUid();
        currentUserEmail=user.getEmail();

        currentServiceName = getIntent().getExtras().get("ServiceName").toString();
        currentCompany = getIntent().getExtras().get("CompanyName").toString();
        currentServiceNo = getIntent().getLongExtra("ServiceNo", 0);

        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        currentUserName = map.get("name").toString();
                        txtname.setText(currentUserName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        txtService.setText(currentServiceName);
        txtServiceP.setText(currentCompany);
        txtTime.setText(Date +"/"+ Month +"/"+ Year +"   "+ hour +":"+ min);
        txtcontact.setText(String.valueOf(currentServiceNo));
        txtCancelRequest.setText(R.string.btn_cancel_ticket);

        txtCancelRequest.setOnClickListener(this);

        ticket=new Ticket();
        reff= FirebaseDatabase.getInstance().getReference().child("Queue").child(currentCompany).child(currentServiceName);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    maxid=(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 ticket.setName(currentUserEmail);
                 ticket.setContact(txtcontact.getText().toString().trim());
                 ticket.setTime(txtTime.getText().toString().trim());
                 ticket.setService(txtService.getText().toString().trim());
                 ticket.setServiceP(txtServiceP.getText().toString().trim());

                reff.child(String.valueOf((currentServiceNo*1000) + maxid + 1001)).setValue(ticket);

                Toast.makeText(CfmTicketActivity.this,"Data Inserted successfully",Toast.LENGTH_LONG).show();

                Intent ServiceIntent = new Intent(getApplicationContext(), TicketActivity.class);
                ServiceIntent.putExtra("ServiceName",currentServiceName);
                ServiceIntent.putExtra("CompanyName",currentCompany);
                ServiceIntent.putExtra("UserEmail",currentUserEmail);
                startActivity(ServiceIntent);

            }
        });
    }

    public void onClick(View v) {
               if(v==txtCancelRequest){
            finish();
            startActivity(new Intent(this, MenuActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"This session not allowed to go back, please press comfirm or Cancel to continue ",Toast.LENGTH_SHORT).show();

    }
}

