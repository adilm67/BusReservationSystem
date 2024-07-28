package com.example.brs;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Personaldetails extends AppCompatActivity {

    private EditText fullnameEditText, cnicEditText, phoneEditText;
    private Button submitButton, btnhome, btnback;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String route, time, date, cnic_id, email;
    private int fare, seat;
    private boolean isRoundTrip;
    private static final String TAG = "Personaldetails";
    private String etmail, etcnic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaldetails);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        fullnameEditText = findViewById(R.id.fullname);
        cnicEditText = findViewById(R.id.cnic);
        phoneEditText = findViewById(R.id.phone);
        submitButton = findViewById(R.id.submit);
        btnhome = findViewById(R.id.btnHome);
        btnback = findViewById(R.id.btnBack);

        // Retrieve incoming information from intent
        Intent intent = getIntent();
        if (intent != null) {
            seat = intent.getIntExtra("seat", 0);
            cnic_id = intent.getStringExtra("cnic_id");
            email = intent.getStringExtra("email_coll");
            route = intent.getStringExtra("route");
            time = intent.getStringExtra("time");
            date = intent.getStringExtra("date");
            fare = intent.getIntExtra("fare", 0);
            isRoundTrip = intent.getBooleanExtra("isRoundTrip", false);
        }

        submitButton.setOnClickListener(v -> {
            String fullname = fullnameEditText.getText().toString().trim();
            String cnic = cnicEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            if (validateInputs(fullname, cnic, phone)) {
                saveUserAndBookingDetails(fullname, cnic, phone);
            }
        });

        btnhome.setOnClickListener(v -> {
            Intent intents = new Intent(Personaldetails.this, menu.class);
            startActivity(intents);
            finish();
        });

        btnback.setOnClickListener(v -> {
            Intent intents = new Intent(Personaldetails.this, seats.class);
            startActivity(intents);
            finish();
        });
    }

    private boolean validateInputs(String fullname, String cnic, String phone) {
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(getApplicationContext(), "Please enter your full name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(cnic) || cnic.length() != 13) {
            Toast.makeText(getApplicationContext(), "Please enter a valid 13-digit CNIC", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getApplicationContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserAndBookingDetails(String fullname, String cnic, String phone) {
        // Create a map with booking details
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("FullName", fullname);
        userDetails.put("CNIC", cnic);
        userDetails.put("Phone", phone);
        userDetails.put("Route", route);
        userDetails.put("Time", time);
        userDetails.put("Seat", seat);
        userDetails.put("Date", date);
        userDetails.put("Fare", fare);
        userDetails.put("IsRoundTrip", isRoundTrip);
        userDetails.put("Timestamp", FieldValue.serverTimestamp());

        // Create a map with the CNIC as the key
        Map<String, Object> bookingDetails = new HashMap<>();
        bookingDetails.put(cnic, userDetails);

        etmail = "a@gmail.com";
        etcnic = "3410486257141";

        // Update the document with CNIC as the key
        DocumentReference docRef = firestore.collection(etmail).document(etcnic);
        docRef.set(bookingDetails, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Booking added successfully");
                        Toast.makeText(getApplicationContext(), "Booking added successfully", Toast.LENGTH_SHORT).show();
                        navigateToNextActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding booking", e);
                        Toast.makeText(getApplicationContext(), "Error adding booking", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToNextActivity() {
        // Proceed to next activity (e.g., booking details activity)
        Intent intent = new Intent(Personaldetails.this, bookingdetails.class);
        intent.putExtra("email", etmail);
        intent.putExtra("cnic", etcnic);
        startActivity(intent);
        finish();  // Close current activity
    }
}
