package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.ChatRoom;
import com.example.bodify.Models.Favourite;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Favourite> favourites;
    private final Context context;
    private String mealType;
    private String adapterChoice;
    private ArrayList<Integer> servingNumbers;

    public FavouriteAdapter(ArrayList<Favourite> favourites, Context context) {
        this.favourites = favourites;
        this.context = context;
    }

    @NonNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavouriteAdapter.ViewHolder holder, final int position) {
        holder.setItemName(favourites.get(position).getItemName());
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);
                popup.inflate(R.menu.rcv_menu_options);
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addToDiary:
                                final AlertDialog.Builder alertDialogMeals = new AlertDialog.Builder(context);
                                alertDialogMeals.setTitle("Select Meal");
                                final String[] items = {"Breakfast", "Lunch", "Dinner", "Other"};
                                Spinner servings = new Spinner(context);
                                int checkedItem = 0;
                                servingNumbers = new ArrayList<>();
                                Favourite favourite = null;
                                holder.getAdapterPosition();
                                for (int i = 0; i < favourites.size(); i++) {
                                    if (i == holder.getAdapterPosition()) {
                                        favourite = favourites.get(i);
                                    }
                                }
                                assert favourite != null;
                                for (int i = 1; i <= favourite.getNumberOfServings(); i++) {
                                    servingNumbers.add(i);
                                }
                                ArrayAdapter<Integer> servingAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, servingNumbers);
                                servings.setAdapter(servingAdapter);
                                servingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                servings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        adapterChoice = parent.getItemAtPosition(position).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                                alertDialogMeals.setView(servings);
                                alertDialogMeals.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                            case 1:
                                            case 2:
                                            case 3:
                                                mealType = items[which];
                                                break;
                                        }
                                    }
                                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Favourite favourite = null;
                                        int currentItem = holder.getAdapterPosition();
                                        for (int i = 0; i < favourites.size(); i++) {
                                            if (i == currentItem) {
                                                favourite = favourites.get(i);
                                            }
                                        }
                                            Date now = new Date();
                                            SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
                                            Log.i("Tag", simpleDateformat.format(now));
                                            Meal meal = new Meal(favourite.getItemName(), favourite.getUserID(), favourite.getCalories(),
                                                    favourite.getCaloriesFromFat(), favourite.getItemTotalFat(), favourite.getItemSodium(),
                                                    favourite.getItemTotalCarbohydrates(), favourite.getItemSugars(),
                                                    favourite.getItemProtein(), Integer.parseInt(adapterChoice), mealType);
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DayOfWeek");
                                            databaseReference.child(simpleDateformat.format(now)).push().setValue(meal).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "Successfully saved", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alert = alertDialogMeals.create();
                                alert.setCanceledOnTouchOutside(false);
                                alert.show();
                                break;
                            case R.id.DeleteFromFavourites:
                                builder.setMessage("Are you sure you want to delete this item from your favourites")
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int p = holder.getAdapterPosition();
                                        Favourite favourite = new Favourite();
                                        for (int i = 0; i < favourites.size(); i++) {
                                            if (p == i) {
                                                favourite = favourites.get(i);
                                                break;
                                            }
                                        }
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favourites");
                                        databaseReference.child(favourite.getId()).removeValue();
                                        favourites.clear();
                                        notifyDataSetChanged();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.setTitle("Attention required!");
                                alertDialog.show();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }


    @Override
    public void onClick(View v) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName;
        private final TextView buttonViewOption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameTextField);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
        }

        public void setItemName(String name) {
            itemName.setText(name);
        }
    }
}
