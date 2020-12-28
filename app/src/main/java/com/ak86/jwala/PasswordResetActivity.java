package com.ak86.jwala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private Button btnBack, btnReset;
    private EditText inputEmail;
    private ProgressBar progressBar;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recover Password");

        inputEmail = findViewById(R.id.forgotPasswordEmail);
        btnReset = findViewById(R.id.btnReset);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.resetPasswordProgressBar);
        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = inputEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(),"Pl enter your registered email id",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                inputEmail.setEnabled(false);

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Pl check your email to reset password",Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }else {
                                    Toast.makeText(getApplicationContext(),"Password reset unsuccessful!",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    inputEmail.setEnabled(true);
                                }
                            }
                        });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
