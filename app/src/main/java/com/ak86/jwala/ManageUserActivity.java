package com.ak86.jwala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ak86.jwala.models.User;
import com.ak86.jwala.util.Validator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ManageUserActivity extends AppCompatActivity {

    private TableLayout usersTable;
    private DatabaseReference userListDR;
    private View editView;
    private ProgressBar progressBarManageUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        usersTable = findViewById(R.id.usersTable);
        progressBarManageUsers = findViewById(R.id.progressBarManageUsers);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Manage Users");
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(Html.fromHtml("<small>Long Press to Edit</small>"));
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);



        userListDR = FirebaseDatabase.getInstance().getReference();
        userListDR.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateUsersTable(snapshot);
                if(progressBarManageUsers!=null){
                    progressBarManageUsers.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void populateUsersTable(final DataSnapshot snapshot){
        usersTable.removeAllViews();
        initializeTableHeader();
        int slNoCounter = 1;
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        for(final DataSnapshot users : snapshot.getChildren()){
            final User user = users.getValue(User.class);
            TableRow tableRow = new TableRow(getApplicationContext());
            //*********************** SERIAL NUMBER ************************//*
            TextView textViewSlNo = new TextView(getApplicationContext());
            textViewSlNo.setText(String.valueOf(slNoCounter));
            textViewSlNo.setGravity(Gravity.CENTER);
            slNoCounter++;
            tableRow.addView(textViewSlNo);
            //*********************** USER NAME ************************//*
            TextView userName = new TextView(getApplicationContext());
            userName.setText(Validator.decodeFromFirebaseKey(user.getUsername()));
            userName.setHeight(70);
            userName.setGravity(Gravity.CENTER);
            tableRow.addView(userName);
            //*********************** EMAIL DESCR ************************//*
            TextView emailID = new TextView(getApplicationContext());
            emailID.setText(Validator.decodeFromFirebaseKey(user.getEmailId()));
            emailID.setGravity(Gravity.CENTER);
            tableRow.addView(emailID);
            //*********************** BLOCK SIZE ************************//*
            TextView userLevel = new TextView(getApplicationContext());
            userLevel.setText(user.getLevel());
            userLevel.setGravity(Gravity.CENTER);
            tableRow.addView(userLevel);
            /***********************  ADD A DELETE BUTTON************************/
           /* Button actionButton = new Button(getApplicationContext());
            actionButton.setText("Delete");
            actionButton.setTextSize(8);
            actionButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteBlock(block.getBlockName());
                }
            });
            tableRow.addView(actionButton);*/
            //A long click pops up an edit menu
            tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View v) {
                                                    popupAndEditUser(user.getUsername(), user);
                                                    return true;
                                                }
                                            }
            );
            tableRow.setMinimumHeight(50);
            tableRow.setElevation(4);
            tableRow.setBackgroundColor(Color.argb(100,236,235,232));
            tableRow.setLayoutParams(lp);
            usersTable.addView(tableRow,lp);
        }
    }
    private void initializeTableHeader(){
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        TableRow headerRow = new TableRow(getApplicationContext());
        TextView slno = new TextView(getApplicationContext());
        slno.setText(" S.No ");
        slno.setTextColor(Color.parseColor("white"));
        slno.setGravity(Gravity.CENTER);
        headerRow.addView(slno);
        TextView name = new TextView(getApplicationContext());
        name.setText("     Name     ");
        name.setTextColor(Color.parseColor("white"));
        name.setGravity(Gravity.CENTER);
        headerRow.addView(name);
        TextView email = new TextView(getApplicationContext());
        email.setText("  Email ID  ");
        email.setTextColor(Color.parseColor("white"));
        email.setGravity(Gravity.CENTER);
        headerRow.addView(email);
        TextView userLevel = new TextView(getApplicationContext());
        userLevel.setText("  User Level  ");
        userLevel.setTextColor(Color.parseColor("white"));
        userLevel.setGravity(Gravity.CENTER);
        headerRow.addView(userLevel);
        headerRow.setBackgroundColor(Color.argb(200,33,150,243));
        headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        usersTable.addView(headerRow,lp);

    }

    private void popupAndEditUser(final String userName, User user){
        /*********************populate with existing data. read from cloud and fill up********************************************************************************************/
        LayoutInflater li = LayoutInflater.from(ManageUserActivity.this);
        editView = li.inflate(R.layout.user_level_popup, null);
        final Spinner level = editView.findViewById(R.id.spinnerUserLevel);

        /******When user changes the data existing, then first look at the changes. Then move the people data to ***********************************************************************************************************/
        /******a corresponding node. mov record function, then create a new block with new data, copy details from existing***********************************************************************************************************/
        /******lastly delete the old copies***********************************************************************************************************/
        AlertDialog.Builder builder = new AlertDialog.Builder(ManageUserActivity.this);
        builder.setView(editView);
        builder.setCancelable(false)
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newUserLevel = level.getSelectedItem().toString();
                                user.setLevel(newUserLevel);
                                DatabaseReference userDR = FirebaseDatabase.getInstance().getReference();
                                userDR.child("users").child(userName).setValue(user);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}