package com.example.smartpantrymanager;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class RecipeDetailActivity extends Activity {

    private DatabaseHelper database;
    private LinearLayout ingredientContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        database = new DatabaseHelper(this);
        ingredientContainer = findViewById(R.id.detailIngredients);

        String name = getIntent().getStringExtra("recipe_name");
        String steps = getIntent().getStringExtra("recipe_steps");
        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        TextView title = findViewById(R.id.txtRecipeTitle);
        TextView preparation = findViewById(R.id.txtPreparationSteps);

        title.setText(name == null ? "Recipe" : name);
        preparation.setText(steps == null ? "" : steps);

        findViewById(R.id.btnBackToRecipes).setOnClickListener(v -> finish());

        loadIngredients(recipeId);
    }

    private void loadIngredients(int recipeId) {
        if (recipeId < 0) {
            return;
        }

        try (Cursor cursor = database.getRecipeIngredients(recipeId)) {
            int nameIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_NAME);
            int quantityIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_REQUIRED_QUANTITY);
            int unitIndex = cursor.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_REQUIRED_UNIT);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                double quantity = cursor.getDouble(quantityIndex);
                String unit = cursor.getString(unitIndex);

                TextView item = new TextView(this);
                item.setText("• " + name + " — "
                        + formatQuantity(quantity) + " " + unit);
                item.setTextColor(0xFFF4F0E4);
                item.setTextSize(16);
                item.setPadding(0, 8, 0, 8);
                ingredientContainer.addView(item);
            }
        }
    }


    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }

        return String.format(Locale.getDefault(), "%.2f", quantity);
    }

}
