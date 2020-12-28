package com.ak86.jwala;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class QuarantineActivity extends AppCompatActivity {

    private FloatingActionButton btnAddPeople;
    private final FirebaseService firebaseService = new FirebaseService();
    private TableLayout tableLayout;
    private int slNoCounter;
    private ArrayList<CheckBox> selectedCheckboxes = new ArrayList<CheckBox>();
    private ArrayList<Person> selectedPeople = new ArrayList<Person>();
    private Date newIsolationStartDate, newIsolationEndDate, dateOfPositiveResult;
    private ProgressBar progressBar;
    private Date currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarantine);
        tableLayout = findViewById(R.id.quarantineTable);
        progressBar = findViewById(R.id.progressBar3);
        getCurrentDate();
        setupActionBar();
        setupButtons();
        updateData();
        getListOfQuarantinedPeople();

    }
    private void updateData(){
        firebaseService.updateExistingDataWithNewFields(new JwalaCallback<ArrayList<Person>>() {
            @Override
            public void onSuccess(ArrayList<Person> result) {
                DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference().child("quarantine");
                for(Person checkPerson : result){
                    checkPerson.setRecovered(true);
                    updateRef.child(checkPerson.getDbKey()).setValue(checkPerson);
                }
            }

            @Override
            public void onFailed(Integer errorCode) {

            }
        });
    }
    private void setupActionBar() {
        getSupportActionBar().setTitle("Quarantine");
        getSupportActionBar().setLogo(R.drawable.ic_action_name);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupButtons() {
        btnAddPeople = findViewById(R.id.btnAddPeople);
        btnAddPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addPeopleIntent = new Intent(getApplicationContext(), AddPeopleActivity.class);
                addPeopleIntent.putExtra("callingActivity", "quarantine");
                startActivity(addPeopleIntent);
            }
        });
    }

    private void getListOfQuarantinedPeople() {
        firebaseService.getAllQuarntiners(new JwalaCallback<ArrayList<Person>>() {
            @Override
            public void onSuccess(ArrayList<Person> result) {
                initTableHeaders();
                //result.sort(Comparator.comparing(Person::getToDate));
                fillDataInTable(result);
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

        TextView isolate = new TextView(getApplicationContext());
        isolate.setText("  Select  ");
        isolate.setTextColor(Color.parseColor("white"));
        isolate.setGravity(Gravity.CENTER);
        headerRow.addView(isolate);

        TextView actions = new TextView(getApplicationContext());
        actions.setText("  Actions  ");
        actions.setTextColor(Color.parseColor("white"));
        actions.setGravity(Gravity.CENTER);
        headerRow.addView(actions);

        headerRow.setBackgroundColor(Color.argb(200, 33, 150, 243));

        tableLayout.addView(headerRow, lp);

    }

    private void fillDataInTable(ArrayList<Person> people) {
        tableLayout = findViewById(R.id.quarantineTable);
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
            textViewFromDate.setText(sdf.format(person.getFromDate()));
            textViewFromDate.setTextColor(Color.argb(255, 33, 150, 243));
            textViewFromDate.setGravity(Gravity.CENTER);
            tableRow.addView(textViewFromDate);

            TextView textViewToDate = new TextView(getApplicationContext());
            textViewToDate.setText(sdf.format(person.getToDate()));
            textViewToDate.setTextColor(Color.argb(255, 33, 150, 243));
            textViewToDate.setGravity(Gravity.CENTER);
            tableRow.addView(textViewToDate);

            TextView textViewDuration = new TextView(getApplicationContext());
            long duration = person.getToDate().getTime() - person.getFromDate().getTime();
            long diffInSeconds = TimeUnit.MILLISECONDS.toDays(duration);
            textViewDuration.setText((Integer.toString((int) diffInSeconds)) + " Days");
            textViewDuration.setTextColor(Color.argb(255, 33, 150, 243));
            textViewDuration.setGravity(Gravity.CENTER);
            tableRow.addView(textViewDuration);

            final CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBox.isChecked()) {
                        selectedCheckboxes.add(checkBox);
                        selectedPeople.add(person);
                    } else {
                        selectedCheckboxes.remove(checkBox);
                        selectedPeople.remove(person);
                    }
                }
            });
            if (person.getIsolation()) {
                checkBox.setEnabled(false);
            }
            tableRow.addView(checkBox);

            Button actionButton = new Button(getApplicationContext());
            actionButton.setText("Delete");
            actionButton.setTextSize(8);
            actionButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.app.AlertDialog.Builder deleteDialog = new android.app.AlertDialog.Builder(QuarantineActivity.this);
                    deleteDialog.setMessage("Are you sure you want to delete " + person.getName() + "?");
                    deleteDialog.setTitle("Confirm?");
                    deleteDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deletePerson(person);
                        }
                    });
                    deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    android.app.AlertDialog alertDialog = deleteDialog.create();
                    deleteDialog.show();
                }
            });
            tableRow.addView(actionButton);

            tableRow.setLayoutParams(lp);
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
            if(person.getToDate().toString().substring(0,10).equals(currentDate.toString().substring(0,10))){
                tableRow.setBackgroundColor(Color.argb(100, 44, 172, 80));
            } else {
                tableRow.setBackgroundColor(Color.argb(100, 236, 235, 232));
            }
            tableLayout.addView(tableRow, lp);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void deletePerson(Person personToBeDeleted) {
        DatabaseReference deletePersonReference = FirebaseDatabase.getInstance().getReference().child("quarantine");
        deletePersonReference.child(personToBeDeleted.getDbKey()).removeValue();
    }

    private void moveRecord(final DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");

                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.quarantine_actions_menu, menu);
        MenuItem movToIsolation = menu.findItem(R.id.movToIsolation);
        MenuItem markPositive = menu.findItem(R.id.markPositive);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.movToIsolation:
                isolateSelectedPeople();
                return true;
            case R.id.markPositive:
                isolateSelectedPeopleAndMarkPositive();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void isolateSelectedPeople() {
        if (selectedCheckboxes.size() == 0) {
            Toast.makeText(getApplicationContext(), "No Selection Made!", Toast.LENGTH_SHORT).show();
            return;
        }
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(QuarantineActivity.this);
        dialog.setMessage("Are you sure you want to isolate " + selectedCheckboxes.size() + " people?");
        dialog.setTitle("Alert!");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            DatabaseReference isolatePersonReference = FirebaseDatabase.getInstance().getReference();

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                builder.setCalendarConstraints(constraintsBuilder.build());
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
                materialDatePicker.show(getSupportFragmentManager(), materialDatePicker.toString());
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        Pair selectedDates = (Pair) materialDatePicker.getSelection();
                        final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
                        newIsolationStartDate = rangeDate.first;
                        newIsolationEndDate = rangeDate.second;
                        for (int j = 0; j < selectedCheckboxes.size(); j++) {
                            if (selectedCheckboxes.get(j).isChecked()) {
                                selectedCheckboxes.get(j).setEnabled(false);
                                selectedPeople.get(j).setIsolation(true);
                                selectedPeople.get(j).setRecovered(false);
                                selectedPeople.get(j).setIsolationStartDate(newIsolationStartDate);
                                selectedPeople.get(j).setIsolationEndDate(newIsolationEndDate);
                                isolatePersonReference.child("quarantine").child(selectedPeople.get(j).getDbKey()).setValue(selectedPeople.get(j));
                                moveRecord(isolatePersonReference.child("quarantine").child(selectedPeople.get(j).getDbKey()),
                                        isolatePersonReference.child("isolation").child("negative").child(selectedPeople.get(j).getDbKey()));
                                deletePerson(selectedPeople.get(j));
                            } else {
                                selectedCheckboxes.get(j).setChecked(false);
                            }


                        }
                        selectedCheckboxes.clear();
                        selectedPeople.clear();
                    }
                });
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < selectedCheckboxes.size(); j++) {
                    selectedCheckboxes.get(j).setChecked(false);
                }
                selectedCheckboxes.clear();
                selectedPeople.clear();
            }
        });
        android.app.AlertDialog alertDialog = dialog.create();
        dialog.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void isolateSelectedPeopleAndMarkPositive() {
        if (selectedCheckboxes.size() == 0) {
            Toast.makeText(getApplicationContext(), "No Selection Made!", Toast.LENGTH_SHORT).show();
            return;
        }
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(QuarantineActivity.this);
        dialog.setMessage("Are you sure? This action cannot be changed!");
        dialog.setTitle("COVID POSITIVE");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            DatabaseReference coronaPositiveReference = FirebaseDatabase.getInstance().getReference();

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                builder.setTitleText("Select Duration of Isolation");
                builder.setCalendarConstraints(constraintsBuilder.build());
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
                materialDatePicker.show(getSupportFragmentManager(), materialDatePicker.toString());
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        Pair selectedDates = (Pair) materialDatePicker.getSelection();
                        final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
                        newIsolationStartDate = rangeDate.first;
                        newIsolationEndDate = rangeDate.second;
                        MaterialDatePicker.Builder<Long> positiveResultBuilder = MaterialDatePicker.Builder.datePicker();
                        CalendarConstraints.Builder constraintsBuilder2 = new CalendarConstraints.Builder();
                        positiveResultBuilder.setTitleText("Select Date of Positive Result");
                        positiveResultBuilder.setCalendarConstraints(constraintsBuilder2.build());
                        MaterialDatePicker<Long> materialDatePicker1 = positiveResultBuilder.build();
                        materialDatePicker1.show(getSupportFragmentManager(), materialDatePicker1.toString());
                        materialDatePicker1.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                dateOfPositiveResult = new Date(materialDatePicker1.getSelection());
                                for (int j = 0; j < selectedCheckboxes.size(); j++) {
                                    if (selectedCheckboxes.get(j).isChecked()) {
                                        selectedCheckboxes.get(j).setEnabled(false);
                                        selectedPeople.get(j).setIsolationStartDate(newIsolationStartDate);
                                        selectedPeople.get(j).setIsolationEndDate(newIsolationEndDate);
                                        selectedPeople.get(j).setDateOfPositiveResult(dateOfPositiveResult);
                                        selectedPeople.get(j).setIsolation(true);
                                        selectedPeople.get(j).setNowPositive(true);
                                        selectedPeople.get(j).setRecovered(false);
                                        coronaPositiveReference.child("isolation").child("positive").child(selectedPeople.get(j).getDbKey()).setValue(selectedPeople.get(j));
                                        coronaPositiveReference.child("quarantine").child(selectedPeople.get(j).getDbKey()).removeValue();
                                        Toast.makeText(getApplicationContext(), "Moved " + selectedPeople.get(j).getName() + " to isolation", Toast.LENGTH_SHORT).show();
                                    } else {
                                        selectedCheckboxes.get(j).setChecked(false);
                                    }
                                }
                                selectedCheckboxes.clear();
                                selectedPeople.clear();
                            }
                        });
                    }
                });
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < selectedCheckboxes.size(); j++) {
                    selectedCheckboxes.get(j).setChecked(false);
                }
                selectedCheckboxes.clear();
                selectedPeople.clear();
            }
        });
        android.app.AlertDialog alertDialog = dialog.create();
        dialog.show();


    }

    private void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        currentDate = calendar.getTime();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}