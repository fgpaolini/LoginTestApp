package com.example.loginscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;


public class RegisterUser extends AppCompatActivity implements View.OnClickListener {


    private TextView banner, registerUser;
    private EditText editTextName, editTextAge, editTextEmail, editTextPassReg;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // ...
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.loginTextTitle2);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUserButton);
        registerUser.setOnClickListener(this);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAge = (EditText) findViewById(R.id.editTextAge);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassReg = (EditText) findViewById(R.id.editTextPassReg);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginTextTitle2:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerUserButton:
                registerUser();
        }


    }

    private void registerUser() {

        String email = editTextEmail.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String pass = editTextPassReg.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("El nombre es requerido!");
            editTextName.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            editTextAge.setError("La edad es requerido!");
            editTextAge.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("El email es requerido!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Introduce tu email.");
            editTextEmail.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            editTextPassReg.setError("Introduce una contraseña!");
            editTextPassReg.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            editTextPassReg.setError("La contraseña debe tener minimo 6 caracteres");
            editTextPassReg.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    User user = new User(name, age, email);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterUser.this, "Usuario registrado", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                        // REDIRIGIR A LOGIN LAYOUT
                                    } else {
                                        Toast.makeText(RegisterUser.this, "No se ha podido registrar el usuario", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterUser.this, "No se ha podido registrar el usuario", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }
}