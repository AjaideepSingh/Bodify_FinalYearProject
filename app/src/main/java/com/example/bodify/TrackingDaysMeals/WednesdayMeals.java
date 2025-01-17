package com.example.bodify.TrackingDaysMeals;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Adapters.MealAdapter;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class WednesdayMeals extends Fragment {
    private MealAdapter mondayBreakfastAdapter;
    private MealAdapter mondayLunchAdapter;
    private MealAdapter mondayDinnerAdapter;
    private MealAdapter mondayOtherAdapter;
    private RecyclerView breakfastRecyclerView;
    private RecyclerView lunchRecyclerView;
    private RecyclerView dinnerRecyclerView;
    private RecyclerView otherRecyclerView;
    private final ArrayList<Meal> breakfastMeals = new ArrayList<>();
    private final ArrayList<Meal> lunchMeals = new ArrayList<>();
    private final ArrayList<Meal> dinnerMeals = new ArrayList<>();
    private final ArrayList<Meal> otherMeals = new ArrayList<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    private ConstraintLayout constraintLayout;

    public WednesdayMeals() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_showmeals, container, false);
        breakfastRecyclerView = view.findViewById(R.id.breakfastRecyclerView);
        lunchRecyclerView = view.findViewById(R.id.lunchRecyclerView);
        dinnerRecyclerView = view.findViewById(R.id.dinnerRecyclerView);
        otherRecyclerView = view.findViewById(R.id.otherRecyclerView);
        constraintLayout = view.findViewById(R.id.smcl);
        breakfastMeals.clear();
        lunchMeals.clear();
        dinnerMeals.clear();
        otherMeals.clear();
        getRCVData();
        return view;
    }

    public void getRCVData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Wednesday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    meal.setId(userSnapshot.getKey());
                    if (meal.getMealType().equals("Breakfast") && meal.getUserID().equals(userID)) {
                        breakfastMeals.add(meal);
                    } else if (meal.getMealType().equals("Lunch") && meal.getUserID().equals(userID)) {
                        lunchMeals.add(meal);
                    } else if (meal.getMealType().equals("Dinner") && meal.getUserID().equals(userID)) {
                        dinnerMeals.add(meal);
                    } else if (meal.getMealType().equals("Other") && meal.getUserID().equals(userID)) {
                        otherMeals.add(meal);
                    }
                }
                manipulateBreakfastMeals(breakfastMeals);
                manipulateLunchMeals(lunchMeals);
                manipulateDinnerMeals(dinnerMeals);
                manipulateOtherMeals(otherMeals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("error", "" + error.getMessage());
            }
        });
    }

    public void manipulateBreakfastMeals(ArrayList<Meal> breakfastMeals) {
        ArrayList<String> dbKeys = new ArrayList<>();
        for (int i = 0; i < breakfastMeals.size(); i++) {
            dbKeys.add(breakfastMeals.get(i).getId());
        }
        Set<String> uuids = new LinkedHashSet<>(dbKeys);
        dbKeys.clear();
        dbKeys.addAll(uuids);
        ArrayList<Meal> meals = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Wednesday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (int i = 0; i < dbKeys.size(); i++) {
                        if (dbKeys.get(i).equals(userSnapshot.getKey())) {
                            Meal meal = userSnapshot.getValue(Meal.class);
                            if (meal != null) {
                                meal.setId(userSnapshot.getKey());
                                meals.add(meal);
                            }
                        }
                    }
                }
                if (!meals.isEmpty()) {
                    breakfastRecyclerView.setHasFixedSize(true);
                    breakfastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    mondayBreakfastAdapter = new MealAdapter(meals, getContext(), constraintLayout);
                    breakfastRecyclerView.setAdapter(mondayBreakfastAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("error", "" + error.getMessage());
            }
        });
    }

    public void manipulateLunchMeals(ArrayList<Meal> lunchMeals) {
        ArrayList<String> dbKeys = new ArrayList<>();
        for (int i = 0; i < lunchMeals.size(); i++) {
            dbKeys.add(lunchMeals.get(i).getId());
        }
        Set<String> uuids = new LinkedHashSet<>(dbKeys);
        dbKeys.clear();
        dbKeys.addAll(uuids);
        ArrayList<Meal> meals = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Wednesday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (int i = 0; i < dbKeys.size(); i++) {
                        if (dbKeys.get(i).equals(userSnapshot.getKey())) {
                            Meal meal = userSnapshot.getValue(Meal.class);
                            if (meal != null) {
                                meal.setId(userSnapshot.getKey());
                                meals.add(meal);
                            }
                        }
                    }
                }
                if (!meals.isEmpty()) {
                    lunchRecyclerView.setHasFixedSize(true);
                    lunchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    mondayLunchAdapter = new MealAdapter(meals, getContext(), constraintLayout);
                    lunchRecyclerView.setAdapter(mondayLunchAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("error", "" + error.getMessage());
            }
        });
    }

    public void manipulateDinnerMeals(ArrayList<Meal> dinnerMeals) {
        ArrayList<String> dbKeys = new ArrayList<>();
        for (int i = 0; i < dinnerMeals.size(); i++) {
            dbKeys.add(dinnerMeals.get(i).getId());
        }
        Set<String> uuids = new LinkedHashSet<>(dbKeys);
        dbKeys.clear();
        dbKeys.addAll(uuids);
        ArrayList<Meal> meals = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Wednesday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (int i = 0; i < dbKeys.size(); i++) {
                        if (dbKeys.get(i).equals(userSnapshot.getKey())) {
                            Meal meal = userSnapshot.getValue(Meal.class);
                            if (meal != null) {
                                meal.setId(userSnapshot.getKey());
                                meals.add(meal);
                            }
                        }
                    }
                }
                if (!meals.isEmpty()) {
                    dinnerRecyclerView.setHasFixedSize(true);
                    dinnerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    mondayDinnerAdapter = new MealAdapter(meals, getContext(), constraintLayout);
                    dinnerRecyclerView.setAdapter(mondayDinnerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("error", "" + error.getMessage());
            }
        });
    }

    public void manipulateOtherMeals(ArrayList<Meal> otherMeals) {
        ArrayList<String> dbKeys = new ArrayList<>();
        for (int i = 0; i < otherMeals.size(); i++) {
            dbKeys.add(otherMeals.get(i).getId());
        }
        Set<String> uuids = new LinkedHashSet<>(dbKeys);
        dbKeys.clear();
        dbKeys.addAll(uuids);
        ArrayList<Meal> meals = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Wednesday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (int i = 0; i < dbKeys.size(); i++) {
                        if (dbKeys.get(i).equals(userSnapshot.getKey())) {
                            Meal meal = userSnapshot.getValue(Meal.class);
                            if (meal != null) {
                                meal.setId(userSnapshot.getKey());
                                meals.add(meal);
                            }
                        }
                    }
                }
                if (!meals.isEmpty()) {
                    otherRecyclerView.setHasFixedSize(true);
                    otherRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    mondayOtherAdapter = new MealAdapter(meals, getContext(), constraintLayout);
                    otherRecyclerView.setAdapter(mondayOtherAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("error", "" + error.getMessage());
            }
        });
    }
}
