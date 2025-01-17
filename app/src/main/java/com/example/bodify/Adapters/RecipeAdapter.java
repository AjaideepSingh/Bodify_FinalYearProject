package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bodify.Models.Favourite;
import com.example.bodify.Models.Habits;
import com.example.bodify.Models.Meal;
import com.example.bodify.Models.Recipe;
import com.example.bodify.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Recipe> recipes;
    private final Context context;
    private ArrayList<String> servingNumbers;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String whatDayToAddTo;
    private String quantityAdapterChoice;
    private String mealAdapterChoice;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private final Date date = new Date();
    private final ConstraintLayout constraintLayout;

    public RecipeAdapter(ArrayList<Recipe> recipes, Context context, ConstraintLayout constraintLayout) {
        this.recipes = recipes;
        this.context = context;
        this.constraintLayout = constraintLayout;
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.setRecipeName(recipes.get(position).getTitle());
        holder.setServingQuantity(String.valueOf(recipes.get(position).getServings()));
        holder.setCookDuration(recipes.get(position).getReadyInMinutes());
        holder.menuOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.menuOptions);
            popupMenu.inflate(R.menu.recipe_menu_options);
            popupMenu.setOnMenuItemClickListener(item -> {
                TextView itemNameFromScan, itemCalories, itemTotalFatT, itemSodiumT, itemTotalCarbohydratesT, itemSugarsT, itemProteinT, itemServingsT;
                ImageView imageView;
                switch (item.getItemId()) {
                    case R.id.onlineRecipe:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipes.get(position).getSourceUrl()));
                        context.startActivity(browserIntent);
                        break;
                    case R.id.information:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.meal_popup, null);
                        itemNameFromScan = view.findViewById(R.id.mealItemNameTextView);
                        itemCalories = view.findViewById(R.id.mealItemCaloriesTextView);
                        itemTotalFatT = view.findViewById(R.id.mealTotalFatTextView);
                        itemSodiumT = view.findViewById(R.id.mealTotalSodiumTextView);
                        itemTotalCarbohydratesT = view.findViewById(R.id.mealTotalCarbohydrateTextView);
                        itemSugarsT = view.findViewById(R.id.mealTotalSugarTextView);
                        itemProteinT = view.findViewById(R.id.mealTotalProteinTextView);
                        itemServingsT = view.findViewById(R.id.mealAmount);
                        imageView = view.findViewById(R.id.recipeImage);
                        itemNameFromScan.setText(recipes.get(position).getTitle());
                        itemCalories.setText(String.valueOf(recipes.get(position).getCalories()));
                        itemTotalFatT.setText(String.valueOf(recipes.get(position).getFats()));
                        itemSodiumT.setText(String.valueOf(recipes.get(position).getSodium()));
                        itemTotalCarbohydratesT.setText(String.valueOf(recipes.get(position).getCarbohydrates()));
                        itemSugarsT.setText(String.valueOf(recipes.get(position).getSugar()));
                        itemProteinT.setText(String.valueOf(recipes.get(position).getProteins()));
                        itemServingsT.setText(String.valueOf(recipes.get(position).getServings()));
                        Glide.with(context).load(recipes.get(position).getImageUrl()).into(imageView);
                        builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                        builder.setView(view);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                    case R.id.addToTodaysDiary:
                        AlertDialog.Builder diaryBuilder = new AlertDialog.Builder(context);
                        final Spinner meals, quantity, whatDay;
                        @SuppressLint("InflateParams")
                        LayoutInflater diaryInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View diaryView = diaryInflater.inflate(R.layout.addtodiarypopup, null);
                        meals = diaryView.findViewById(R.id.when);
                        quantity = diaryView.findViewById(R.id.quan);
                        whatDay = diaryView.findViewById(R.id.dayOfWeek);
                        final ArrayList<String> daysOfWeek = new ArrayList<>();
                        daysOfWeek.add("Monday");
                        daysOfWeek.add("Tuesday");
                        daysOfWeek.add("Wednesday");
                        daysOfWeek.add("Thursday");
                        daysOfWeek.add("Friday");
                        daysOfWeek.add("Saturday");
                        daysOfWeek.add("Sunday");
                        ArrayList<String> daysToShow = new ArrayList<>();
                        String dayPosition = null;
                        for (int i = 0; i < daysOfWeek.size(); i++) {
                            if (simpleDateformat.format(date).equalsIgnoreCase(daysOfWeek.get(i))) {
                                dayPosition = String.valueOf(daysOfWeek.indexOf(daysOfWeek.get(i)));
                                break;
                            }
                        }
                        for (int i = 0; i <= Integer.parseInt(Objects.requireNonNull(dayPosition)); i++) {
                            daysToShow.add(daysOfWeek.get(i));
                        }
                        ArrayAdapter dayAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, daysToShow);
                        whatDay.setAdapter(dayAdapter);
                        whatDay.setSelection(daysToShow.indexOf(simpleDateformat.format(date)));
                        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        whatDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                whatDayToAddTo = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        final ArrayList<String> mealTypes = new ArrayList<>();
                        mealTypes.add("Select Meal");
                        mealTypes.add("Breakfast");
                        mealTypes.add("Lunch");
                        mealTypes.add("Dinner");
                        mealTypes.add("Other");
                        servingNumbers = new ArrayList<>();
                        servingNumbers.add("Select Quantity");
                        for (int i = 1; i <= recipes.get(position).getServings(); i++) {
                            servingNumbers.add(String.valueOf(i));
                        }
                        ArrayAdapter servingAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, servingNumbers) {
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
                        quantity.setAdapter(servingAdapter);
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
                        ArrayAdapter mealsAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, mealTypes) {
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
                        meals.setAdapter(mealsAdapter);
                        mealsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        meals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mealAdapterChoice = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        String finalDayPosition = dayPosition;
                        diaryBuilder.setPositiveButton("Create", (d, which) -> {
                        });
                        diaryBuilder.setNegativeButton("Close", (d, which) -> d.cancel());
                        diaryBuilder.setView(diaryView);
                        AlertDialog diaryAlertDialog = diaryBuilder.create();
                        diaryAlertDialog.show();
                        diaryAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                            if (meals.getSelectedItemPosition() == 0 || quantity.getSelectedItemPosition() == 0) {
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                                dlgAlert.setMessage("Not All Fields are Filled!");
                                dlgAlert.setTitle("Error...");
                                dlgAlert.setPositiveButton("Ok", (dialog1, which1) -> dialog1.dismiss());
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();
                            } else {
                                diaryAlertDialog.dismiss();
                                Recipe recipe = recipes.get(position);
                                String positionToAddTo = null;
                                for (int i = 0; i < daysOfWeek.size(); i++) {
                                    if (daysOfWeek.get(i).equalsIgnoreCase(whatDayToAddTo)) {
                                        positionToAddTo = String.valueOf(daysOfWeek.indexOf(daysOfWeek.get(i)));
                                        break;
                                    }
                                }
                                assert positionToAddTo != null;
                                int finalDate = Integer.parseInt(finalDayPosition) - Integer.parseInt(positionToAddTo);
                                int subStringCD = Integer.parseInt(formatter.format(date).substring(0, 2));
                                int f = subStringCD - finalDate;
                                StringBuilder stringBuffer = new StringBuilder(formatter.format(date));
                                stringBuffer.replace(0, 2, String.valueOf(f));
                                String newDate;
                                if (stringBuffer.length() == 9) {
                                    String date = String.valueOf(stringBuffer);
                                    newDate = "0" + date;
                                } else {
                                    newDate = String.valueOf(stringBuffer);
                                }
                                Meal meal = new Meal(recipe.getTitle(), mAuth.getUid(), recipe.getCalories(),
                                        recipe.getFats(), recipe.getSodium(), recipe.getCarbohydrates(),
                                        recipe.getSugar(), recipe.getProteins(),
                                        Integer.parseInt(quantityAdapterChoice), mealAdapterChoice, whatDayToAddTo, newDate, recipe.getServings(), UUID.randomUUID().toString(), recipe.getSourceUrl());
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DayOfWeek");
                                databaseReference.child(whatDayToAddTo).push().setValue(meal).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Snackbar snackbar = Snackbar.make(constraintLayout, recipe.getTitle() + " added to " + mealAdapterChoice.toLowerCase() + " on " + whatDayToAddTo, Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        DatabaseReference habitReference = FirebaseDatabase.getInstance().getReference("Habits").child(Objects.requireNonNull(mAuth.getUid()));
                                        habitReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Habits habits = snapshot.getValue(Habits.class);
                                                if (habits != null) {
                                                    switch (mealAdapterChoice) {
                                                        case "Breakfast":
                                                            if (habits.getBreakfastNames().contains("No Meals")) {
                                                                for (int i = 0; i < Objects.requireNonNull(habits).getBreakfastNames().size(); i++) {
                                                                    if (habits.getBreakfastNames().get(i).equals("No Meals")) {
                                                                        habits.getBreakfastNames().remove(i);
                                                                        habits.getBreakfastNames().add(recipe.getTitle());
                                                                        break;
                                                                    }
                                                                }
                                                            } else if (habits.getBreakfastNames().stream().noneMatch(recipe.getTitle()::equalsIgnoreCase)) {
                                                                habits.getBreakfastNames().add(recipe.getTitle());
                                                            }
                                                            habitReference.child("breakfastNames").setValue(habits.getBreakfastNames());
                                                            break;
                                                        case "Lunch":
                                                            if (habits.getLunchNames().contains("No Meals")) {
                                                                for (int i = 0; i < Objects.requireNonNull(habits).getLunchNames().size(); i++) {
                                                                    if (habits.getLunchNames().get(i).equals("No Meals")) {
                                                                        habits.getLunchNames().remove(i);
                                                                        habits.getLunchNames().add(recipe.getTitle());
                                                                        break;
                                                                    }
                                                                }
                                                            } else if (habits.getLunchNames().stream().noneMatch(recipe.getTitle()::equalsIgnoreCase)) {
                                                                habits.getLunchNames().add(recipe.getTitle());
                                                            }
                                                            habitReference.child("lunchNames").setValue(habits.getLunchNames());
                                                            break;
                                                        case "Dinner":
                                                            if (habits.getDinnerNames().contains("No Meals")) {
                                                                for (int i = 0; i < Objects.requireNonNull(habits).getDinnerNames().size(); i++) {
                                                                    if (habits.getDinnerNames().get(i).equals("No Meals")) {
                                                                        habits.getDinnerNames().remove(i);
                                                                        habits.getDinnerNames().add(recipe.getTitle());
                                                                        break;
                                                                    }
                                                                }
                                                            } else if (habits.getDinnerNames().stream().noneMatch(recipe.getTitle()::equalsIgnoreCase)) {
                                                                habits.getDinnerNames().add(recipe.getTitle());
                                                            }
                                                            habitReference.child("dinnerNames").setValue(habits.getDinnerNames());
                                                            break;
                                                        case "Other":
                                                            if (habits.getOtherNames().contains("No Meals")) {
                                                                for (int i = 0; i < Objects.requireNonNull(habits).getOtherNames().size(); i++) {
                                                                    if (habits.getOtherNames().get(i).equals("No Meals")) {
                                                                        habits.getOtherNames().remove(i);
                                                                        habits.getOtherNames().add(recipe.getTitle());
                                                                        break;
                                                                    }
                                                                }
                                                            } else if (habits.getOtherNames().stream().noneMatch(recipe.getTitle()::equalsIgnoreCase)) {
                                                                habits.getOtherNames().add(recipe.getTitle());
                                                            }
                                                            habitReference.child("otherNames").setValue(habits.getOtherNames());
                                                            break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + error.getMessage(), Snackbar.LENGTH_SHORT);
                                                snackbar.show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(e -> {
                                    Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + e.getMessage(), Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                });
                            }
                        });
                        break;
                    case R.id.ingredients:
                        final AlertDialog.Builder ingredientsBuilder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
                        LayoutInflater inflater2 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view2 = inflater2.inflate(R.layout.ingredients, null);
                        RecyclerView recyclerView = view2.findViewById(R.id.ingredientsRCV);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(recipes.get(position).getIngredients());
                        ingredientsBuilder.setNegativeButton("Close", (dialog15, which) -> dialog15.cancel());
                        recyclerView.setAdapter(ingredientsAdapter);
                        ingredientsBuilder.setView(view2);
                        AlertDialog ingredientsDialog = ingredientsBuilder.create();
                        ingredientsDialog.show();
                        break;
                    case R.id.addToFavs:
                        final AlertDialog.Builder favouritesBuilder = new AlertDialog.Builder(context);
                        favouritesBuilder.setMessage("Would you like to add this Recipe to your Favourites?")
                                .setNegativeButton("No", (dialog13, which) -> dialog13.cancel()).setPositiveButton("Yes", (dialog14, which) -> {
                            Recipe recipe = recipes.get(position);
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Favourites");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean exists = false;
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        Favourite favourite = userSnapshot.getValue(Favourite.class);
                                        if (favourite != null) {
                                            if (favourite.getItemName().equalsIgnoreCase(recipe.getTitle()) && favourite.getUserID().equalsIgnoreCase(mAuth.getUid())) {
                                                exists = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (exists) {
                                        Snackbar snackbar = Snackbar.make(constraintLayout, "Item already exists in Favourites!", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    }
                                    if (!exists) {
                                        assert recipe != null;
                                        Favourite favourite = new Favourite(recipe.getTitle(), recipe.getCalories(), recipe.getFats(),
                                                recipe.getSodium(), recipe.getCarbohydrates(), recipe.getSugar(), recipe.getProteins(), mAuth.getUid(), recipe.getServings(), recipe.getSourceUrl());
                                        databaseReference.push().setValue(favourite).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Snackbar snackbar = Snackbar.make(constraintLayout, recipe.getTitle() + " added to Favourites!", Snackbar.LENGTH_SHORT);
                                                snackbar.show();
                                            } else {
                                                Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + Objects.requireNonNull(task.getException()).getMessage(), Snackbar.LENGTH_SHORT);
                                                snackbar.show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + error.getMessage(), Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            });
                        });
                        AlertDialog alertDialog = favouritesBuilder.create();
                        alertDialog.setTitle("Attention required!");
                        alertDialog.show();
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeName, servingQuantity, cookDuration, menuOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeTitle);
            servingQuantity = itemView.findViewById(R.id.Serving);
            cookDuration = itemView.findViewById(R.id.timeToCook);
            menuOptions = itemView.findViewById(R.id.recipeOptions);
        }

        public void setRecipeName(String rn) {
            recipeName.setText(rn);
        }

        public void setServingQuantity(String sq) {
            servingQuantity.setText(sq.concat(" Servings"));
        }

        public void setCookDuration(String cd) {
            cookDuration.setText(cd.concat(" Mins"));
        }
    }
}

