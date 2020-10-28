package com.example.bodify;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonalProfile extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{
    private FirebaseAuth mAuth;
    private TextView userName,email;
    private EditText height,weight;
    private Spinner activityLevelSpinner,fitnessGoalSpinner;
    private Button updateProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        userName = findViewById(R.id.userNameTextField);
        email = findViewById(R.id.emailAddressTextField);
        height = findViewById(R.id.heightTextFieldPP);
        weight = findViewById(R.id.weightTextFieldPP);
        updateProfile = findViewById(R.id.updateInformationButton);
        activityLevelSpinner = findViewById(R.id.activityLevelSpinner);
        fitnessGoalSpinner = findViewById(R.id.fitnessGoalSpinner);
        mAuth = FirebaseAuth.getInstance();
        final String userID = mAuth.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 User user = snapshot.getValue(User.class);
                userName.setText(user.getUserName());
                email.setText(user.getEmail());
                int intHeight = user.getHeight();
                String strHeight = String.valueOf(intHeight);
                height.setText(strHeight);
                Double dblWeight = user.getWeight();
                String strWeight = String.valueOf(dblWeight);
                weight.setText(strWeight);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PersonalProfile.this,R.array.activityLevels,android.R.layout.simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                activityLevelSpinner.setAdapter(adapter);
                activityLevelSpinner.setOnItemSelectedListener(PersonalProfile.this);
                activityLevelSpinner.setPrompt(user.getActivityLevel());

                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(PersonalProfile.this,R.array.fitnessGoals,android.R.layout.simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                fitnessGoalSpinner.setAdapter(adapter1);
                fitnessGoalSpinner.setOnItemSelectedListener(PersonalProfile.this);
                fitnessGoalSpinner.setPrompt(user.getFitnessGoal());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Database Access Error!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String strInputtedHeight = height.getText().toString().trim();
            String strInputtedWeight = weight.getText().toString().trim();
            String strFitnessGoalSpinner = fitnessGoalSpinner.getSelectedItem().toString();
            String strActivityGoalSpinner = activityLevelSpinner.getSelectedItem().toString();
            if(TextUtils.isEmpty(strInputtedHeight)){
                height.setError("Height in CMs is required!");
                height.requestFocus();
            }else if(TextUtils.isEmpty(strInputtedWeight)){
                weight.setError("Weight in KGs is required!");
                weight.requestFocus();
            }else{

                Double dblHeight = Double.parseDouble(strInputtedHeight);
                Double dblWeight = Double.parseDouble(strInputtedWeight);
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String userID = firebaseUser.getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
                databaseReference.child("weight").setValue(dblWeight);
                databaseReference.child("height").setValue(dblHeight);
                databaseReference.child("activityLevel").setValue(strActivityGoalSpinner);
                databaseReference.child("fitnessGoal").setValue(strFitnessGoalSpinner);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user != null) {
                            height.setText(String.valueOf(user.getHeight()));
                            weight.setText(String.valueOf(user.getWeight()));
                            activityLevelSpinner.setPrompt(user.getActivityLevel());
                            fitnessGoalSpinner.setPrompt(user.getFitnessGoal());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PersonalProfile.this,"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    });
}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String choice = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}