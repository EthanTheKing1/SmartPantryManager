package com.example.smartpantrymanager;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.drawable.GradientDrawable;

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
        ImageView recipeImage = findViewById(R.id.imgRecipeDetail);

        int imageResource = findRecipeImage(name);

        if (imageResource != 0) {
            recipeImage.setImageResource(imageResource);
        } else {
            recipeImage.setImageDrawable(null);
        }

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

    private int findRecipeImage(String recipeName) {
        if (recipeName == null) {
            return 0;
        }

        String name = recipeName.trim().toLowerCase(Locale.ROOT);
        String drawableName;

        switch (name) {
            case "cheese omelette":
                drawableName = "recipe_cheese_omelette";
                break;
            case "tomato pasta":
                drawableName = "recipe_tomato_pasta";
                break;
            case "banana pancakes":
                drawableName = "recipe_banana_pancakes";
                break;
            case "garlic butter pasta":
                drawableName = "recipe_garlic_butter_pasta";
                break;
            case "cheese sandwich":
                drawableName = "recipe_cheese_sandwich";
                break;
            case "french toast":
                drawableName = "recipe_french_toast";
                break;
            case "tomato and cheese toast":
                drawableName = "recipe_tomato_cheese_toast";
                break;
            case "banana smoothie":
                drawableName = "recipe_banana_smoothie";
                break;
            case "fluffy scrambled eggs":
                drawableName = "recipe_scrambled_eggs";
                break;
            case "simple flour flatbread":
                drawableName = "recipe_flour_flatbread";
                break;
            case "garlic butter bread":
                drawableName = "recipe_garlic_butter_bread";
                break;
            case "tomato omelette":
                drawableName = "recipe_tomato_omelette";
                break;
            case "cheesy pasta":
                drawableName = "recipe_cheesy_pasta";
                break;
            case "banana french toast":
                drawableName = "recipe_banana_french_toast";
                break;
            case "tomato pasta with cheese":
                drawableName = "recipe_tomato_pasta_cheese";
                break;
            case "creamy garlic pasta":
                drawableName = "recipe_creamy_garlic_pasta";
                break;
            case "egg and cheese sandwich":
                drawableName = "recipe_egg_cheese_sandwich";
                break;
            case "banana milk bowl":
                drawableName = "recipe_banana_milk_bowl";
                break;
            case "tomato garlic pasta":
                drawableName = "recipe_tomato_garlic_pasta";
                break;
            case "cheese and garlic flatbread":
                drawableName = "recipe_cheese_garlic_flatbread";
                break;
            default:
                return 0;
        }

        return getResources().getIdentifier(
                drawableName,
                "drawable",
                getPackageName()
        );
    }

}
