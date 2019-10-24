package com.bignerdranch.android.queueApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bignerdranch.android.queueApp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SPChooseService extends AppCompatActivity {

    private String CurrentCompanyName;
    private ListView listView;
    FirebaseDatabase database;
    DatabaseReference myRef;
    final Context context = this;
    private ArrayList<String> mServiceName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spchoose_service);
        getSupportActionBar().setTitle("Choose your services"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       CurrentCompanyName = getIntent().getExtras().get("CompanyName").toString();


        //currentUserName = getIntent().getExtras().get("UserName").toString();
        // currentUserEmail = getIntent().getExtras().get("UserEmail").toString();


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ServiceProvider").child(CurrentCompanyName).child("Services");

        listView = (ListView) findViewById(R.id.SPService);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mServiceName);
        listView.setAdapter(arrayAdapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren())
                {
                    mServiceName.add(String.valueOf(dsp.getValue()));

                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_counter_num);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();


                //String currentServiceName = adapterView.getItemAtPosition(position).toString();
                //long currentServiceNo = adapterView.getItemIdAtPosition(position);

                //Intent ServiceIntent = new Intent(getApplicationContext(), CfmTicketActivity.class);
                //ServiceIntent.putExtra("ServiceName",currentServiceName);
               // ServiceIntent.putExtra("ServiceNo",currentServiceNo);
                //ServiceIntent.putExtra("UserName",currentUserName);
                //ServiceIntent.putExtra("UserEmail",currentUserEmail);
               // startActivity(ServiceIntent);

            }
        });
    }
}
