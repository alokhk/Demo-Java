package com.ak86.jwala;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ak86.jwala.models.Person;
import com.ak86.jwala.models.User;
import com.ak86.jwala.util.FirebaseService;
import com.ak86.jwala.util.JwalaCallback;
import com.ak86.jwala.util.Validator;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


public class ActiveCases extends Fragment {

    private TableLayout coronaActiveTable;
    private DatabaseReference coronaPositiveDR;
    private int slNoCounter = 1;
    private FirebaseAuth mAuth;
    private TableLayout.LayoutParams lp;
    private View updatePerson, negativeDatePopup;
    private ProgressBar progressBar;
    private final FirebaseService firebaseService = new FirebaseService();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_active_cases, container, false);
        mAuth = FirebaseAuth.getInstance();
        coronaActiveTable = view.findViewById(R.id.coronaActiveTable);
        progressBar = view.findViewById(R.id.progressBarActiveCases);
        getActivePeople();
        return view;
    }

    private void getActivePeople() {
        firebaseService.getCoronaPositivePeople(new JwalaCallback<ArrayList<Person>>() {
            @Override
            public void onSuccess(ArrayList<Person> result) {
                intializeTableHeader();
                if (result.size() != 0) {
                    fillDataInTable(result);
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getActivity(), "No Corona Positive Cases!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailed(Integer errorCode) {
                if (getContext() != null) {
                    Toast.makeText(getActivity(), "Failed to get Data from Server. Try again after some time..." + errorCode, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillDataInTable(ArrayList<Person> people) {
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 5, 5, 0);
        lp.setMarginEnd(5);
        for (Person person : people) {
            slNoCounter = 1;
            if (getContext() != null) {
                if(person!=null && person.getNowPositive()){
                    TableRow tableRow = new TableRow(getActivity());

                    TextView textViewSlNo = new TextView(getActivity());
                    textViewSlNo.setText(String.valueOf(slNoCounter));
                    textViewSlNo.setTextColor(Color.argb(255, 33, 150, 243));
                    textViewSlNo.setGravity(Gravity.CENTER);
                    slNoCounter++;
                    tableRow.addView(textViewSlNo);

                    TextView textViewPersonName = new TextView(getActivity());
                    textViewPersonName.setText(Validator.decodeFromFirebaseKey(person.getRank()));
                    textViewPersonName.setTextColor(Color.argb(255, 33, 150, 243));
                    textViewPersonName.setGravity(Gravity.CENTER);
                    textViewPersonName.setHeight(50);
                    tableRow.addView(textViewPersonName);

                    TextView textViewStatus = new TextView(getActivity());
                    textViewStatus.setText(Validator.decodeFromFirebaseKey(person.getName()));
                    textViewStatus.setTextColor(Color.argb(255, 33, 150, 243));
                    textViewStatus.setGravity(Gravity.CENTER);
                    tableRow.addView(textViewStatus);

                    TextView textViewLocation = new TextView(getActivity());
                    textViewLocation.setText(Validator.decodeFromFirebaseKey(person.getCompany()));
                    textViewLocation.setTextColor(Color.argb(255, 33, 150, 243));
                    textViewLocation.setGravity(Gravity.CENTER);
                    tableRow.addView(textViewLocation);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

                    TextView dateOfResultTV = new TextView(getActivity());
                    dateOfResultTV.setText(sdf.format(person.getDateOfPositiveResult()));
                    dateOfResultTV.setTextColor(Color.argb(255, 33, 150, 243));
                    dateOfResultTV.setGravity(Gravity.CENTER);
                    dateOfResultTV.setWidth(150);
                    tableRow.addView(dateOfResultTV);

                    TextView location = new TextView(getActivity());
                    if(person.getCurrentLocation()!=null){
                        location.setText(person.getCurrentLocation());
                    } else {
                        location.setText("Please Update");
                    }
                    location.setTextColor(Color.argb(255, 33, 150, 243));
                    location.setGravity(Gravity.CENTER);
                    tableRow.addView(location);

                    TextView dateOfNextTestTV = new TextView(getActivity());
                    if(person.getDateOfNextTest()!=null){
                        dateOfNextTestTV.setText(sdf.format(person.getDateOfNextTest()));
                    }else{
                        dateOfNextTestTV.setText("Please Update");
                    }
                    dateOfNextTestTV.setTextColor(Color.argb(255, 33, 150, 243));
                    dateOfNextTestTV.setGravity(Gravity.CENTER);
                    dateOfNextTestTV.setWidth(150);
                    tableRow.addView(dateOfNextTestTV);

                    DatabaseReference userAuthDR = FirebaseDatabase.getInstance().getReference();
                    userAuthDR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot users : snapshot.getChildren()) {
                                User user = users.getValue(User.class);
                                if (mAuth.getCurrentUser().getEmail().equals(Validator.decodeFromFirebaseKey(user.getEmailId()))) {
                                    if (user.getUserLevel() > 2) {
                                        MaterialButton btnNegativePerson = new MaterialButton(getActivity());
                                        btnNegativePerson.setText("Mark Negative");
                                        btnNegativePerson.setTextSize(8);
                                        btnNegativePerson.setElevation(5);
                                        btnNegativePerson.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        btnNegativePerson.setCornerRadius(8);
                                        btnNegativePerson.setTextColor(Color.argb(255, 33, 150, 243));
                                        btnNegativePerson.setBackgroundColor(Color.parseColor("white"));
                                        btnNegativePerson.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                popUpForNegativeTestDate(person);
                                            }
                                        });
                                        tableRow.addView(btnNegativePerson);

                                        tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                                            @Override
                                            public boolean onLongClick(View v) {
                                                popUpAndUpdateCoronaDetails(person);
                                                return true;
                                            }
                                        });

                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    tableRow.setMinimumHeight(50);
                    tableRow.setElevation(4);
                    tableRow.setBackgroundColor(Color.argb(100, 236, 235, 232));
                    tableRow.setLayoutParams(lp);
                    progressBar.setVisibility(View.GONE);
                    coronaActiveTable.addView(tableRow, lp);
                }

            }
        }

    }

    private void intializeTableHeader() {
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 5, 5, 0);
        coronaActiveTable.removeAllViews();
        if (getContext() != null) {
            TableRow headerRow = new TableRow(getContext());
            TextView slno = new TextView(getActivity());
            slno.setText(" S.No ");
            slno.setTextColor(Color.parseColor("white"));
            slno.setGravity(Gravity.CENTER);
            headerRow.addView(slno);

            TextView rank = new TextView(getActivity());
            rank.setText("     Rank     ");
            rank.setTextColor(Color.parseColor("white"));
            rank.setGravity(Gravity.CENTER);
            headerRow.addView(rank);

            TextView name = new TextView(getActivity());
            name.setText("  Name  ");
            name.setTextColor(Color.parseColor("white"));
            name.setGravity(Gravity.CENTER);
            headerRow.addView(name);

            TextView company = new TextView(getActivity());
            company.setText("  Company  ");
            company.setTextColor(Color.parseColor("white"));
            company.setGravity(Gravity.CENTER);
            headerRow.addView(company);

            TextView dateOfPositiveResult = new TextView(getActivity());
            dateOfPositiveResult.setText("Tested +ve  \nOn");
            dateOfPositiveResult.setTextColor(Color.parseColor("white"));
            dateOfPositiveResult.setGravity(Gravity.CENTER);
            headerRow.addView(dateOfPositiveResult);

            TextView location = new TextView(getActivity());
            location.setText("   Location   ");
            location.setTextColor(Color.parseColor("white"));
            location.setGravity(Gravity.CENTER);
            headerRow.addView(location);

            TextView dateOfNextTest = new TextView(getActivity());
            dateOfNextTest.setText("Next Test  \nOn");
            dateOfNextTest.setTextColor(Color.parseColor("white"));
            dateOfNextTest.setGravity(Gravity.CENTER);
            headerRow.addView(dateOfNextTest);

            /*TextView dateOfNegativeResult = new TextView(getActivity());
            dateOfNegativeResult.setText("Tested\n   -ve on   ");
            dateOfNegativeResult.setTextColor(Color.parseColor("white"));
            dateOfNegativeResult.setGravity(Gravity.CENTER);
            headerRow.addView(dateOfNegativeResult);*/
            DatabaseReference userAuthDR = FirebaseDatabase.getInstance().getReference();
            userAuthDR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot users : snapshot.getChildren()) {
                        User user = users.getValue(User.class);
                        if (mAuth.getCurrentUser().getEmail().equals(Validator.decodeFromFirebaseKey(user.getEmailId()))) {
                            if (user.getUserLevel() > 2) {
                                TextView action = new TextView(getActivity());
                                action.setText("  Actions  ");
                                action.setTextColor(Color.parseColor("white"));
                                action.setGravity(Gravity.CENTER);
                                headerRow.addView(action);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            headerRow.setBackgroundColor(Color.argb(200, 33, 150, 243));
            headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            coronaActiveTable.addView(headerRow, lp);
        }
    }

    private void popUpAndUpdateCoronaDetails(Person coronaPositivePerson){
        LayoutInflater li = LayoutInflater.from(getContext());
        updatePerson = li.inflate(R.layout.corona_person_details, null);
        TextView curLoc = updatePerson.findViewById(R.id.locationOfCoronaPersonFd);
        DatePicker dtOfResultPicker = updatePerson.findViewById(R.id.dtOfPositiveTestFd);
        DatePicker dtOfNextPicker = updatePerson.findViewById(R.id.dtOfNextTestFd);
        //DatePicker dtOfNegativePicker = updatePerson.findViewById(R.id.dtOfNegativeTestFd);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(updatePerson);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentLocation = Validator.encodeForFirebaseKey(curLoc.getText().toString().trim());
                Date resultDate = readDate(dtOfResultPicker);
                Date nextDate = readDate(dtOfNextPicker);
                //Date negativeDate = readDate(dtOfNegativePicker);
                DatabaseReference personUpdateDR = FirebaseDatabase.getInstance().getReference();
                coronaPositivePerson.setCurrentLocation(currentLocation);
                coronaPositivePerson.setDateOfPositiveResult(resultDate);
                coronaPositivePerson.setDateOfNextTest(nextDate);
                //coronaPositivePerson.setDateOfNegativeResult(negativeDate);
                personUpdateDR.child("isolation").child("positive").child(coronaPositivePerson.getDbKey()).setValue(coronaPositivePerson);
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void popUpForNegativeTestDate(Person nowNegativePerson){
        LayoutInflater li = LayoutInflater.from(getContext());
        negativeDatePopup = li.inflate(R.layout.negative_test_date, null);
        DatePicker dtOfNegativePicker = negativeDatePopup.findViewById(R.id.dtOfNegativeTestFd);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(negativeDatePopup);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Date negativeDate = readDate(dtOfNegativePicker);
                DatabaseReference personUpdateDR = FirebaseDatabase.getInstance().getReference();
                nowNegativePerson.setDateOfNegativeResult(negativeDate);
                nowNegativePerson.setNowPositive(false);
                nowNegativePerson.setRecovered(true);
                personUpdateDR.child("isolation").child("positive").child(nowNegativePerson.getDbKey()).setValue(nowNegativePerson);
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void moveNegativePersonToRecovered(final DatabaseReference fromPath, final DatabaseReference toPath) {
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

    private Date readDate(DatePicker inputDate) {
        Date convertedDate = new Date();
        int day = inputDate.getDayOfMonth();
        int month = inputDate.getMonth();
        int year = inputDate.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(calendar.getTime());
        try {
            convertedDate = sdf.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

}