package com.ak86.jwala;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.ak86.jwala.databinding.ActivityCoronaBinding;
import com.ak86.jwala.models.User;
import com.ak86.jwala.util.Validator;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class CoronaActivity extends AppCompatActivity {

        ActivityCoronaBinding binding;

        private TextView textViewCoronaPositivePersonName;
        private DatePicker resultDatePicker;
        private Date resultDate;
        private String coronaPositivePersonName;
        private FirebaseAuth mAuth;

        // tab titles
        private String[] titles = new String[]{"Active Cases", "Recovered Cases"};

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityCoronaBinding.inflate(getLayoutInflater());
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Corona Stats & Mgt");
            Objects.requireNonNull(getSupportActionBar()).setSubtitle(Html.fromHtml("<small>Long Press to Add Details   </small>"));
            setContentView(binding.getRoot());
            mAuth = FirebaseAuth.getInstance();
            init();
        }

        private void init() {
            // removing toolbar elevation
            getSupportActionBar().setElevation(0);

            binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(this));

            // attaching tab mediator
            new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                    (tab, position) -> tab.setText(titles[position])).attach();
        }

        private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

            public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
                super(fragmentActivity);
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new ActiveCases();
                    case 1:
                        return new RecoveredCases();

                }
                return new ActiveCases();
            }

            @Override
            public int getItemCount() {
                return titles.length;
            }
        }

        @Override
         public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            DatabaseReference userAuthDR = FirebaseDatabase.getInstance().getReference();
            userAuthDR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot users : snapshot.getChildren()){
                        User user = users.getValue(User.class);
                        if(mAuth.getCurrentUser().getEmail().equals(Validator.decodeFromFirebaseKey(user.getEmailId()))){
                            MenuInflater menuInflater = getMenuInflater();
                            menuInflater.inflate(R.menu.active_person,menu);
                            MenuItem addCoronaPerson = menu.findItem(R.id.addPositivePerson);
                            if(user.getUserLevel() > 2){
                                addCoronaPerson.setVisible(true);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return true;
        }

        /*private void popUpAndGetPerson(){
            //Popup layout is in person_popup, inflate it and build the alert dialog. It has two button Ok & Cancel.
            //Ok writes to the database.
            LayoutInflater li = LayoutInflater.from(com.ak86.staysafe.CoronaActivity.this);
            View coronaPersonView = li.inflate(R.layout.add_corona_person,null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(com.ak86.staysafe.CoronaActivity.this);
            alertDialogBuilder.setView(coronaPersonView);
            textViewCoronaPositivePersonName = coronaPersonView.findViewById(R.id.personNameFdCP);
            resultDatePicker = coronaPersonView.findViewById(R.id.dtResultCP);
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yy");
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //from the date selection spinner, get the day, month and year and build a calendar object.
                                    int day = resultDatePicker.getDayOfMonth();
                                    int month = resultDatePicker.getMonth();
                                    int year = resultDatePicker.getYear();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(year, month, day);
                                    //Date in the format dd/mm/yy
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    String formattedDate = sdf.format(calendar.getTime());
                                    try {
                                        //Hold it in java util data. because date operations will be out of the box
                                        resultDate = sdf.parse(formattedDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (TextUtils.isEmpty(textViewCoronaPositivePersonName.getText().toString())) {
                                        Toast.makeText(getApplicationContext(), "Please enter a name!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        coronaPositivePersonName = Validator.encodeForFirebaseKey(textViewCoronaPositivePersonName.getText().toString());
                                        CoronaPositivePerson newCoronaPerson = new CoronaPositivePerson(coronaPositivePersonName);
                                        DatabaseReference newCoronaDR = FirebaseDatabase.getInstance().getReference();
                                        String key = newCoronaDR.push().getKey();
                                        newCoronaPerson.setDbKey(key);
                                        newCoronaDR.child("Corona").child("Positive").child(key).setValue(newCoronaPerson);
                                    }
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        }*/

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.addPositivePerson :
                        //popUpAndGetPerson();
                    default:
                        return super.onOptionsItemSelected(item);
                }
        }
}