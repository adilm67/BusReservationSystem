package com.example.brs;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class SignUp extends AppCompatActivity {
    private EditText etEmail, cnic, etPassword, etConfirmPassword, selectDate;
    private Button btnSignUp;
    private TextView tvLoginPrompt;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        cnic = findViewById(R.id.cnic);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLoginPrompt = findViewById(R.id.tvLoginPrompt);
        selectDate = findViewById(R.id.selectDate);

        // Set up click listeners
        btnSignUp.setOnClickListener(v -> {
            if (validateSignUp()) {
                // Retrieve email, password, CNIC, and date inside the click listener
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String cnicText = cnic.getText().toString().trim();
                String date = selectDate.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, store additional user information in Firestore
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        storeUserDetails(cnicText, email, date);
                                        updateUI(user);
                                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Log.e(TAG, "User is null after successful sign up.");
                                        Toast.makeText(SignUp.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUp.this, "Authentication failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            }
        });

        // Set listener for date EditText
        selectDate.setOnClickListener(v -> showDatePicker());

        tvLoginPrompt.setOnClickListener(v -> {
            // Navigate back to the main activity (login screen)
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void storeUserDetails(String cnic, String email, String date) {
        // Create a new user with email and date
        User user = new User(email, date);

        db.collection(email).document(cnic)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User details successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing user details", e));
    }

    private void showDatePicker() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SignUp.this,
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

    private boolean validateSignUp() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String cnicText = cnic.getText().toString().trim();
        String date = selectDate.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password should be at least 6 characters long");
            etPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (cnicText.isEmpty()) {
            cnic.setError("CNIC is required");
            cnic.requestFocus();
            return false;
        }

        if (date.isEmpty()) {
            selectDate.setError("Date is required");
            selectDate.requestFocus();
            return false;
        }

        return true;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Navigate to a different activity or update UI to show user is signed in
            Toast.makeText(SignUp.this, "User signed up successfully: " + user.getEmail(), Toast.LENGTH_LONG).show();
        } else {
            // User is null, show authentication failed message
            Toast.makeText(SignUp.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    public class User {
        public String email;
        public String date;

        public User(String email, String date) {
            this.email = email;
            this.date = date;
        }
    }
}
