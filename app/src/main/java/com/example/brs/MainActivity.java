package com.example.brs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignUpPrompt;
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity.java";
    private FirebaseFirestore db;
    private String emaill,email, password;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
            navigateToMenu(currentUser.getEmail());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUpPrompt = findViewById(R.id.tvSignUpPrompt);

        // Set up click listeners
        btnLogin.setOnClickListener(v -> {
            if (validateLogin()) {
                // Retrieve email and password inside the click listener after validation
                email = etEmail.getText().toString();
                emaill = email;
                password = etPassword.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    navigateToMenu(user.getEmail());
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            }
        });

        tvForgotPassword.setOnClickListener(v -> {
            // Handle forgot password action
            Toast.makeText(MainActivity.this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
        });

        tvSignUpPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });
    }

    private boolean validateLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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

        return true;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(MainActivity.this, "User signed in successfully: " + user.getEmail(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMenu(String userEmail) {
        // Log the userEmail to ensure it is correct
        Log.d(TAG, "Navigating to menu for userEmail: " + userEmail);

        // Retrieve all documents under the email collection and get the CNIC number
        db.collection(userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Log the number of documents found
                            Log.d(TAG, "Documents found: " + task.getResult().size());

                            // Assuming there's only one document in the collection for simplicity
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String cnic = document.getId(); // Get the document ID (CNIC)

                            // Log the retrieved CNIC
                            Log.d(TAG, "Retrieved CNIC: " + cnic);

                            // Proceed to Routes activity with CNIC as an extra
                            Intent intent = new Intent(MainActivity.this, menu.class);
                            intent.putExtra("cnic", cnic);
                            intent.putExtra("email", emaill);
                            startActivity(intent);
                            finish(); // Close current activity
                        } else {
                            Log.d(TAG, "No documents found");
                            Toast.makeText(MainActivity.this, "No CNIC document found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        Toast.makeText(MainActivity.this, "Error fetching CNIC document.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
