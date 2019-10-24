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

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private ArrayList<String> mUsername = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Service Provider"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //currentUserEmail = getIntent().getExtras().get("UserEmail").toString();
        //currentUserName = getIntent().getExtras().get("UserName").toString();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ServiceProvider");

        listView = (ListView) findViewById(R.id.newListView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mUsername);
        listView.setAdapter(arrayAdapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren())
                {
                    mUsername.add(String.valueOf(dsp.getKey()));
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

                String currentCompanyName = adapterView.getItemAtPosition(position).toString();

                Intent CompanyIntent = new Intent(getApplicationContext(),ServiceActivity.class);
                CompanyIntent.putExtra("CompanyName",currentCompanyName);
                //CompanyIntent.putExtra("UserEmail",currentUserEmail);
                //CompanyIntent.putExtra("UserName",currentUserName);
                startActivity(CompanyIntent);

            }
        });

    }

}



