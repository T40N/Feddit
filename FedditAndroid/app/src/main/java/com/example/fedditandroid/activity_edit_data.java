package com.example.fedditandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class activity_edit_data extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    private Button confirmBtn;
    private Button backBtn;

    private EditText nameInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        nameInput = (EditText) findViewById(R.id.txt_edit_name);
        passwordInput = (EditText) findViewById(R.id.txt_edit_password);

        backBtn = (Button) findViewById(R.id.returnHomeBtn_2);
        confirmBtn = (Button) findViewById(R.id.confirmEditBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEdit();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_edit_data.this, MainPage.class));
            }
        });
    }

    private void userEdit(){
        String name = nameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(name.isEmpty()){
            passwordInput.setError("Name is required!");
            passwordInput.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordInput.setError("Password is required!");
            passwordInput.requestFocus();
            return;
        }

        if(password.length() < 6){
            passwordInput.setError("Please enter password with length of minimum characters.");
            passwordInput.requestFocus();
            return;
        }

        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String uEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        User user = new User(name, uEmail);
        db = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String newPassword = password;



        firebaseUser.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });

        db.collection("Users").document(uId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                firebaseUser.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Data was changed", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        });
            }
        });
        }

    }
