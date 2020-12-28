package com.ak86.jwala.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ak86.jwala.R;
import com.ak86.jwala.util.Validator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RankActivity extends AppCompatActivity {

    private EditText rankFd;
    private Button btnAdd;
    private DatabaseReference writeRankDR, listRankDR, deleteRankDR;
    private TableLayout rankTable;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manage Ranks");
        rankFd = findViewById(R.id.editTextCompany);
        btnAdd  = findViewById(R.id.btnAddCompany);
        progressBar = findViewById(R.id.progressBarCompanies);
        rankTable = findViewById(R.id.companiesTable);
        listRankDR = FirebaseDatabase.getInstance().getReference();
        listRankDR.child("ranks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAllRanksInTable(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeRankDR = FirebaseDatabase.getInstance().getReference();
                writeRankDR.child("ranks").child(Validator.encodeForFirebaseKey(rankFd.getText().toString().trim())).setValue(Validator.encodeForFirebaseKey(rankFd.getText().toString().trim()));
                rankFd.setText("");
            }
        });
    }

    private void initializeTableHeader(){
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        TableRow headerRow = new TableRow(getApplicationContext());
        TextView slno = new TextView(getApplicationContext());
        slno.setText("  S.No  ");
        slno.setTextColor(Color.parseColor("white"));
        slno.setGravity(Gravity.CENTER);
        headerRow.addView(slno);
        TextView rank = new TextView(getApplicationContext());
        rank.setText("   Rank   ");
        rank.setTextColor(Color.parseColor("white"));
        rank.setGravity(Gravity.CENTER);
        headerRow.addView(rank);
        TextView action = new TextView(getApplicationContext());
        action.setText("  Actions  ");
        action.setTextColor(Color.parseColor("white"));
        action.setGravity(Gravity.CENTER);
        headerRow.addView(action);
        headerRow.setLayoutParams(lp);
        headerRow.setBackgroundColor(Color.argb(200,33,150,243));
        rankTable.addView(headerRow,lp);

    }

    private void listAllRanksInTable(DataSnapshot rankSnapshot){
        rankTable.removeAllViews();
        initializeTableHeader();
        int slNoCounter = 1;
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        for(final DataSnapshot snapshot : rankSnapshot.getChildren()){
            final String rank =  Validator.decodeFromFirebaseKey(snapshot.getValue().toString());
            TableRow tableRow = new TableRow(getApplicationContext());
            /*********************** SERIAL NUMBER ************************/
            TextView textViewSlNo = new TextView(getApplicationContext());
            textViewSlNo.setText(String.valueOf(slNoCounter));
            textViewSlNo.setTextColor(Color.argb(255,33,150,243));
            textViewSlNo.setGravity(Gravity.CENTER);
            slNoCounter++;
            tableRow.addView(textViewSlNo);
            /*********************** BARRACK TYPE ************************/
            TextView textViewRank = new TextView(getApplicationContext());
            textViewRank.setText(rank);
            textViewRank.setTextColor(Color.argb(255,33,150,243));
            textViewRank.setGravity(Gravity.CENTER);
            tableRow.addView(textViewRank);
            /***********************  ADD A DELETE BUTTON************************/
            Button actionButton = new Button(getApplicationContext());
            actionButton.setText("Delete");
            actionButton.setTextSize(8);
            actionButton.setElevation(1);
            actionButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            actionButton.setTextColor(Color.argb(255,33,150,243));
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteRank(rank);
                }
            });
            tableRow.addView(actionButton);
            tableRow.setLayoutParams(lp);
            tableRow.setBackgroundColor(Color.argb(100,236,235,232));
            progressBar.setVisibility(View.GONE);
            rankTable.addView(tableRow,lp);
        }

    }

    private void deleteRank(String rank){
        deleteRankDR = FirebaseDatabase.getInstance().getReference();
        deleteRankDR.child("ranks").child(Validator.decodeFromFirebaseKey(rank)).removeValue();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}