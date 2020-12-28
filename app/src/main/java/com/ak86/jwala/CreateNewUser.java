package com.ak86.jwala;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ak86.jwala.models.User;
import com.ak86.jwala.util.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CreateNewUser extends AppCompatActivity {

    private static final String TAG = "CREATENEWUSER";
    private EditText fdUserName, fdEmailId, fdPassword, fdConfirmPassword;
    private Button btnCreateUser;
    private boolean okFLAG;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private User newUser = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create New User");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fdUserName = findViewById(R.id.fdUserName);
        fdEmailId =  findViewById(R.id.fdUserEmailID);
        fdPassword = findViewById(R.id.fdPassword);
        fdConfirmPassword = findViewById(R.id.fdConfirmPassword);
        progressBar = findViewById(R.id.progressBar);
        btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okFLAG = true;
                if (TextUtils.isEmpty(fdUserName.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Pl enter a username!", Toast.LENGTH_SHORT).show();
                    okFLAG = false;
                    return;
                }
                if (TextUtils.isEmpty(fdEmailId.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Pl enter a valid email!", Toast.LENGTH_SHORT).show();
                    okFLAG  = false;
                    return;
                }
                if (TextUtils.isEmpty(fdPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Pl enter a password!", Toast.LENGTH_SHORT).show();
                    okFLAG  = false;
                    return;
                }
                if (TextUtils.isEmpty(fdConfirmPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Pl retype your password!", Toast.LENGTH_SHORT).show();
                    okFLAG  = false;
                    return;
                }
                if(!(fdPassword.getText().toString().equals(fdConfirmPassword.getText().toString()))) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    okFLAG = false;
                    return;
                }
                if (okFLAG) {
                    newUser.setUsername(Validator.encodeForFirebaseKey(fdUserName.getText().toString()));
                    newUser.setEmailId(Validator.encodeForFirebaseKey(fdEmailId.getText().toString()));
                    newUser.setUserLevel(0);
                    mAuth.createUserWithEmailAndPassword(fdEmailId.getText().toString(),fdPassword.getText().toString())
                            .addOnCompleteListener(CreateNewUser.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        alertDialog();
                                        databaseReference.child("users").child(fdUserName.getText().toString()).setValue(newUser);
                                    }else {
                                        Toast.makeText(CreateNewUser.this,"Failed to create new account.",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        finish();
                                    }
                                }
                            });
                    if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    private void alertDialog(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("Successfully created new user " +mAuth.getCurrentUser().getEmail());
        dialog.setTitle("Success");
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }
}

