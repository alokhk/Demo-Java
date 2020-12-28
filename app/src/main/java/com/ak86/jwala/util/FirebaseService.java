package com.ak86.jwala.util;

import com.ak86.jwala.models.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;

public class FirebaseService {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public void getAllRanks(JwalaCallback<ArrayList<String>> rankCallback) {
        firebaseDatabase.getReference("ranks").addListenerForSingleValueEvent(new ValueEventListener() {
            private ArrayList<String> ranks = new ArrayList<String>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot rankSnapshot : snapshot.getChildren()) {
                    ranks.add(Validator.decodeFromFirebaseKey(rankSnapshot.getValue().toString()));
                }
                rankCallback.onSuccess(ranks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                rankCallback.onFailed(error.getCode());

            }
        });

    }

    public void getAllCompanies(JwalaCallback<ArrayList<String>> companyCallback) {
        firebaseDatabase.getReference("companies").addListenerForSingleValueEvent(new ValueEventListener() {
            private ArrayList<String> companies = new ArrayList<String>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot companySnapshot : snapshot.getChildren()) {
                    companies.add(companySnapshot.getValue().toString());
                }
                companyCallback.onSuccess(companies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                companyCallback.onFailed(error.getCode());
            }
        });
    }

    public void getAllQuarntiners(JwalaCallback<ArrayList<Person>> personsCallback) {
        firebaseDatabase.getReference("quarantine").keepSynced(true);
        firebaseDatabase.getReference("quarantine").addValueEventListener(new ValueEventListener() {
            private ArrayList<Person> people = new ArrayList<Person>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                people.clear();
                for (DataSnapshot personSnapshot : snapshot.getChildren()) {
                    people.add(personSnapshot.getValue(Person.class));
                }
                personsCallback.onSuccess(people);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                personsCallback.onFailed(error.getCode());
            }
        });
    }

    public void getAllIsolatedPeople(JwalaCallback<ArrayList<Person>> personsCallback) {
        firebaseDatabase.getReference("isolation").keepSynced(true);
        firebaseDatabase.getReference("isolation").child("negative").addValueEventListener(new ValueEventListener() {
            private ArrayList<Person> people = new ArrayList<Person>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                people.clear();
                for (DataSnapshot personSnapshot : snapshot.getChildren()) {
                    people.add(personSnapshot.getValue(Person.class));
                }
                personsCallback.onSuccess(people);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                personsCallback.onFailed(error.getCode());
            }
        });
    }

    public void getCoronaPositivePeople(JwalaCallback<ArrayList<Person>> personsCallback) {
        firebaseDatabase.getReference("isolation").keepSynced(true);
        firebaseDatabase.getReference("isolation").child("positive").addValueEventListener(new ValueEventListener() {
            private ArrayList<Person> people = new ArrayList<Person>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                people.clear();
                for (DataSnapshot personSnapshot : snapshot.getChildren()) {
                    people.add(personSnapshot.getValue(Person.class));
                }
                personsCallback.onSuccess(people);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                personsCallback.onFailed(error.getCode());
            }
        });
    }

    public void updateExistingDataWithNewFields(JwalaCallback<ArrayList<Person>> personsCallback){
        firebaseDatabase.getReference("quarantine").addValueEventListener(new ValueEventListener() {
            private ArrayList<Person> people = new ArrayList<Person>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                people.clear();
                for(DataSnapshot personSnapshot : snapshot.getChildren()){
                    people.add(personSnapshot.getValue(Person.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
