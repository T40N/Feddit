package com.example.fedditandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText fullNameInput, emailInput, passwordInput;
    private ProgressBar progressBar;
    private Button registerAcceptButton;
    private Button backToLoginPageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        fullNameInput = (EditText) findViewById(R.id.fullNameRegisterID);
        emailInput = (EditText) findViewById(R.id.emailRegisterID);
        passwordInput = (EditText) findViewById(R.id.passwordRegisterID);

        progressBar = (ProgressBar) findViewById(R.id.progressBarRegisterID);
        progressBar.setVisibility(View.INVISIBLE);

        registerAcceptButton = (Button) findViewById(R.id.registerAcceptButtonID);
        backToLoginPageButton = (Button) findViewById(R.id.backToLoginButtonID);

        registerAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        backToLoginPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterUser.this, MainActivity.class));
            }
        });

    }

    private void registerUser() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(fullName.isEmpty()){
            fullNameInput.setError("Full name is required!");
            fullNameInput.requestFocus();
            return;
        }

        if(email.isEmpty()){
            emailInput.setError("Email is required!");
            emailInput.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setError("Please enter valid email adress.");
            emailInput.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordInput.setError("Password is required!");
            passwordInput.requestFocus();
            return;
        }

        if(password.length() < 6){
            passwordInput.setError("Please enter password with length of minimum characters");
            passwordInput.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(fullName, email);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.VISIBLE);

                                                startActivity(new Intent(RegisterUser.this, MainActivity.class));
                                            }else{
                                                Toast.makeText(RegisterUser.this, "Failed to create new user!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(RegisterUser.this, "Failed to register!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });



    }
}