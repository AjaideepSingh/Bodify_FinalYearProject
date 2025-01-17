package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bodify.Models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Tailoring extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText weight, height;
    private Spinner genderSpinner, fitnessGoalSpinner, activityLevelSpinner, bodyTypeSpinner, preferredFoodsSpinner;
    private ArrayList<String> genders;
    private ArrayList<String> fitnessGoals;
    private ArrayList<String> activityLevels;
    private ArrayList<String> bodyTypes;
    private ArrayList<String> preferredFoods;
    private ConstraintLayout constraintLayout;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tailoring);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Personalize your profile");
        weight = findViewById(R.id.weightTextFieldPP);
        height = findViewById(R.id.heightTextFieldPP);
        genderSpinner = findViewById(R.id.genderSpinner);
        fitnessGoalSpinner = findViewById(R.id.fitnessLevelSpinnerTailoring);
        activityLevelSpinner = findViewById(R.id.activityLevelSpinnerTailoring);
        bodyTypeSpinner = findViewById(R.id.bodyTypeSpinner);
        preferredFoodsSpinner = findViewById(R.id.foodTypeSpinner);
        constraintLayout = findViewById(R.id.tcl);
        updateSpinners();
        Button submit = findViewById(R.id.submitButton);
        submit.setOnClickListener(v -> {
            if (weight.getText().toString().length() < 2) {
                weight.setError("Invalid weight!");
                weight.requestFocus();
            } else if (weight.getText().toString().indexOf("0") == 0) {
                weight.setError("Invalid weight!");
                weight.requestFocus();
            } else if (Double.parseDouble(weight.getText().toString()) <= 1.00) {
                weight.setError("Invalid weight!");
                weight.requestFocus();
            } else if (weight.getText().toString().indexOf(".") == 1) {
                weight.setError("Invalid weight!");
                weight.requestFocus();
            } else if (weight.getText().toString().contains(".") && weight.getText().toString().indexOf(".") == -1) {
                weight.setError("Invalid weight!");
                weight.requestFocus();
            } else if (weight.getText().toString().contains(".") && weight.getText().toString().indexOf(".") == -2) {
                weight.setError("Invalid weight!");
                weight.requestFocus();
            } else if (weight.getText().toString().indexOf(".") == 2 && weight.getText().toString().length() == 6) {
                weight.setError("Invalid weight!");
                weight.requestFocus();
            } else if (Double.parseDouble(weight.getText().toString()) > 442.00) {
                weight.setError("Error max weight is 442KG!");
            } else if (height.getText().toString().length() < 2) {
                height.setError("Invalid height!");
                height.requestFocus();
            } else if (height.getText().toString().indexOf("0") == 0) {
                height.setError("Invalid height!");
                height.requestFocus();
            } else if (Integer.parseInt(height.getText().toString()) > 232) {
                height.setError("Error max height is 232CM!");
                height.requestFocus();
            } else if ((fitnessGoalSpinner.getSelectedItemPosition() == 0) ||
                    (activityLevelSpinner.getSelectedItemPosition() == 0) || (bodyTypeSpinner.getSelectedItemPosition() == 0) ||
                    (genderSpinner.getSelectedItemPosition() == 0) || (preferredFoodsSpinner.getSelectedItemPosition() == 0)) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(Tailoring.this);
                dlgAlert.setMessage("Select all drop down values!");
                dlgAlert.setTitle("Error...");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            } else {
                Intent intent = getIntent();
                double bodyMassIndex;
                double heightInMetres = Integer.parseInt(height.getText().toString()) / 100.00;
                bodyMassIndex = Double.parseDouble(weight.getText().toString()) / Math.pow(heightInMetres, 2.0);
                DecimalFormat decimalFormat = new DecimalFormat("##.00");
                double formattedBodyMassIndex = Double.parseDouble(decimalFormat.format(bodyMassIndex));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat todaysDate = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                User user = new User(intent.getStringExtra("userName"), Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), genderSpinner.getSelectedItem().toString(),
                        activityLevelSpinner.getSelectedItem().toString(), fitnessGoalSpinner.getSelectedItem().toString(),
                        bodyTypeSpinner.getSelectedItem().toString(), preferredFoodsSpinner.getSelectedItem().toString(),
                        Double.parseDouble(weight.getText().toString()), formattedBodyMassIndex, Integer.parseInt(height.getText().toString()),
                        intent.getStringExtra("imageUrl"), todaysDate.format(date));
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
                databaseReference.child(Objects.requireNonNull(mAuth.getUid())).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel notificationChannel = new NotificationChannel("My Notification", "test", NotificationManager.IMPORTANCE_DEFAULT);
                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(notificationChannel);
                        }
                        String message = "Thank you for creating an account with us!";
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(Tailoring.this, "My Notification").setSmallIcon(
                                R.drawable.info).setContentTitle("Welcome").setContentText(message).setAutoCancel(true);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(Tailoring.this);
                        notificationManagerCompat.notify(0, builder.build());
                        Toast.makeText(getApplicationContext(), "User Created Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Management.class));
                    } else {
                        Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + Objects.requireNonNull(task.getException()).getMessage(), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
            }
        });
    }

    public void updateSpinners() {
        genders = new ArrayList<>();
        genders.add("Select Gender");
        genders.add("Male");
        genders.add("Female");

        fitnessGoals = new ArrayList<>();
        fitnessGoals.add("Select Fitness Goal");
        fitnessGoals.add("Lose weight");
        fitnessGoals.add("Maintain weight");
        fitnessGoals.add("Gain weight");

        activityLevels = new ArrayList<>();
        activityLevels.add("Select Activity Level");
        activityLevels.add("1");
        activityLevels.add("2");
        activityLevels.add("3");

        bodyTypes = new ArrayList<>();
        bodyTypes.add("Select Body Composition");
        bodyTypes.add("Excess body fat");
        bodyTypes.add("Average Shape");
        bodyTypes.add("Good Shape");

        preferredFoods = new ArrayList<>();
        preferredFoods.add("Preferred Macro-Nutrient");
        preferredFoods.add("Fats");
        preferredFoods.add("Carbohydrates");
        preferredFoods.add("Don't have a preference");

        ArrayAdapter<String> adapterGender = new ArrayAdapter<String>(Tailoring.this, android.R.layout.simple_spinner_dropdown_item, genders) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) {
                    textview.setTextColor(Color.GRAY);
                } else {
                    textview.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapterGender);
        genderSpinner.setOnItemSelectedListener(Tailoring.this);
        ArrayAdapter<String> adapterFitnessGoal = new ArrayAdapter<String>(Tailoring.this, android.R.layout.simple_spinner_dropdown_item, fitnessGoals) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) {
                    textview.setTextColor(Color.GRAY);
                } else {
                    textview.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapterFitnessGoal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fitnessGoalSpinner.setAdapter(adapterFitnessGoal);
        fitnessGoalSpinner.setOnItemSelectedListener(Tailoring.this);
        ArrayAdapter<String> adapterActivityLevels = new ArrayAdapter<String>(Tailoring.this, android.R.layout.simple_spinner_dropdown_item, activityLevels) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) {
                    textview.setTextColor(Color.GRAY);
                } else {
                    textview.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapterActivityLevels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(adapterActivityLevels);
        activityLevelSpinner.setOnItemSelectedListener(Tailoring.this);
        ArrayAdapter<String> adapterBodyType = new ArrayAdapter<String>(Tailoring.this, android.R.layout.simple_spinner_dropdown_item, bodyTypes) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) {
                    textview.setTextColor(Color.GRAY);
                } else {
                    textview.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapterBodyType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodyTypeSpinner.setAdapter(adapterBodyType);
        bodyTypeSpinner.setOnItemSelectedListener(Tailoring.this);
        ArrayAdapter<String> adapterPreferredFoods = new ArrayAdapter<String>(Tailoring.this, android.R.layout.simple_spinner_dropdown_item, preferredFoods) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) {
                    textview.setTextColor(Color.GRAY);
                } else {
                    textview.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapterPreferredFoods.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        preferredFoodsSpinner.setAdapter(adapterPreferredFoods);
        preferredFoodsSpinner.setOnItemSelectedListener(Tailoring.this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
