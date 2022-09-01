package com.example.myprojectagro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myprojectagro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OnboardActivity extends AppCompatActivity {

    private static final String TAG = "onboard_activity";

    private FirebaseAuth mAuth;
    private EditText emailAddressEdit;
    private EditText passwordEdit;
    private Button signInButton;
    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        initUi();
        register();
        signIn();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUi(currentUser);
    }

    private void initUi() {
        emailAddressEdit = findViewById(R.id.email_address_edittext);
        passwordEdit = findViewById(R.id.password_edittext);
        signInButton = findViewById(R.id.button_sign_in);
        signUpButton = findViewById(R.id.button_sign_up);

        mAuth = FirebaseAuth.getInstance();
    }

    private void updateUi(FirebaseUser user) {
        if (user == null) {
            emailAddressEdit.setText("");
            return;
        }
        emailAddressEdit.setText(user.getEmail());
    }

    private void register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUi(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(OnboardActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUi(null);
                    }
                });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUi(user);
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(OnboardActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        updateUi(null);
                    }

                });
    }

    private void signIn() {
        signInButton.setOnClickListener(v -> {
            signIn(emailAddressEdit.getText().toString(), passwordEdit.getText().toString());
        });
    }

    private void register() {
        signUpButton.setOnClickListener(v -> {
            register(emailAddressEdit.getText().toString(), passwordEdit.getText().toString());
        });
    }
}