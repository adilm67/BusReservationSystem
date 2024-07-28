package com.example.brs;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Routes extends AppCompatActivity {

    private int fare;
    private Button btnselectseat, btnhome, btnback;
    private Spinner spin, spin2;
    private EditText fareEditText, selectDate;
    private RadioGroup tripTypeRadioGroup;
    private RadioButton singleTripRadioButton;
    private RadioButton roundTripRadioButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private HashMap<String, Integer> fareMap;

    private String cnic_id, email_coll;

    private String[] users = {"Faizabad Junction to Lahore",
            "Lahore to Faizabad Junction",
            "Faizabad Junction to Gujranwala",
            "Gujranwala to Faizabad Junction",
            "Faizabad Junction to Multan",
            "Multan to Faizabad Junction",
            "Faizabad Junction to Peshawar",
            "Peshawar to Faizabad Junction",
            "Faizabad Junction to Karachi",
            "Karachi to Faizabad Junction",
            "Faizabad Junction to Faislabad",
            "Faislabad to Faizabad Junction"};
    private String[] time = {"1:00 AM", "3:00 AM", "5:00 AM", "7:00 AM", "9:00 AM", "11:00 AM",
            "1:00 PM", "3:00 PM", "5:00 PM", "7:00 PM", "9:00 PM", "11:00 PM"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        // Initialize fare map
        fareMap = new HashMap<>();
        fareMap.put("Faizabad Junction to Lahore", 1845);
        fareMap.put("Lahore to Faizabad Junction", 1845);
        fareMap.put("Faizabad Junction to Gujranwala", 1245);
        fareMap.put("Gujranwala to Faizabad Junction", 1245);
        fareMap.put("Faizabad Junction to Multan", 2655);
        fareMap.put("Multan to Faizabad Junction", 2655);
        fareMap.put("Faizabad Junction to Peshawar", 2210);
        fareMap.put("Peshawar to Faizabad Junction", 2210);
        fareMap.put("Faizabad Junction to Karachi", 3650);
        fareMap.put("Karachi to Faizabad Junction", 3650);
        fareMap.put("Faizabad Junction to Faislabad", 1670);
        fareMap.put("Faislabad to Faizabad Junction", 1670);

        // Initialize views
        spin = findViewById(R.id.routes);
        spin2 = findViewById(R.id.times);
        fareEditText = findViewById(R.id.faretext);
        tripTypeRadioGroup = findViewById(R.id.tripTypeRadioGroup);
        singleTripRadioButton = findViewById(R.id.single);
        roundTripRadioButton = findViewById(R.id.doubletrip);
        btnselectseat = findViewById(R.id.select);
        selectDate = findViewById(R.id.selectDate);
        btnhome = findViewById(R.id.btnHome);
        btnback = findViewById(R.id.btnBack);

        // Initialize spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, time);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(adapter1);

        // Set listener for spinner item selection
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, android.view.View selectedItemView, int position, long id) {
                // Update fare EditText with the selected route's fare
                String selectedRoute = spin.getSelectedItem().toString();
                int fare = fareMap.get(selectedRoute);
                fareEditText.setText(String.valueOf(fare));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        // Set listener for trip type radio group
        tripTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Update fare EditText based on selected trip type
            fare = Integer.parseInt(fareEditText.getText().toString());
            if (checkedId == singleTripRadioButton.getId()) {
                fareEditText.setText(String.valueOf(fare));
            } else if (checkedId == roundTripRadioButton.getId()) {
                fareEditText.setText(String.valueOf(fare * 2));
            }
        });

        // Set listener for date EditText
        selectDate.setOnClickListener(v -> showDatePicker());

        Intent intents = getIntent();
        cnic_id = intents.getStringExtra("cnic_id");
        email_coll = intents.getStringExtra("email_coll");

        btnselectseat.setOnClickListener(v -> {
            if (validateRoute()) {
                String route = spin.getSelectedItem().toString();
                String timess = spin2.getSelectedItem().toString();
                int faretext = Integer.parseInt(fareEditText.getText().toString());
                String selectedDate = selectDate.getText().toString();
                boolean isRoundTrip = roundTripRadioButton.isChecked();

                // Create an Intent to start the seats activity
                Intent intent = new Intent(Routes.this, seats.class);
                intent.putExtra("route", route);
                intent.putExtra("cnic_id", cnic_id);
                intent.putExtra("email_coll", email_coll);
                intent.putExtra("time", timess);
                intent.putExtra("fare", faretext);
                intent.putExtra("date", selectedDate);
                intent.putExtra("isRoundTrip", isRoundTrip);
                startActivity(intent);
            }
        });

        btnhome.setOnClickListener(v -> {
            Intent intent = new Intent(Routes.this, menu.class);
            startActivity(intent);
            finish();
        });

        btnback.setOnClickListener(v -> {
            Intent intent = new Intent(Routes.this, menu.class);
            startActivity(intent);
            finish();
        });
    }

    private void showDatePicker() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Routes.this,
                (view, year, month, dayOfMonth) -> {
                    // Set the selected date in the EditText
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    selectDate.setText(sdf.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Restrict the date picker to only allow future dates within the next 10 days
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        // Show the date picker dialog
        datePickerDialog.show();
    }

    private boolean validateRoute() {
        String route = spin.getSelectedItem().toString().trim();
        String times1 = spin2.getSelectedItem().toString().trim();
        String selectedDate = selectDate.getText().toString().trim();

        if (route.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Select Route !", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (times1.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Select Time !", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Select Date !", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
