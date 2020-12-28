package com.ak86.jwala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        getSupportActionBar().setIcon(R.drawable.ic_action_name);
        getSupportActionBar().setTitle("Jwala");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnSignIn =  this.findViewById(R.id.login);

        auth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                inputEmail.setText("");
                inputPassword.setText("");
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Enter your email address!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Enter your password!",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar = findViewById(R.id.progressBar);
                if (progressBar != null)  {
                    progressBar.setVisibility(View.VISIBLE);
                }
                inputEmail.setEnabled(false);
                inputPassword.setEnabled(false);

                auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                inputPassword.setEnabled(true);
                                inputEmail.setEnabled(true);

                                if(!task.isSuccessful()){
                                    if(password.length() < 6){
                                        inputPassword.setError("Min password lenght is 6");
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Authentication Failed!",Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                } else {

                                    DatabaseReference currentUserAuthLevelDR = FirebaseDatabase.getInstance().getReference();
                                    currentUserAuthLevelDR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot users : snapshot.getChildren()){
                                                User user = users.getValue(User.class);
                                                if(auth.getCurrentUser().getEmail().equals(Validator.decodeFromFirebaseKey(user.getEmailId()))){
                                                    Toast.makeText(getApplicationContext(),auth.getCurrentUser().getEmail()+" logged in!",Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
                                                    intent.putExtra("currentUserAuthLevel",user.getUserLevel());
                                                    Log.println(Log.ASSERT,"ALOK",String.valueOf(user.getUserLevel()));
                                                    progressBar.setVisibility(View.GONE);
                                                    startActivity(intent);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }

                            }
                        });
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.login_menu,menu);
        MenuItem password = menu.findItem(R.id.passwordRecover);
        MenuItem create = menu.findItem(R.id.createAccount);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.passwordRecover :
                Intent intentPass = new Intent(getApplicationContext(), PasswordResetActivity.class);
                startActivity(intentPass);
                return true;
            case R.id.createAccount :
                Intent intentAcc = new Intent(getApplicationContext(), CreateNewUser.class);
                startActivity(intentAcc);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}