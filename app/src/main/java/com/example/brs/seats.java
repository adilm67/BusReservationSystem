package com.example.brs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class seats extends AppCompatActivity {

    private GridView gridViewSeats;
    private List<String> seatNumbers;
    private Button btnhome, btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seats);

        gridViewSeats = findViewById(R.id.gridViewSeats);

        btnhome = findViewById(R.id.btnHome);
        btnback = findViewById(R.id.btnBack);

        Intent intents = getIntent();
        // Retrieve data from intent
        String cnic_id = intents.getStringExtra("cnic_id");
        String email_coll = intents.getStringExtra("email_coll");
        String route = getIntent().getStringExtra("route");
        String time = getIntent().getStringExtra("time");
        int fare = getIntent().getIntExtra("fare", 0);
        String date = getIntent().getStringExtra("date");
        boolean isRoundTrip = getIntent().getBooleanExtra("isRoundTrip", false);

        // Display or use the retrieved data as needed
        Toast.makeText(this, "Route: " + route + ", Time: " + time + ", Fare: " + fare, Toast.LENGTH_LONG).show();

        // Initialize seat numbers
        seatNumbers = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            seatNumbers.add("Seat " + i);
        }

        // Convert seatNumbers list to array
        String[] seatsArray = seatNumbers.toArray(new String[0]);

        // Set the adapter
        SeatsAdapter adapter = new SeatsAdapter(this, seatsArray, cnic_id, email_coll, route, time, fare, date, isRoundTrip);
        gridViewSeats.setAdapter(adapter);

        btnhome.setOnClickListener(v -> {
            Intent intent = new Intent(seats.this, menu.class);
            startActivity(intent);
            finish();
        });

        btnback.setOnClickListener(v -> {
            Intent intent = new Intent(seats.this, Routes.class);
            startActivity(intent);
            finish();
        });
    }
}
