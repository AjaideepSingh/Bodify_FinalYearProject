package com.example.bodify;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.bodify.SignUp.MESSAGE_KEY;
import static com.example.bodify.SignUp.MESSAGE_KEY1;

public class Tailoring extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{

    private EditText weight, height;
//    private RadioGroup gender, fitnessGoal, activityLevel;
    private Spinner genderSpinner,fitnessGoalSpinner,activityLevelSpinner,bodyTypeSpinner,preferredFoodsSpinner;
    private Button submit;
    private ArrayList<String> genders;
    private ArrayList<String> fitnessGoals;
    private ArrayList<String> activityLevels;
    private ArrayList<String> bodyTypes;
    private ArrayList<String> preferredFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tailoring);
        getSupportActionBar().setTitle("Personalize your profile");

        weight = findViewById(R.id.weightTextFieldPP);
        height = findViewById(R.id.heightTextFieldPP);
        genderSpinner = findViewById(R.id.genderSpinner);
        fitnessGoalSpinner = findViewById(R.id.fitnessLevelSpinnerTailoring);
        activityLevelSpinner = findViewById(R.id.activityLevelSpinnerTailoring);
        bodyTypeSpinner = findViewById(R.id.bodyTypeSpinner);
        preferredFoodsSpinner = findViewById(R.id.foodTypeSpinner);
        updateSpinners();
        submit = findViewById(R.id.submitButton);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strWeight = weight.getText().toString();
                String strHeight = height.getText().toString();







                Double dblWeight = Double.parseDouble(strWeight);
                int intHeight = Integer.parseInt(strHeight);


                Intent intent = getIntent();
                String strUserName = intent.getStringExtra(MESSAGE_KEY);
                String imageUrl = intent.getStringExtra(MESSAGE_KEY1);
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String strEmail = firebaseUser.getEmail();
                String userID = firebaseUser.getUid();

                Double bodyMassIndex;
                Double heightInMetres = intHeight/100.00;
                bodyMassIndex = dblWeight /Math.pow(heightInMetres,2.0);
                DecimalFormat decimalFormat = new DecimalFormat("##.00");
                Double formattedBodyMassIndex = Double.parseDouble(decimalFormat.format(bodyMassIndex));
                final User user = new User(strUserName,strEmail,"Gender","activityLevel","fitnessGoal",dblWeight,formattedBodyMassIndex,intHeight,imageUrl,"body type","foods");
                databaseReference.child("User").child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User Saved Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),DashBoard.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Error Occurred!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
}

    public void updateSpinners() {
        genders = new ArrayList<>();
        genders.add("Male");
        genders.add("Female");

        fitnessGoals = new ArrayList<>();
        fitnessGoals.add("Lose Weight");
        fitnessGoals.add("Maintain Weight");
        fitnessGoals.add("Gain Weight");

        activityLevels = new ArrayList<>();
        activityLevels.add("1");
        activityLevels.add("2");
        activityLevels.add("3");

        bodyTypes = new ArrayList<>();
        bodyTypes.add("Excess Body Fat");
        bodyTypes.add("Average Shape");
        bodyTypes.add("Good Shape");

        preferredFoods = new ArrayList<>();
        preferredFoods.add("Prefer carbohydrates");
        preferredFoods.add("Don't have a preference");
        preferredFoods.add("Enjoy fatty foods");

        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(Tailoring.this,android.R.layout.simple_spinner_dropdown_item,genders);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapterGender);
        genderSpinner.setOnItemSelectedListener(Tailoring.this);
        genderSpinner.setPrompt("Select Gender");

        ArrayAdapter<String> adapterFitnessGoal = new ArrayAdapter<>(Tailoring.this,android.R.layout.simple_spinner_dropdown_item,fitnessGoals);
        adapterFitnessGoal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fitnessGoalSpinner.setAdapter(adapterFitnessGoal);
        fitnessGoalSpinner.setOnItemSelectedListener(Tailoring.this);

        ArrayAdapter<String> adapterActivityLevels = new ArrayAdapter<>(Tailoring.this,android.R.layout.simple_spinner_dropdown_item,activityLevels);
        adapterActivityLevels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(adapterActivityLevels);
        activityLevelSpinner.setOnItemSelectedListener(Tailoring.this);

        ArrayAdapter<String> adapterBodyType = new ArrayAdapter<>(Tailoring.this,android.R.layout.simple_spinner_dropdown_item,bodyTypes);
        adapterBodyType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodyTypeSpinner.setAdapter(adapterBodyType);
        bodyTypeSpinner.setOnItemSelectedListener(Tailoring.this);

        ArrayAdapter<String> adapterPreferredFoods = new ArrayAdapter<>(Tailoring.this,android.R.layout.simple_spinner_dropdown_item,preferredFoods);
        adapterPreferredFoods.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        preferredFoodsSpinner.setAdapter(adapterPreferredFoods);
        preferredFoodsSpinner.setOnItemSelectedListener(Tailoring.this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String choice = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
