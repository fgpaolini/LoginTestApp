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
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgotPassword;
    private EditText editTextEmail, editTextLoginPass;
    private Button signIn;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.loginButton);
        signIn.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.editTextLoginEmail);
        editTextLoginPass = (EditText) findViewById(R.id.editTextLoginPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
        mAuth = FirebaseAuth.getInstance();

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.register:
                startActivity(new Intent(this, RegisterUser.class));
                break;
            case R.id.loginButton:
                userLogin();
              //  startActivity(new Intent());
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));

        }

    }

    private void userLogin() {

        String email = editTextEmail.getText().toString().trim();
        String pass = editTextLoginPass.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email es requerido!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Introduzca un email valido!");
            editTextEmail.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            editTextLoginPass.setError("La contraseña es requerida!");
            editTextLoginPass.requestFocus();
            return;
        }
        if (pass.length() <6) {
            editTextLoginPass.setError("La contraseña debe tener mínimo 6 caracteres!");
            editTextLoginPass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()) {
                        //Redirigir a pantalla
                        Toast.makeText(MainActivity.this, "Usuario logeado con exito!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        finish();

                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Revisa tu email para verificar tu cuenta.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "No se ha podido iniciar sesión. Comprueba tus credenciales!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });


    }
}