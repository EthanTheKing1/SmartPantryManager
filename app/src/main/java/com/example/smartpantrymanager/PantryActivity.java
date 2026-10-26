package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PantryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout ingredientList;
    private TextView tvIngredientCount;
    private TextView tvEmptyPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        databaseHelper = new DatabaseHelper(this);

        ingredientList = findViewById(R.id.ingredientList);
        tvIngredientCount = findViewById(R.id.tvIngredientCount);
        tvEmptyPantry = findViewById(R.id.tvEmptyPantry);

        Button btnAddIngredient =
                findViewById(R.id.btnAddIngredient);

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    AddIngredientActivity.class
            );
            startActivity(intent);
        });

        loadIngredients();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && ingredientList != null) {
            loadIngredients();
        }
    }

    private void loadIngredients() {

        ingredientList.removeAllViews();

        int count = 0;

        try (Cursor cursor = databaseHelper.getAllIngredients()) {

            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ID));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_NAME));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_QUANTITY));

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_UNIT));

                addIngredientRow(id, name, quantity, unit);
                count++;
            }
        }

        tvIngredientCount.setText(
                count + (count == 1
                        ? " ingredient saved"
                        : " ingredients saved"));

        tvEmptyPantry.setVisibility(
                count == 0 ? android.view.View.VISIBLE
                        : android.view.View.GONE);
    }

    private void addIngredientRow(
            int id, String name, double quantity, String unit) {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(16, 12, 8, 12);
        row.setBackgroundColor(Color.rgb(28, 56, 43));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setTextColor(Color.rgb(244, 240, 228));
        nameView.setTextSize(17);
        nameView.setTypeface(null, Typeface.BOLD);

        TextView quantityView = new TextView(this);
        quantityView.setText(
                "Quantity: " + quantity + " " + unit);
        quantityView.setTextColor(Color.rgb(185, 203, 175));
        quantityView.setTextSize(14);

        details.addView(nameView);
        details.addView(quantityView);

        LinearLayout.LayoutParams detailsParams =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1);

        row.addView(details, detailsParams);

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setAllCaps(false);
        deleteButton.setTextColor(Color.rgb(244, 240, 228));
        deleteButton.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.rgb(102, 55, 45)));

        deleteButton.setOnClickListener(v -> {
            boolean deleted = databaseHelper.deleteIngredient(id);

            if (deleted) {
                Toast.makeText(
                        this,
                        name + " deleted",
                        Toast.LENGTH_SHORT
                ).show();

                loadIngredients();
            } else {
                Toast.makeText(
                        this,
                        "Could not delete ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        row.addView(deleteButton);

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        rowParams.bottomMargin = 10;

        ingredientList.addView(row, rowParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
