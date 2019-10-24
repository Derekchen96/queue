package com.bignerdranch.android.queueApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class ServiceActivity extends AppCompatActivity {

    private String currentCompanyName;
    private String currentUserEmail;
    private String currentUserName;
    private ListView listView;

    FirebaseDatabase database;
    DatabaseReference myRef;
    private ArrayList<String> mServiceName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        currentCompanyName = getIntent().getExtras().get("CompanyName").toString();
        getSupportActionBar().setTitle(currentCompanyName); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //currentUserName = getIntent().getExtras().get("UserName").toString();
       // currentUserEmail = getIntent().getExtras().get("UserEmail").toString();


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ServiceProvider").child(currentCompanyName).child("Services");

        listView = (ListView) findViewById(R.id.ServiceListView);
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

                String currentServiceName = adapterView.getItemAtPosition(position).toString();
                long currentServiceNo = adapterView.getItemIdAtPosition(position);

                Intent ServiceIntent = new Intent(getApplicationContext(), CfmTicketActivity.class);
                ServiceIntent.putExtra("ServiceName",currentServiceName);
                ServiceIntent.putExtra("ServiceNo",currentServiceNo);
                ServiceIntent.putExtra("CompanyName",currentCompanyName);
                //ServiceIntent.putExtra("UserName",currentUserName);
                //ServiceIntent.putExtra("UserEmail",currentUserEmail);
                startActivity(ServiceIntent);

            }
        });
    }
}
