package com.ak86.jwala;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ak86.jwala.models.Person;
import com.ak86.jwala.util.Validator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class RecoveredCases extends Fragment {

    private TableLayout coronaNegativeTable;
    private DatabaseReference coronaNegativeDR;
    private int slNoCounter;
    private ProgressBar progressBar;
    private TableLayout.LayoutParams lp;
    private Boolean REC_EXISTS = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view  =  inflater.inflate(R.layout.fragment_active_cases, container, false);
        coronaNegativeTable = view.findViewById(R.id.coronaActiveTable);
        progressBar = view.findViewById(R.id.progressBarActiveCases);
        lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        coronaNegativeDR = FirebaseDatabase.getInstance().getReference();
        coronaNegativeDR.child("isolation").child("positive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                slNoCounter = 1;
                populateAndInitializeTable(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return view;
    }

    private void populateAndInitializeTable(DataSnapshot snapshot){
        coronaNegativeTable.removeAllViews();
        intializeTableHeader();
        for(DataSnapshot coronaPositiveSnapshot : snapshot.getChildren()){
            Person coronaPositivePerson = coronaPositiveSnapshot.getValue(Person.class);
            if(getContext()!=null){
                if(coronaPositivePerson.getRecovered()!=null & coronaPositivePerson.getRecovered()){
                    REC_EXISTS = true;
                    final TableRow tableRow = new TableRow(getActivity());

                    TextView textViewSlNo = new TextView(getActivity());
                    textViewSlNo.setText(String.valueOf(slNoCounter));
                    textViewSlNo.setGravity(Gravity.CENTER);
                    slNoCounter++;
                    tableRow.addView(textViewSlNo);

                    TextView textViewPersonName = new TextView(getActivity());
                    textViewPersonName.setText(Validator.decodeFromFirebaseKey(coronaPositivePerson.getName()));
                    textViewPersonName.setGravity(Gravity.CENTER);
                    textViewPersonName.setHeight(50);
                    tableRow.addView(textViewPersonName);

                    TextView textViewStatus = new TextView(getActivity());
                    textViewStatus.setText("-ve");
                    textViewStatus.setGravity(Gravity.CENTER);
                    tableRow.addView(textViewStatus);

                    TextView textViewLocation  = new TextView(getActivity());
                    textViewLocation.setText(coronaPositivePerson.getCurrentLocation());
                    textViewLocation.setGravity(Gravity.CENTER);
                    tableRow.addView(textViewLocation);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

                    TextView dateOfNegativeTestTv = new TextView(getActivity());
                    dateOfNegativeTestTv.setText(sdf.format(coronaPositivePerson.getDateOfNegativeResult()));
                    dateOfNegativeTestTv.setGravity(Gravity.CENTER);
                    tableRow.addView(dateOfNegativeTestTv);

                    tableRow.setMinimumHeight(50);
                    tableRow.setElevation(4);
                    tableRow.setBackgroundColor(Color.argb(100,236,235,232));
                    tableRow.setLayoutParams(lp);
                    progressBar.setVisibility(View.GONE);
                    coronaNegativeTable.addView(tableRow, lp);
                }
            }

        }
        if(!REC_EXISTS){
            if (getContext() != null) {
                Toast.makeText(getActivity(), "No Data Found!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void intializeTableHeader(){
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        if (getContext() != null){
            TableRow headerRow = new TableRow(getContext());
            TextView slno = new TextView(getActivity());
            slno.setText(" S.No ");
            slno.setTextColor(Color.parseColor("white"));
            slno.setGravity(Gravity.CENTER);
            headerRow.addView(slno);
            TextView name = new TextView(getActivity());
            name.setText("     Name     ");
            name.setTextColor(Color.parseColor("white"));
            name.setGravity(Gravity.CENTER);
            headerRow.addView(name);
            TextView status = new TextView(getActivity());
            status.setText("  Status  ");
            status.setTextColor(Color.parseColor("white"));
            status.setGravity(Gravity.CENTER);
            headerRow.addView(status);
            TextView location = new TextView(getActivity());
            location.setText("  Location  ");
            location.setTextColor(Color.parseColor("white"));
            location.setGravity(Gravity.CENTER);
            headerRow.addView(location);
            TextView dtOfNegativeResult = new TextView(getActivity());
            dtOfNegativeResult.setText("Tested\n   -ve on   ");
            dtOfNegativeResult.setTextColor(Color.parseColor("white"));
            dtOfNegativeResult.setGravity(Gravity.CENTER);
            headerRow.addView(dtOfNegativeResult);
            headerRow.setLayoutParams(lp);
            headerRow.setBackgroundColor(Color.argb(200,33,150,243));
            coronaNegativeTable.addView(headerRow,lp);
        }
    }


}