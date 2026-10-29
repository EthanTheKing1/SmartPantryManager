package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.Gravity;

import java.util.Locale;

public class SuggestedRecipesActivity extends Activity {

    private DatabaseHelper database;
    private LinearLayout recipeContainer;
    private TextView emptyMessage;
    private LinearLayout suggestedSection;
    private LinearLayout almostThereSection;
    private LinearLayout almostThereContainer;
    private TextView emptyAlmostThereMessage;
    private Button suggestedTab;
    private Button almostThereTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        database = new DatabaseHelper(this);
        recipeContainer = findViewById(R.id.recipeContainer);
        emptyMessage = findViewById(R.id.txtNoRecipes);
        suggestedSection = findViewById(R.id.suggestedSection);
        almostThereSection = findViewById(R.id.almostThereSection);
        almostThereContainer = findViewById(R.id.almostThereContainer);
        emptyAlmostThereMessage = findViewById(R.id.txtNoAlmostThere);

        suggestedTab = findViewById(R.id.btnSuggestedTab);
        almostThereTab = findViewById(R.id.btnAlmostThereTab);

        suggestedTab.setOnClickListener(v -> showTab(true));
        almostThereTab.setOnClickListener(v -> showTab(false));

        showTab(true);

        findViewById(R.id.btnBackToHome).setOnClickListener(v -> finish());

    }
    private void showTab(boolean showSuggested) {
        suggestedSection.setVisibility(
                showSuggested ? ViewGroup.VISIBLE : ViewGroup.GONE);

        almostThereSection.setVisibility(
                showSuggested ? ViewGroup.GONE : ViewGroup.VISIBLE);

        suggestedTab.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(
                                showSuggested ? 212 : 28,
                                showSuggested ? 184 : 56,
                                showSuggested ? 121 : 43)));

        suggestedTab.setTextColor(
                Color.rgb(
                        showSuggested ? 16 : 244,
                        showSuggested ? 37 : 240,
                        showSuggested ? 29 : 228));

        almostThereTab.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(
                                showSuggested ? 28 : 212,
                                showSuggested ? 56 : 184,
                                showSuggested ? 43 : 121)));

        almostThereTab.setTextColor(
                Color.rgb(
                        showSuggested ? 244 : 16,
                        showSuggested ? 240 : 37,
                        showSuggested ? 228 : 29));
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
        loadAlmostThereRecipes();
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

    private void loadAlmostThereRecipes() {
        almostThereContainer.removeAllViews();

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

                String missingIngredient = getMissingIngredient(recipeId);

                if (missingIngredient != null) {
                    addAlmostThereCard(
                            recipeId, name, steps, missingIngredient);
                    matches++;
                }
            }
        }

        emptyAlmostThereMessage.setVisibility(
                matches == 0 ? ViewGroup.VISIBLE : ViewGroup.GONE);
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

    private String getMissingIngredient(int recipeId) {
        int missingCount = 0;
        String missingIngredient = "";

        try (Cursor required = database.getRecipeIngredients(recipeId)) {
            int nameIndex = required.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_NAME);
            int quantityIndex = required.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_REQUIRED_QUANTITY);
            int unitIndex = required.getColumnIndexOrThrow(
                    DatabaseHelper.COLUMN_REQUIRED_UNIT);

            if (!required.moveToFirst()) {
                return null;
            }

            do {
                String requiredName = required.getString(nameIndex);
                double requiredQuantity = required.getDouble(quantityIndex);
                String requiredUnit = required.getString(unitIndex);

                if (!hasEnough(
                        requiredName, requiredQuantity, requiredUnit)) {

                    missingCount++;

                    if (missingCount > 1) {
                        return null;
                    }

                    missingIngredient = requiredName + " — need "
                            + formatQuantity(requiredQuantity)
                            + " " + requiredUnit;
                }

            } while (required.moveToNext());
        }

        return missingCount == 1 ? missingIngredient : null;
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }

        return String.valueOf(quantity);
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


    private void addAlmostThereCard(
            int id, String name, String steps, String missingIngredient) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(16));

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
        cardParams.setMargins(0, 0, 0, dp(18));
        card.setLayoutParams(cardParams);

        // Recipe image
        FrameLayout imageFrame = new FrameLayout(this);
        imageFrame.setBackground(
                makeRoundedBackground(Color.rgb(35, 70, 52), 16));

        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(170));
        imageParams.bottomMargin = dp(14);
        imageFrame.setLayoutParams(imageParams);

        int imageResource = findRecipeImage(name);

        if (imageResource != 0) {
            ImageView image = new ImageView(this);
            image.setImageResource(imageResource);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setBackground(
                    makeRoundedBackground(Color.rgb(35, 70, 52), 16));
            image.setClipToOutline(true);

            imageFrame.addView(image,
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            TextView placeholder = new TextView(this);
            placeholder.setText(getRecipeEmoji(name));
            placeholder.setTextSize(64);
            placeholder.setGravity(Gravity.CENTER);

            imageFrame.addView(placeholder,
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
        }

        card.addView(imageFrame);

        // Recipe title
        TextView title = new TextView(this);
        title.setText(name);
        title.setTextColor(Color.rgb(244, 240, 228));
        title.setTextSize(20);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dp(8);
        card.addView(title, titleParams);

        // Missing ingredient
        TextView missingText = new TextView(this);
        missingText.setText("Missing: " + missingIngredient);
        missingText.setTextColor(Color.rgb(212, 184, 121));
        missingText.setTextSize(14);

        LinearLayout.LayoutParams missingParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        missingParams.bottomMargin = dp(14);
        card.addView(missingText, missingParams);

        // View recipe button
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

        card.addView(button);
        almostThereContainer.addView(card);
    }




    private void addRecipeCard(int id, String name, String steps) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(16));

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
        cardParams.setMargins(0, 0, 0, dp(18));
        card.setLayoutParams(cardParams);

        // Large visual area for the recipe image.
        FrameLayout imageFrame = new FrameLayout(this);
        imageFrame.setBackground(makeRoundedBackground(
                Color.rgb(35, 70, 52), 16));

        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(170));
        imageParams.bottomMargin = dp(14);
        imageFrame.setLayoutParams(imageParams);

        int imageResource = findRecipeImage(name);

        if (imageResource != 0) {
            ImageView image = new ImageView(this);
            image.setImageResource(imageResource);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setBackground(makeRoundedBackground(
                    Color.rgb(35, 70, 52), 16));
            image.setClipToOutline(true);

            imageFrame.addView(image,
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            TextView placeholder = new TextView(this);
            placeholder.setText(getRecipeEmoji(name));
            placeholder.setTextSize(64);
            placeholder.setGravity(Gravity.CENTER);

            imageFrame.addView(placeholder,
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
        }

        card.addView(imageFrame);

        TextView title = new TextView(this);
        title.setText(name);
        title.setTextColor(Color.rgb(244, 240, 228));
        title.setTextSize(20);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dp(8);
        card.addView(title, titleParams);

        TextView description = new TextView(this);
        description.setText("✓  All required ingredients available");
        description.setTextColor(Color.rgb(185, 203, 175));
        description.setTextSize(13);

        LinearLayout.LayoutParams descriptionParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        descriptionParams.bottomMargin = dp(14);
        card.addView(description, descriptionParams);

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

        card.addView(button);
        recipeContainer.addView(card);
    }

    private GradientDrawable makeRoundedBackground(int color, int radiusDp) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(color);
        background.setCornerRadius(dp(radiusDp));
        return background;
    }


    private int findRecipeImage(String recipeName) {
        String name = recipeName.toLowerCase(Locale.ROOT).trim();

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


    private String getRecipeEmoji(String recipeName) {
        String name = recipeName.toLowerCase(Locale.ROOT);

        if (name.contains("pasta") || name.contains("spaghetti")
                || name.contains("macaroni")) {
            return "🍝";
        } else if (name.contains("omelette") || name.contains("omelet")
                || name.contains("egg")) {
            return "🍳";
        } else if (name.contains("sandwich") || name.contains("toast")) {
            return "🥪";
        } else if (name.contains("soup")) {
            return "🍲";
        } else if (name.contains("chicken")) {
            return "🍗";
        } else if (name.contains("salad")) {
            return "🥗";
        } else if (name.contains("rice")) {
            return "🍚";
        } else if (name.contains("bread")) {
            return "🍞";
        } else {
            return "🍽️";
        }
    }


    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

}
