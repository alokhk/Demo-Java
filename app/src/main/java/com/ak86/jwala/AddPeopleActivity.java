package com.ak86.jwala;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddPeopleActivity extends AppCompatActivity {

    private final FirebaseService firebaseService = new FirebaseService();
    private Spinner rankSpinner, companySpinner;
    private ArrayList<String> ranksList;
    private ImageButton minusButton, plusButton;
    private Button addButton;
    private ArrayAdapter<String> ranksArrayAdapter, companiesArrayAdapter;
    private TextView peopleCounter, fromDateFd, toDateFd, duration;
    private EditText editTextPersonName;
    private TableLayout addPeopleTable;
    private LinearLayout addPeopleLayout;
    private int oldCount, newCount;
    private Date startDate, endDate;
    private DatabaseReference writePeopleDR;
    private List<Spinner> allRankSpinners = new ArrayList<Spinner>();
    private List<EditText> allEditTexts = new ArrayList<EditText>();
    private List<Spinner> allCoySpinners = new ArrayList<Spinner>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);
        setupActionBar();
        findAllViews();
        progressBar.setVisibility(View.GONE);
        setUpPeopleCounter();
        setupSpinners();
        setupDateListeners();
        setupAddButton();
    }

    private void findAllViews() {
        rankSpinner = findViewById(R.id.rankSpinner);
        companySpinner = findViewById(R.id.coySpinner);
        minusButton = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.minusButton);
        peopleCounter = getSupportActionBar().getCustomView().findViewById(R.id.peopleCount);
        plusButton = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.plusButton);
        addPeopleTable = findViewById(R.id.addPeopleTable);
        addPeopleLayout = findViewById(R.id.addPeopleLayout);
        fromDateFd = findViewById(R.id.fromDate);
        toDateFd = findViewById(R.id.toDate);
        addButton = findViewById(R.id.buttonAddPeople);
        editTextPersonName = findViewById(R.id.editTextPersonName);
        duration = findViewById(R.id.selectionDuration);
        progressBar = findViewById(R.id.progressBar4);

    }

    private void setupActionBar() {
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setTitle("Add People");
        getSupportActionBar().setLogo(R.drawable.ic_action_name);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupSpinners() {
        firebaseService.getAllRanks(new JwalaCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> result) {
                ranksArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, result);
                ranksArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                rankSpinner.setAdapter(ranksArrayAdapter);
            }

            @Override
            public void onFailed(Integer errorCode) {

            }
        });
        firebaseService.getAllCompanies(new JwalaCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> result) {
                companiesArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, result);
                companiesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                companySpinner.setAdapter(companiesArrayAdapter);
            }

            @Override
            public void onFailed(Integer errorCode) {

            }
        });
    }

    private void setUpPeopleCounter() {
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldCount = Integer.parseInt(peopleCounter.getText().toString());
                if (oldCount > 1) {
                    newCount = oldCount - 1;
                    peopleCounter.setText(Integer.toString(newCount));
                    addRemoveExtraFields(oldCount, newCount);
                }
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldCount = Integer.parseInt(peopleCounter.getText().toString());
                newCount = oldCount + 1;
                peopleCounter.setText(Integer.toString(newCount));
                addRemoveExtraFields(oldCount, newCount);
            }
        });
    }

    private void addRemoveExtraFields(int oldCount, int newCount) {
        if (newCount > oldCount) {
            for (int i = oldCount; i < newCount; i++) {
                TableRow tableRow = new TableRow(getApplicationContext());
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f);
                Spinner rankSpinner = new Spinner(getApplicationContext());
                allRankSpinners.add(rankSpinner);
                rankSpinner.setAdapter(ranksArrayAdapter);
                rankSpinner.setBackground(null);
                tableRow.addView(rankSpinner, params);
                EditText editText = new EditText(getApplicationContext());
                allEditTexts.add(editText);
                editText.setEms(6);
                tableRow.addView(editText, params);
                Spinner companySpinner = new Spinner(getApplicationContext());
                allCoySpinners.add(companySpinner);
                companySpinner.setAdapter(companiesArrayAdapter);
                companySpinner.setBackground(null);
                tableRow.addView(companySpinner, params);
                tableRow.setWeightSum(3);
                addPeopleTable.addView(tableRow, params);
            }
        } else if (oldCount > newCount) {
            for (int i = oldCount; i > newCount; i--) {
                addPeopleTable.removeViewAt(i);
            }
        }

    }

    private void setupDateListeners() {
        View.OnClickListener durationListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                builder.setCalendarConstraints(constraintsBuilder.build());
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
                materialDatePicker.show(getSupportFragmentManager(), materialDatePicker.toString());
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair <Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        Pair selectedDates = (Pair) materialDatePicker.getSelection();
                        final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
                        startDate = rangeDate.first;
                        endDate = rangeDate.second;
                        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yy");
                        fromDateFd.setText(simpleFormat.format(startDate));
                        toDateFd.setText(simpleFormat.format(endDate));
                    }

                });
            }
        };
        fromDateFd.setOnClickListener(durationListener);
        toDateFd.setOnClickListener(durationListener);
        duration.setOnClickListener(durationListener);
    }

    private void setupAddButton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int noOfPeople = Integer.parseInt(peopleCounter.getText().toString());
                writePeopleDR = FirebaseDatabase.getInstance().getReference();
                if (noOfPeople == 1) {
                    writeOnePersonToDB();
                }
                if (noOfPeople > 1) {
                    progressBar.setVisibility(View.VISIBLE);
                    writeOnePersonToDB();
                    writeRemainingPeopleToDB(noOfPeople);
                    progressBar.setVisibility(View.INVISIBLE);
                    finish();
                }
            }
        });
    }

    private void writeRemainingPeopleToDB(int noOfPeople) {
        for (int i = 0; i < noOfPeople - 1; i++) {
            if (allRankSpinners.get(i) != null && allRankSpinners.get(i).getSelectedItem() != null) {
                if (!TextUtils.isEmpty(allEditTexts.get(i).getText().toString())) {
                    if (allCoySpinners.get(i) != null && allCoySpinners.get(i).getSelectedItem() != null) {
                        if (startDate != null && endDate != null) {
                            Person person = new Person(allRankSpinners.get(i).getSelectedItem().toString(),
                                    allEditTexts.get(i).getText().toString(),
                                    allCoySpinners.get(i).getSelectedItem().toString(),
                                    startDate,
                                    endDate,startDate,endDate, startDate, startDate, startDate
                            );
                            String key = writePeopleDR.push().getKey();
                            person.setDbKey(key);
                            if (getIntent().getStringExtra("callingActivity").equals("quarantine")) {
                                writePeopleDR.child("quarantine").child(key).setValue(person);
                                key = null;
                            }
                            if (getIntent().getStringExtra("callingActivity").equals("isolation")) {
                                person.setIsolation(true);
                                writePeopleDR.child("isolation").child("negative").child(key).setValue(person);
                                key = null;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Dates Not Set", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Coy Info Not Loaded Yet", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter A Name!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Rank Info Not Loaded Yet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void writeOnePersonToDB() {
        if (rankSpinner != null && rankSpinner.getSelectedItem() != null) {
            if (!TextUtils.isEmpty(editTextPersonName.getText().toString())) {
                if (companySpinner != null && companySpinner.getSelectedItem() != null) {
                    if (startDate != null && endDate != null) {
                        Person person = new Person(rankSpinner.getSelectedItem().toString(),
                                editTextPersonName.getText().toString(),
                                companySpinner.getSelectedItem().toString(),
                                startDate,
                                endDate, startDate, startDate, startDate, startDate, startDate
                        );
                        String key = writePeopleDR.push().getKey();
                        person.setDbKey(key);
                        if (getIntent().getStringExtra("callingActivity").equals("quarantine")) {
                            writePeopleDR.child("quarantine").child(key).setValue(person);
                            key = null;
                            finish();
                        }
                        if (getIntent().getStringExtra("callingActivity").equals("isolation")) {
                            writePeopleDR.child("isolation").child("negative").child(key).setValue(person);
                            person.setIsolation(true);
                            key = null;
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Dates Not Set", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Coy Info Not Loaded Yet", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please Enter A Name!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Rank Info Not Loaded Yet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}