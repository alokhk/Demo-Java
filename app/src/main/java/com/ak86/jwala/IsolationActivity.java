package com.ak86.jwala;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ak86.jwala.models.Person;
import com.ak86.jwala.util.FirebaseService;
import com.ak86.jwala.util.JwalaCallback;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class IsolationActivity extends AppCompatActivity {

    private FloatingActionButton btnAddPeople;
    private final FirebaseService firebaseService = new FirebaseService();
    private TableLayout tableLayout;
    private int slNoCounter;
    private Date dateOfPositiveResult;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isolation);
        tableLayout = findViewById(R.id.isolationTable);
        progressBar = findViewById(R.id.progressBar2);
        setupActionBar();
        setupButtons();
        getAllIsolated();

    }

    private void setupActionBar() {
        getSupportActionBar().setTitle("Isolation");
        getSupportActionBar().setLogo(R.drawable.ic_action_name);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupButtons() {
        btnAddPeople = findViewById(R.id.btnAddPeopleToIsolation);
        btnAddPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addPeopleIntent = new Intent(getApplicationContext(), AddPeopleActivity.class);
                addPeopleIntent.putExtra("callingActivity", "isolation");
                startActivity(addPeopleIntent);
            }
        });
    }

    private void getAllIsolated() {
        firebaseService.getAllIsolatedPeople(new JwalaCallback<ArrayList<Person>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(ArrayList<Person> result) {
                initTableHeaders();
                if (result.size() != 0) {
                    fillDataInTable(result);
                } else {
                    Toast.makeText(getApplicationContext(), "No people in Isolation!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailed(Integer errorCode) {
                Toast.makeText(getApplicationContext(), "Failed to get Data from Server. Try again after some time..." + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initTableHeaders() {
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 5, 5, 0);
        slNoCounter = 1;
        tableLayout.removeAllViews();
        TableRow headerRow = new TableRow(getApplicationContext());
        TextView slno = new TextView(getApplicationContext());
        slno.setText("S.No");
        slno.setTextColor(Color.parseColor("white"));
        slno.setGravity(Gravity.CENTER);
        headerRow.addView(slno);

        TextView rank = new TextView(getApplicationContext());
        rank.setText("    Rank    ");
        rank.setTextColor(Color.parseColor("white"));
        rank.setGravity(Gravity.CENTER);
        headerRow.addView(rank);

        TextView name = new TextView(getApplicationContext());
        name.setText("    Name    ");
        name.setTextColor(Color.parseColor("white"));
        name.setGravity(Gravity.CENTER);
        headerRow.addView(name);

        TextView coy = new TextView(getApplicationContext());
        coy.setText("    Coy    ");
        coy.setTextColor(Color.parseColor("white"));
        coy.setGravity(Gravity.CENTER);
        headerRow.addView(coy);

        TextView startDate = new TextView(getApplicationContext());
        startDate.setText("     From     ");
        startDate.setTextColor(Color.parseColor("white"));
        startDate.setGravity(Gravity.CENTER);
        headerRow.addView(startDate);

        TextView endDate = new TextView(getApplicationContext());
        endDate.setText("     To     ");
        endDate.setTextColor(Color.parseColor("white"));
        endDate.setGravity(Gravity.CENTER);
        headerRow.addView(endDate);

        TextView duration = new TextView(getApplicationContext());
        duration.setText("  Duration  ");
        duration.setTextColor(Color.parseColor("white"));
        duration.setGravity(Gravity.CENTER);
        headerRow.addView(duration);

        TextView corona = new TextView(getApplicationContext());
        corona.setText("  Corona\n  Positive");
        corona.setTextColor(Color.parseColor("white"));
        corona.setGravity(Gravity.CENTER);
        headerRow.addView(corona);

        TextView actions = new TextView(getApplicationContext());
        actions.setText("  Actions  ");
        actions.setTextColor(Color.parseColor("white"));
        actions.setGravity(Gravity.CENTER);
        headerRow.addView(actions);

        headerRow.setBackgroundColor(Color.argb(200, 33, 150, 243));

        tableLayout.addView(headerRow, lp);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fillDataInTable(ArrayList<Person> people) {
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 5, 5, 0);
        lp.setMarginEnd(5);
        for (Person person : people) {
            TableRow tableRow = new TableRow(getApplicationContext());

            TextView textViewSlNo = new TextView(getApplicationContext());
            textViewSlNo.setText(String.valueOf(slNoCounter));
            textViewSlNo.setTextColor(Color.argb(255, 33, 150, 243));
            textViewSlNo.setGravity(Gravity.CENTER);
            slNoCounter++;
            tableRow.addView(textViewSlNo);

            TextView textViewRank = new TextView(getApplicationContext());
            textViewRank.setText(person.getRank());
            textViewRank.setTextColor(Color.argb(255, 33, 150, 243));
            textViewRank.setGravity(Gravity.CENTER);
            tableRow.addView(textViewRank);

            TextView textViewName = new TextView(getApplicationContext());
            textViewName.setMaxWidth(150);
            textViewName.setText(person.getName());
            textViewName.setTextColor(Color.argb(255, 33, 150, 243));
            textViewName.setGravity(Gravity.CENTER);
            tableRow.addView(textViewName);

            TextView textViewCoy = new TextView(getApplicationContext());
            textViewCoy.setText(person.getCompany());
            textViewCoy.setTextColor(Color.argb(255, 33, 150, 243));
            textViewCoy.setGravity(Gravity.CENTER);
            tableRow.addView(textViewCoy);

            TextView textViewFromDate = new TextView(getApplicationContext());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            textViewFromDate.setText(sdf.format(person.getIsolationStartDate()));
            textViewFromDate.setTextColor(Color.argb(255, 33, 150, 243));
            textViewFromDate.setGravity(Gravity.CENTER);
            tableRow.addView(textViewFromDate);

            TextView textViewToDate = new TextView(getApplicationContext());
            textViewToDate.setText(sdf.format(person.getIsolationEndDate()));
            textViewToDate.setTextColor(Color.argb(255, 33, 150, 243));
            textViewToDate.setGravity(Gravity.CENTER);
            tableRow.addView(textViewToDate);

            TextView textViewDuration = new TextView(getApplicationContext());
            long duration = person.getIsolationEndDate().getTime() - person.getIsolationStartDate().getTime();
            long diffInSeconds = TimeUnit.MILLISECONDS.toDays(duration);
            textViewDuration.setText((Integer.toString((int) diffInSeconds)) + " Days");
            textViewDuration.setTextColor(Color.argb(255, 33, 150, 243));
            textViewDuration.setGravity(Gravity.CENTER);
            tableRow.addView(textViewDuration);

            CheckBox checkBoxCorona = new CheckBox(getApplicationContext());
            checkBoxCorona.setOnClickListener(view -> {
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(IsolationActivity.this);
                dialog.setMessage("Are you sure? This action cannot be changed!");
                dialog.setTitle("COVID POSITIVE");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MaterialDatePicker.Builder<Long> positiveResultBuilder = MaterialDatePicker.Builder.datePicker();
                        CalendarConstraints.Builder constraintsBuilder2 = new CalendarConstraints.Builder();
                        positiveResultBuilder.setTitleText("Select Date of Positive Result");
                        positiveResultBuilder.setCalendarConstraints(constraintsBuilder2.build());
                        MaterialDatePicker<Long> materialDatePicker1 = positiveResultBuilder.build();
                        materialDatePicker1.show(getSupportFragmentManager(), materialDatePicker1.toString());
                        materialDatePicker1.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                DatabaseReference coronaPositiveReference = FirebaseDatabase.getInstance().getReference();
                                dateOfPositiveResult = new Date(materialDatePicker1.getSelection());
                                person.setDateOfPositiveResult(dateOfPositiveResult);
                                person.setNowPositive(true);
                                coronaPositiveReference.child("isolation").child("positive").child(person.getDbKey()).setValue(person);
                                coronaPositiveReference.child("isolation").child("negative").child(person.getDbKey()).removeValue();
                            }
                        });
                    }
                });
                android.app.AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            });
            tableRow.addView(checkBoxCorona);

            Button actionButton = new Button(getApplicationContext());
            if (person.getNowPositive()) {
                actionButton.setText("Mark Negative");
            } else {
                actionButton.setText("Delete");
            }
            actionButton.setTextSize(8);
            actionButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (person.getNowPositive()) {
                        markNegativeAndMoveToRecovered(person);
                    } else {
                        deletePerson(person);
                    }
                }
            });
            tableRow.addView(actionButton);

            tableRow.setLayoutParams(lp);
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
            if (person.getNowPositive()) {
                tableRow.setBackgroundColor(Color.argb(100, 255, 55, 3));
                checkBoxCorona.setEnabled(false);
            } else {
                tableRow.setBackgroundColor(Color.argb(100, 236, 235, 232));
            }

            tableLayout.addView(tableRow, lp);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void deletePerson(Person personToBeDeleted) {
        DatabaseReference deletePersonReference = FirebaseDatabase.getInstance().getReference().child("isolation");
        deletePersonReference.child(personToBeDeleted.getDbKey()).removeValue();
    }

    private void markNegativeAndMoveToRecovered(Person recoveredPerson) {
        recoveredPerson.setNowPositive(false);
        recoveredPerson.setIsolation(false);
        DatabaseReference recoveredReference = FirebaseDatabase.getInstance().getReference();
        recoveredReference.child("recovered").setValue(recoveredPerson);
        deletePerson(recoveredPerson);

    }

    private void markCoronaPositive(Person person) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(IsolationActivity.this);
        dialog.setMessage("Are you sure? This action cannot be changed!");
        dialog.setTitle("COVID POSITIVE");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MaterialDatePicker.Builder<Long> positiveResultBuilder = MaterialDatePicker.Builder.datePicker();
                CalendarConstraints.Builder constraintsBuilder2 = new CalendarConstraints.Builder();
                positiveResultBuilder.setTitleText("Select Date of Positive Result");
                positiveResultBuilder.setCalendarConstraints(constraintsBuilder2.build());
                MaterialDatePicker<Long> materialDatePicker1 = positiveResultBuilder.build();
                materialDatePicker1.show(getSupportFragmentManager(), materialDatePicker1.toString());
                materialDatePicker1.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        DatabaseReference coronaPositiveReference = FirebaseDatabase.getInstance().getReference();
                        dateOfPositiveResult = new Date(materialDatePicker1.getSelection());
                        person.setDateOfPositiveResult(dateOfPositiveResult);
                        person.setNowPositive(true);
                        coronaPositiveReference.child("isolation").child(person.getDbKey()).setValue(person);
                    }
                });
                android.app.AlertDialog alertDialog = dialog.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}