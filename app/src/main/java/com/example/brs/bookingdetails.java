package com.example.brs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class bookingdetails extends AppCompatActivity {

    private static final String TAG = "bookingdetails";
    private LinearLayout cardContainer;
    private FirebaseFirestore firestore;
    private String cnic_id, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookingdetails);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize LinearLayout for card views
        cardContainer = findViewById(R.id.cardContainer);

        // Retrieve intent data
        Intent intent = getIntent(); // Retrieve the intent that started this activity
        cnic_id = intent.getStringExtra("cnic"); // Retrieve "cnic" extra
        email = intent.getStringExtra("email"); // Retrieve "email" extra

        // Check if cnic_id and email are not null
        if (email != null && cnic_id != null) {
            // Retrieve data from Firestore based on email and cnic_id
            firestore.collection(email).document(cnic_id)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> bookingDetails = document.getData();
                                if (bookingDetails != null) {
                                    displayBookingDetails(bookingDetails);
                                } else {
                                    Log.d(TAG, "No booking details found");
                                    Toast.makeText(bookingdetails.this, "No booking details found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "No such document");
                                Toast.makeText(bookingdetails.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            Toast.makeText(bookingdetails.this, "Error retrieving document", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle case where email or cnic_id is null
            Log.d(TAG, "Email or CNIC ID is null");
            Toast.makeText(bookingdetails.this, "Email or CNIC ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayBookingDetails(Map<String, Object> bookingDetails) {
        // Iterate over each key (card) in the booking details map
        for (Map.Entry<String, Object> entry : bookingDetails.entrySet()) {
            String key = entry.getKey();
            Map<String, Object> userDetails = (Map<String, Object>) entry.getValue();

            // Create a card view dynamically
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int margin = getResources().getDimensionPixelSize(R.dimen.card_margin);
            cardLayoutParams.setMargins(margin, margin, margin, margin);
            cardView.setLayoutParams(cardLayoutParams);

            // Set card view attributes
            float elevation = getResources().getDimension(R.dimen.card_elevation);
            cardView.setCardElevation(elevation);
            float cornerRadius = getResources().getDimension(R.dimen.card_corner_radius);
            cardView.setRadius(cornerRadius);

            // Create a linear layout for each card
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            int paddingH = getResources().getDimensionPixelSize(R.dimen.card_padding_horizontal);
            int paddingV = getResources().getDimensionPixelSize(R.dimen.card_padding_vertical);
            linearLayout.setPadding(paddingH, paddingV, paddingH, paddingV);

            // Populate the linear layout with TextViews
            for (Map.Entry<String, Object> userEntry : userDetails.entrySet()) {
                String label = userEntry.getKey();
                String value = String.valueOf(userEntry.getValue());

                TextView textView = new TextView(this);
                textView.setText(label + ": " + value);
                int textSize = getResources().getDimensionPixelSize(R.dimen.text_size);
                textView.setTextSize(textSize);
                linearLayout.addView(textView);
            }

            // Add linear layout to card view
            cardView.addView(linearLayout);

            // Add card view to card container
            cardContainer.addView(cardView);
        }
    }
}
