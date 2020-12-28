package com.ak86.jwala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ak86.jwala.admin.CompanyActivity;
import com.ak86.jwala.admin.RankActivity;
import com.ak86.jwala.models.Person;
import com.ak86.jwala.util.FirebaseService;
import com.ak86.jwala.util.JwalaCallback;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LandingActivity extends AppCompatActivity {

    private Button btnQuarantinerPers, btnIsolatedPers;
    private TextView totalQuarantineTV, totalIsolationTV;
    private final FirebaseService firebaseService = new FirebaseService();
    private PieChart pieChartCoronaStats;
    private BarChart downQuarantineChart;
    private DatabaseReference coronaReference, quarantineReference, isolationReference;
    private int totalCases =0, totalActive=0, totalRecovered=0, counter=0, totalQuarantine, totalIsolation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        getSupportActionBar().setLogo(R.drawable.ic_action_name);
        getSupportActionBar().setTitle(" Jwala");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        downQuarantineChart = findViewById(R.id.barChart);
        pieChartCoronaStats = findViewById(R.id.piechart);
        totalQuarantineTV = findViewById(R.id.totalQuarantine);
        totalIsolationTV = findViewById(R.id.totalIsolation);
        //x axis
        ArrayList<String> daysOfWeek = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        for (int i = 0; i < 7; i++) {
            Date date = calendar.getTime();
            String dt = sdf.format(date);
            daysOfWeek.add(dt);
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }

        ArrayList<Integer> downQuarantinePerDay = new ArrayList<Integer>();
        ArrayList datesForNextOneWeek = new ArrayList();
        calendar = Calendar.getInstance();
        for(int d = 0; d < 7; d++){
            datesForNextOneWeek.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }

        quarantineReference = FirebaseDatabase.getInstance().getReference().child("quarantine");
        Query quarantineQuery = quarantineReference.orderByChild("toDate");
        quarantineQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalQuarantine = (int) snapshot.getChildrenCount();
                totalQuarantineTV.setText(String.valueOf(totalQuarantine));
                for(Object eachDay : datesForNextOneWeek){
                    counter=0;
                    for(DataSnapshot personSnap : snapshot.getChildren()){
                        Person somePerson = personSnap.getValue(Person.class);
                        if(somePerson.getToDate().toString().substring(0,10).equals(eachDay.toString().substring(0,10))){
                            counter++;
                        }
                    }
                    downQuarantinePerDay.add(counter);
                }
                ArrayList noOfPeoplePerDay = new ArrayList();
                noOfPeoplePerDay.add(new BarEntry(0, downQuarantinePerDay.get(0)));
                noOfPeoplePerDay.add(new BarEntry(1,downQuarantinePerDay.get(1)));
                noOfPeoplePerDay.add(new BarEntry(2,downQuarantinePerDay.get(2)));
                noOfPeoplePerDay.add(new BarEntry(3,downQuarantinePerDay.get(3)));
                noOfPeoplePerDay.add(new BarEntry(4,downQuarantinePerDay.get(4)));
                noOfPeoplePerDay.add(new BarEntry(5, downQuarantinePerDay.get(5)));
                noOfPeoplePerDay.add(new BarEntry(6, downQuarantinePerDay.get(6)));

                BarDataSet barDataSet = new BarDataSet(noOfPeoplePerDay, "No of Pers");
                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add((IBarDataSet) barDataSet);
                downQuarantineChart.animateY(5000);
                downQuarantineChart.getAxisLeft().setDrawGridLines(false);
                downQuarantineChart.getAxisRight().setDrawGridLines(false);
                downQuarantineChart.getXAxis().setDrawGridLines(false);
                BarData data = new BarData(dataSets);
                XAxis xAxis = downQuarantineChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));

                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                downQuarantineChart.setData(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        coronaReference = FirebaseDatabase.getInstance().getReference();
        coronaReference.child("isolation").child("positive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalActive=0;
                totalRecovered=0;
                for(DataSnapshot personSnapshot : snapshot.getChildren()){
                    Person checkPerson = personSnapshot.getValue(Person.class);
                    if(checkPerson.getNowPositive()){
                        totalActive++;
                    } else if(checkPerson.getRecovered()){
                        totalRecovered++;
                    }
                }
                totalCases = totalActive + totalRecovered;
                ArrayList<PieEntry> variousCounts = new ArrayList();
                //variousCounts.add(new PieEntry(240, 0));
                variousCounts.add(new PieEntry(totalActive, "Active"));
                variousCounts.add(new PieEntry(totalRecovered, "Recovered"));
                PieDataSet dataSet = new PieDataSet(variousCounts, "Click Title For Details");
                dataSet.setSliceSpace(3f);
                dataSet.setIconsOffset(new MPPointF(0, 40));
                dataSet.setSelectionShift(5f);


                PieData data = new PieData(dataSet);
                dataSet.setColors(Color.RED,Color.GREEN);
                data.setValueTextSize(14f);
                data.setValueTextColor(Color.BLACK);
                data.setValueFormatter(new MyValueFormatter());
                pieChartCoronaStats.setData(data);
                pieChartCoronaStats.animateXY(5000, 5000);
                pieChartCoronaStats.setCenterText(totalCases+" Cases Total");
                pieChartCoronaStats.setDrawEntryLabels(false);
                pieChartCoronaStats.getDescription().setEnabled(false);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        isolationReference = FirebaseDatabase.getInstance().getReference().child("isolation").child("negative");
        isolationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalIsolation = (int) snapshot.getChildrenCount();
                totalIsolationTV.setText(String.valueOf(totalIsolation));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnQuarantinerPers = findViewById(R.id.btnViewQuarantinedPers);
        btnQuarantinerPers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent managerPersIntent = new Intent(getApplicationContext(), QuarantineActivity.class);
                startActivity(managerPersIntent);
            }
        });
        btnIsolatedPers = findViewById(R.id.btnViewIsolatedPers);
        btnIsolatedPers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IsolationActivity.class);
                startActivity(intent);
            }
        });
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CoronaActivity.class);
                startActivity(intent);
            }
        });
        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPreferences.getString("capacity", "");
        Log.d("CAPACITY",name);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.landing_side_menu, menu);
        MenuItem manageRanks = menu.findItem(R.id.manageRanks);
        MenuItem manageCompanies = menu.findItem(R.id.manageCompanys);
        MenuItem manageUsers = menu.findItem(R.id.manageUsers);
        MenuItem settings = menu.findItem(R.id.settings);
        MenuItem logout = menu.findItem(R.id.itemLogout);
        if (getIntent().getIntExtra("currentUserAuthLevel", 0) > 2) {
            manageRanks.setVisible(true);
            manageCompanies.setVisible(true);
            manageUsers.setVisible(true);
            settings.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manageRanks:
                Intent rankIntent = new Intent(getApplicationContext(), RankActivity.class);
                startActivity(rankIntent);
                return true;
            case R.id.manageCompanys:
                Intent companyIntent = new Intent(getApplicationContext(), CompanyActivity.class);
                startActivity(companyIntent);
                return true;
            case R.id.manageUsers:
                Intent manageUsersIntent = new Intent(getApplicationContext(), ManageUserActivity.class);
                startActivity(manageUsersIntent);
                return true;
            case R.id.settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.itemLogout:
//                mAuth.signOut();
                finish();
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public class MyValueFormatter extends ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("#");
        }

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value);
        }
    }


}