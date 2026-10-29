package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SuggestedRecipesActivity extends Activity {

    private DatabaseHelper database;
    private LinearLayout recipeContainer;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        database = new DatabaseHelper(this);
        recipeContainer = findViewById(R.id.recipeContainer);
        emptyMessage = findViewById(R.id.txtNoRecipes);

        findViewById(R.id.btnBackToHome).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        recipeContainer.removeAllViews();
        int recipeCount = 0;
        int pantryCount = 0;

        try (Cursor c = database.getAllRecipes()) {
            recipeCount = c.getCount();
        }

        try (Cursor c = database.getAllIngredients()) {
            pantryCount = c.getCount();
        }

        Toast.makeText(
                this,
                "Recipes in database: " + recipeCount
                        + " | Pantry items: " + pantryCount,
                Toast.LENGTH_LONG
        ).show();
        int matches = 0;

        try (Cursor recipes = database.getAllRecipes()) {
            int idIndex = recipes.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_RECIPE_ID);
            int nameIndex = recipes.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_RECIPE_NAME);
            int stepsIndex = recipes.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_RECIPE_STEPS);

            while (recipes.moveToNext()) {
                int recipeId = recipes.getInt(idIndex);
                String name = recipes.getString(nameIndex);
                String steps = recipes.getString(stepsIndex);

                if (canMakeRecipe(recipeId)) {
                    addRecipeCard(recipeId, name, steps);
                    matches++;
                }
            }
        }

        emptyMessage.setVisibility(
                matches == 0 ? TextView.VISIBLE : TextView.GONE);
    }

    private boolean canMakeRecipe(int recipeId) {
        try (Cursor required = database.getRecipeIngredients(recipeId)) {
            int nameIndex = required.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_NAME);
            int quantityIndex = required.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_REQUIRED_QUANTITY);
            int unitIndex = required.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_REQUIRED_UNIT);

            if (!required.moveToFirst()) {
                return false;
            }

            do {
                String requiredName = required.getString(nameIndex);
                double requiredQuantity = required.getDouble(quantityIndex);
                String requiredUnit = required.getString(unitIndex);

                if (!hasEnough(requiredName, requiredQuantity, requiredUnit)) {
                    return false;
                }
            } while (required.moveToNext());
        }

        return true;
    }


    private boolean hasEnough(
            String requiredName, double requiredQuantity, String requiredUnit) {

        double available = 0;
        boolean found = false;

        try (Cursor pantry = database.getAllIngredients()) {
            int nameIndex = pantry.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_NAME);
            int quantityIndex = pantry.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_QUANTITY);
            int unitIndex = pantry.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_UNIT);

            while (pantry.moveToNext()) {
                String pantryName = pantry.getString(nameIndex);
                String pantryUnit = pantry.getString(unitIndex);
                double quantity = pantry.getDouble(quantityIndex);

                if (!normalizeName(pantryName).equals(
                        normalizeName(requiredName))) {
                    continue;
                }

                double converted = convertQuantity(
                        quantity, pantryUnit, requiredUnit);

                if (converted >= 0) {
                    available += converted;
                    found = true;
                }
            }
        }


        return found && available + 0.000001 >= requiredQuantity;
    }


    private String normalizeName(String name) {
        String value = name.trim().toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        if (value.endsWith("ies") && value.length() > 3) {
            return value.substring(0, value.length() - 3) + "y";
        }

        if (value.endsWith("oes") && value.length() > 3) {
            return value.substring(0, value.length() - 2);
        }

        if (value.endsWith("s") && !value.endsWith("ss")
                && value.length() > 2) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }

    // Returns -1 when the units cannot safely be converted.
    private double convertQuantity(
            double quantity, String fromUnit, String toUnit) {

        String from = normalizeUnit(fromUnit);
        String to = normalizeUnit(toUnit);

        if (from.equals(to)) {
            return quantity;
        }

        // Mass: kilograms and grams.
        if (from.equals("grams") && to.equals("kilograms")) {
            return quantity / 1000.0;
        }
        if (from.equals("kilograms") && to.equals("grams")) {
            return quantity * 1000.0;
        }

        // Volume: litres, millilitres, cups and spoons.
        double fromMl = toMillilitres(quantity, from);
        if (fromMl < 0) {
            return -1;
        }

        double toFactor = millilitresPerUnit(to);
        if (toFactor < 0) {
            return -1;
        }

        return fromMl / toFactor;
    }


    private String normalizeUnit(String unit) {
        String value = unit.trim().toLowerCase(Locale.ROOT);

        // Handle labels such as "grams (g)" and "millilitres (ml)".
        value = value.replaceAll("\\s*\\([^)]*\\)", "").trim();

        switch (value) {
            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return "pieces";

            case "g":
            case "gram":
            case "grams":
                return "grams";

            case "kg":
            case "kilogram":
            case "kilograms":
                return "kilograms";

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "millilitres";

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "litres";

            case "cup":
            case "cups":
                return "cups";

            case "tablespoon":
            case "tablespoons":
            case "tbsp":
                return "tablespoons";

            case "teaspoon":
            case "teaspoons":
            case "tsp":
                return "teaspoons";

            default:
                return value;
        }
    }


    private double toMillilitres(double quantity, String unit) {
        double factor = millilitresPerUnit(unit);
        return factor < 0 ? -1 : quantity * factor;
    }

    private double millilitresPerUnit(String unit) {
        switch (unit) {
            case "millilitres": return 1;
            case "litres": return 1000;
            case "cups": return 240;
            case "tablespoons": return 15;
            case "teaspoons": return 5;
            default: return -1;
        }
    }


    private void addRecipeCard(int id, String name, String steps) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(20), dp(18), dp(20), dp(18));

        GradientDrawable cardBackground = new GradientDrawable();
        cardBackground.setColor(Color.rgb(28, 56, 43));
        cardBackground.setCornerRadius(dp(20));
        cardBackground.setStroke(dp(1), Color.rgb(53, 83, 66));
        card.setBackground(cardBackground);
        card.setClipToOutline(true);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, dp(16));
        card.setLayoutParams(cardParams);

        TextView title = new TextView(this);
        title.setText(name);
        title.setTextColor(Color.rgb(244, 240, 228));
        title.setTextSize(19);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView description = new TextView(this);
        description.setText("✓  All required ingredients available");
        description.setTextColor(Color.rgb(185, 203, 175));
        description.setTextSize(13);
        description.setPadding(0, dp(8), 0, dp(16));

        Button button = new Button(this);
        button.setText("View Recipe  →");
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setTextColor(Color.rgb(16, 37, 29));
        button.setMinHeight(dp(48));
        button.setMinimumHeight(dp(48));
        button.setPadding(dp(16), 0, dp(16), 0);
        button.setBackgroundTintList(null);

        GradientDrawable buttonBackground = new GradientDrawable();
        buttonBackground.setColor(Color.rgb(212, 184, 121));
        buttonBackground.setCornerRadius(dp(14));
        button.setBackground(buttonBackground);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    RecipeDetailActivity.class);
            intent.putExtra("recipe_id", id);
            intent.putExtra("recipe_name", name);
            intent.putExtra("recipe_steps", steps);
            startActivity(intent);
        });

        card.addView(title);
        card.addView(description);
        card.addView(button);
        recipeContainer.addView(card);
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

}
