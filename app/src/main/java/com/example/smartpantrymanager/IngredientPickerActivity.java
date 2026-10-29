package com.example.smartpantrymanager;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IngredientPickerActivity extends AppCompatActivity {

    private EditText etSearch;
    private LinearLayout categoryContainer;
    private GridLayout ingredientContainer;
    private TextView tvNoIngredients;

    private String selectedCategory = "All";
    private final List<PantryOption> catalogue = new ArrayList<>();

    private static class PantryOption {
        final String name;
        final String category;
        final String unit;
        final String emoji;
        final String imageName;

        PantryOption(
                String name,
                String category,
                String unit,
                String emoji) {
            this.name = name;
            this.category = category;
            this.unit = unit;
            this.emoji = emoji;

            this.imageName = getImageName(name);
        }
    }

    private static String getImageName(String name) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "egg":
                return "eggs";
            case "chicken breast":
                return "chicken_breast";
            case "bread rolls":
                return "bread";
            case "tortilla wraps":
                return "bread";
            case "cooking oil":
                return "cooking_oil";
            default:
                return name.toLowerCase(Locale.ROOT)
                        .replace(" ", "_");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_picker);

        etSearch = findViewById(R.id.etIngredientSearch);
        categoryContainer = findViewById(R.id.categoryContainer);
        ingredientContainer = findViewById(R.id.ingredientContainer);
        tvNoIngredients = findViewById(R.id.tvNoIngredients);

        Button btnBack = findViewById(R.id.btnPickerBack);
        ButtonStyler.secondary(btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadCatalogue();
        buildCategories();
        displayIngredients();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                displayIngredients();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadCatalogue() {
        addOptions("Vegetables", "pieces", "🥑",
                "Avocado");
        addOptions("Vegetables", "pieces", "🥦",
                "Broccoli");
        addOptions("Vegetables", "pieces", "🥬",
                "Cabbage", "Spinach");
        addOptions("Vegetables", "pieces", "🥕",
                "Carrot");
        addOptions("Vegetables", "pieces", "🥒",
                "Cucumber");
        addOptions("Vegetables", "pieces", "🧄",
                "Garlic");
        addOptions("Vegetables", "pieces", "🧅",
                "Onion");
        addOptions("Vegetables", "pieces", "🥔",
                "Potato");
        addOptions("Vegetables", "pieces", "🍅",
                "Tomato");

        addOptions("Fruit", "pieces", "🍎",
                "Apple");
        addOptions("Fruit", "pieces", "🍌",
                "Banana");
        addOptions("Fruit", "pieces", "🍋",
                "Lemon");
        addOptions("Fruit", "pieces", "🥭",
                "Mango");
        addOptions("Fruit", "pieces", "🍊",
                "Orange");
        addOptions("Fruit", "pieces", "🍐",
                "Pear");

        addOptions("Dairy", "millilitres (ml)", "🥛",
                "Milk", "Cream");
        addOptions("Dairy", "grams (g)", "🧈",
                "Butter");
        addOptions("Dairy", "grams (g)", "🧀",
                "Cheese");
        addOptions("Dairy", "grams (g)", "🥣",
                "Yoghurt");

        addOptions("Protein", "pieces", "🍗",
                "Chicken breast");
        addOptions("Protein", "pieces", "🥚",
                "Egg");
        addOptions("Protein", "pieces", "🐟",
                "Fish fillet");

        addOptions("Grains", "grams (g)", "🌾",
                "Flour", "Oats");
        addOptions("Grains", "grams (g)", "🍝",
                "Pasta");
        addOptions("Grains", "grams (g)", "🍚",
                "Rice");

        addOptions("Bakery", "pieces", "🍞",
                "Bread");
        addOptions("Bakery", "pieces", "🥖",
                "Bread rolls");
        addOptions("Bakery", "pieces", "🌯",
                "Tortilla wraps");

        addOptions("Pantry", "millilitres (ml)", "🫒",
                "Cooking oil");
        addOptions("Pantry", "millilitres (ml)", "🥢",
                "Soy sauce");
        addOptions("Pantry", "millilitres (ml)", "🍶",
                "Vinegar");
        addOptions("Pantry", "grams (g)", "🧁",
                "Sugar");
        addOptions("Pantry", "grams (g)", "🧂",
                "Salt");
    }

    private void addOptions(
            String category, String unit, String emoji, String... names) {

        for (String name : names) {
            catalogue.add(new PantryOption(name, category, unit, emoji));
        }
    }

    private void buildCategories() {
        List<String> categories = Arrays.asList(
                "All", "Vegetables", "Fruit", "Dairy",
                "Protein", "Grains", "Bakery", "Pantry");

        categoryContainer.removeAllViews();

        for (String category : categories) {
            Button button = new Button(this);
            button.setText(category);
            button.setAllCaps(false);
            button.setTextSize(12);
            button.setMinHeight(0);
            button.setMinimumHeight(0);
            button.setPadding(dp(12), 0, dp(12), 0);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            dp(42));

            params.setMargins(0, 0, dp(8), 0);
            button.setLayoutParams(params);

            updateCategoryStyle(button, category);

            button.setOnClickListener(v -> {
                selectedCategory = category;
                buildCategories();
                displayIngredients();
            });

            categoryContainer.addView(button);
        }
    }

    private void updateCategoryStyle(
            Button button, String category) {

        if (category.equals(selectedCategory)) {
            ButtonStyler.primary(button);
        } else {
            ButtonStyler.secondary(button);
        }
    }

    private void displayIngredients() {
        ingredientContainer.removeAllViews();

        String query = etSearch.getText().toString()
                .trim().toLowerCase(Locale.ROOT);

        int results = 0;

        for (PantryOption option : catalogue) {
            boolean matchesCategory =
                    selectedCategory.equals("All")
                            || option.category.equals(selectedCategory);

            boolean matchesSearch =
                    option.name.toLowerCase(Locale.ROOT).contains(query);

            if (!matchesCategory || !matchesSearch) {
                continue;
            }

            ingredientContainer.addView(createIngredientCard(option));
            results++;
        }

        tvNoIngredients.setVisibility(
                results == 0 ? View.VISIBLE : View.GONE);
    }

    private LinearLayout createIngredientCard(PantryOption option) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(8), dp(12), dp(8), dp(12));

        GradientDrawable background = new GradientDrawable();
        background.setColor(android.graphics.Color.parseColor("#1C382B"));
        background.setCornerRadius(dp(16));
        background.setStroke(
                dp(1),
                android.graphics.Color.parseColor("#385746"));

        card.setBackground(background);
        card.setClickable(true);
        card.setFocusable(true);

        GridLayout.LayoutParams params =
                new GridLayout.LayoutParams();

        params.width = 0;
        params.height = dp(154);
        params.columnSpec = GridLayout.spec(
                GridLayout.UNDEFINED, 1f);
        params.setMargins(dp(4), dp(4), dp(4), dp(4));

        card.setLayoutParams(params);

        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setClipToOutline(true);




        int imageResId = getResources().getIdentifier(
                option.imageName,
                "drawable",
                getPackageName()
        );

        if (imageResId != 0) {
            image = new ImageView(this);
            image.setImageResource(imageResId);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            GradientDrawable imageBackground = new GradientDrawable();
            imageBackground.setColor(Color.parseColor("#294838"));
            imageBackground.setCornerRadius(dp(12));

            image.setBackground(imageBackground);
            image.setClipToOutline(true);

            LinearLayout.LayoutParams imageParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(82)
                    );

            card.addView(image, imageParams);

        } else {
            TextView placeholder = new TextView(this);
            placeholder.setText(option.emoji);
            placeholder.setTextSize(36);
            placeholder.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams placeholderParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(82)
                    );

            card.addView(placeholder, placeholderParams);
        }




        TextView name = new TextView(this);
        name.setText(option.name);
        name.setTextColor(
                android.graphics.Color.parseColor("#F4F0E4"));
        name.setTextSize(15);
        name.setTypeface(null, Typeface.BOLD);
        name.setGravity(Gravity.CENTER);
        name.setMaxLines(2);

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        nameParams.topMargin = dp(6);
        card.addView(name, nameParams);

        TextView unit = new TextView(this);
        unit.setText(option.unit);
        unit.setTextColor(
                android.graphics.Color.parseColor("#B9CBAF"));
        unit.setTextSize(12);
        unit.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams unitParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        unitParams.topMargin = dp(4);
        card.addView(unit, unitParams);

        card.setOnClickListener(v -> selectIngredient(option));

        return card;
    }

    private void selectIngredient(PantryOption option) {
        Intent intent = new Intent(
                IngredientPickerActivity.this,
                AddIngredientActivity.class);

        intent.putExtra("catalogue_name", option.name);
        intent.putExtra("catalogue_unit", option.unit);

        startActivity(intent);
        finish();
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }
}
