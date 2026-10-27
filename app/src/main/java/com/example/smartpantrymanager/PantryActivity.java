package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryActivity extends AppCompatActivity
        implements IngredientAdapter.OnIngredientActionListener {

    private DatabaseHelper databaseHelper;
    private IngredientAdapter ingredientAdapter;
    private TextView tvIngredientCount;
    private TextView tvEmptyPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        databaseHelper = new DatabaseHelper(this);

        tvIngredientCount = findViewById(R.id.tvIngredientCount);
        tvEmptyPantry = findViewById(R.id.tvEmptyPantry);

        RecyclerView rvIngredients = findViewById(R.id.rvIngredients);
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));

        ingredientAdapter = new IngredientAdapter(this);
        rvIngredients.setAdapter(ingredientAdapter);

        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);

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

        if (databaseHelper != null && ingredientAdapter != null) {
            loadIngredients();
        }
    }

    private void loadIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();

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

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_EXPIRY_DATE));

                ingredients.add(new Ingredient(
                        id, name, quantity, unit, expiryDate));
            }
        }

        ingredientAdapter.setIngredients(ingredients);

        int count = ingredients.size();

        tvIngredientCount.setText(
                count + (count == 1
                        ? " ingredient saved"
                        : " ingredients saved"));

        tvEmptyPantry.setVisibility(
                count == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEdit(Ingredient ingredient) {
        Intent intent = new Intent(
                PantryActivity.this,
                AddIngredientActivity.class
        );

        intent.putExtra("ingredient_id", ingredient.getId());
        intent.putExtra("ingredient_name", ingredient.getName());
        intent.putExtra("ingredient_quantity", ingredient.getQuantity());
        intent.putExtra("ingredient_unit", ingredient.getUnit());
        intent.putExtra(
                "ingredient_expiry_date",
                ingredient.getExpiryDate());

        startActivity(intent);
    }

    @Override
    public void onDelete(Ingredient ingredient) {
        boolean deleted =
                databaseHelper.deleteIngredient(ingredient.getId());

        if (deleted) {
            Toast.makeText(
                    this,
                    ingredient.getName() + " deleted",
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
