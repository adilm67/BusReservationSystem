package com.example.brs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class menu extends AppCompatActivity {

    private Button btnbook, btndetail;
    private String cnic, email; // Declare variables to store cnic and email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Enable edge-to-edge display if you have implemented it

        // Initialize views
        btnbook = findViewById(R.id.create_a_new_ticket);
        btndetail = findViewById(R.id.get_an_existing_ticket);

        // Retrieve intent extras from MainActivity
        Intent intent = getIntent();
        cnic = intent.getStringExtra("cnic");
        email = intent.getStringExtra("email");

        // Set click listeners for buttons
        btnbook.setOnClickListener(v -> {
            Intent bookIntent = new Intent(menu.this, Routes.class);
            bookIntent.putExtra("cnic", cnic);
            bookIntent.putExtra("email_coll", email);
            startActivity(bookIntent);
        });

        //btn for cancel


        //btn for detail
        btndetail.setOnClickListener(v -> {
            Intent intents = new Intent(menu.this, bookingdetails.class);
            intents.putExtra("cnic", cnic);
            intents.putExtra("email", email);
            startActivity(intents);
        });
    }
}
