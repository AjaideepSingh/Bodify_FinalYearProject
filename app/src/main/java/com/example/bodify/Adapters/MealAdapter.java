package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Management;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Meal> meals;
    private final Context context;
    private String quantityAdapterChoice;
    private final ConstraintLayout constraintLayout;

    public MealAdapter(ArrayList<Meal> meals, Context context, ConstraintLayout constraintLayout) {
        this.meals = meals;
        this.context = context;
        this.constraintLayout = constraintLayout;
    }

    @NonNull
    @Override
    public MealAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_meal_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setServings(meals.get(position).getNumberOfServings());
        holder.setItemName(meals.get(position).getItemName());
        holder.setCaloriesConsumed(meals.get(position).getCalories() * meals.get(position).getNumberOfServings());
        holder.setFats(meals.get(position).getItemTotalFat() * meals.get(position).getNumberOfServings());
        holder.setProteins(meals.get(position).getItemProtein() * meals.get(position).getNumberOfServings());
        holder.setCarbs(meals.get(position).getItemTotalCarbohydrates() * meals.get(position).getNumberOfServings());
        holder.menuOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.menuOptions);
            popupMenu.inflate(R.menu.meal_menu_options);
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.viewMealOnline:
                        if (meals.get(position).getSourceUrl().equals("no url")) {
                            Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no recipe available!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(meals.get(position).getSourceUrl()));
                            context.startActivity(browserIntent);
                        }
                        break;
                    case R.id.deleteMeal:
                        builder.setMessage("Are you sure you want to delete this meal")
                                .setNegativeButton("No", (dialog, which) -> dialog.cancel()).setPositiveButton("Yes", (dialog, which) -> {
                            DatabaseReference deleteReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child(meals.get(position).getDayOfWeek());
                            deleteReference.child(meals.get(position).getId()).removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Snackbar snackbar = Snackbar.make(constraintLayout, "Meal removed from Diary!", Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                    Intent intent = new Intent(context, Management.class);
                                    context.startActivity(intent);
                                } else {
                                    Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + Objects.requireNonNull(task.getException()).getMessage(), Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            });
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setTitle("Attention required!");
                        alertDialog.show();
                        break;
                    case R.id.editMeal:
                        AlertDialog.Builder diaryBuilder = new AlertDialog.Builder(context);
                        final Spinner mealsSpinner, quantity, whatDay;
                        final TextView header, whatDayHeader, servingsTV;
                        @SuppressLint("InflateParams")
                        LayoutInflater diaryInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View diaryView = diaryInflater.inflate(R.layout.addtodiarypopup, null);
                        mealsSpinner = diaryView.findViewById(R.id.when);
                        quantity = diaryView.findViewById(R.id.quan);
                        header = diaryView.findViewById(R.id.addToDiaryPopUpHeader);
                        whatDayHeader = diaryView.findViewById(R.id.whatDayHeader);
                        header.setText("Edit meal below");
                        whatDay = diaryView.findViewById(R.id.dayOfWeek);
                        servingsTV = diaryView.findViewById(R.id.popUpServings);
                        servingsTV.setVisibility(View.VISIBLE);
                        whatDay.setVisibility(View.INVISIBLE);
                        whatDayHeader.setVisibility(View.INVISIBLE);
                        mealsSpinner.setVisibility(View.INVISIBLE);
                        ArrayList<Integer> servings = new ArrayList<>();
                        for (int i = 1; i <= meals.get(position).getOriginalServings(); i++) {
                            servings.add(i);
                        }
                        int servingsPosition = 0;
                        for (int i = 0; i < servings.size(); i++) {
                            if (servings.get(i).equals(meals.get(position).getNumberOfServings())) {
                                servingsPosition = i;
                            }
                        }
                        ArrayAdapter servingAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, servings);
                        quantity.setAdapter(servingAdapter);
                        quantity.setSelection(servingsPosition);
                        servingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                quantityAdapterChoice = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        diaryBuilder.setPositiveButton("Update", (dialog, which) -> {
                            int adapterPosition = holder.getAdapterPosition();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child(meals.get(holder.getAdapterPosition()).getDayOfWeek());
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Meal meal = dataSnapshot.getValue(Meal.class);
                                        assert meal != null;
                                        meal.setId(dataSnapshot.getKey());
                                        String mealID = meal.getUUID();
                                        String arrayID = null;
                                        if (!meals.isEmpty()) {
                                            arrayID = meals.get(adapterPosition).getUUID();
                                        }
                                        if (mealID.equals(arrayID)) {
                                            databaseReference.child(meal.getId()).child("numberOfServings").setValue(Integer.parseInt(quantityAdapterChoice));
                                            Snackbar snackbar = Snackbar.make(constraintLayout, "Meal successfully updated!", Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                            notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + error.getMessage(), Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            });
                        });
                        diaryBuilder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                        diaryBuilder.setView(diaryView);
                        AlertDialog diaryAlertDialog = diaryBuilder.create();
                        diaryAlertDialog.show();
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName, caloriesConsumed, fats, proteins, carbs, menuOptions, servings;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.mealName);
            caloriesConsumed = itemView.findViewById(R.id.textView16);
            fats = itemView.findViewById(R.id.fatsTV);
            proteins = itemView.findViewById(R.id.proteinsTV);
            carbs = itemView.findViewById(R.id.carbsTV);
            menuOptions = itemView.findViewById(R.id.mealMenuOptions);
            servings = itemView.findViewById(R.id.textView54);
        }

        public void setItemName(String name) {
            itemName.setText(name);
        }

        public void setCaloriesConsumed(int calories) {
            caloriesConsumed.setText(String.valueOf(calories));
        }

        public void setFats(int f) {
            fats.setText(String.valueOf(f).concat("F"));
        }

        public void setProteins(int p) {
            proteins.setText(String.valueOf(p).concat("P"));
        }

        public void setCarbs(int c) {
            carbs.setText(String.valueOf(c).concat("C"));
        }

        public void setServings(int s) {
            servings.setText(String.valueOf("Servings: " + s));
        }
    }
}

