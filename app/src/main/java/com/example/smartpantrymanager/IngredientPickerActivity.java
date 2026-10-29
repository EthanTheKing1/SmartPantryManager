package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IngredientPickerActivity extends AppCompatActivity {

    private EditText etSearch;
    private LinearLayout categoryContainer;
    private LinearLayout ingredientContainer;
    private TextView tvNoIngredients;

    private String selectedCategory = "All";
    private final List<PantryOption> catalogue = new ArrayList<>();

    private static class PantryOption {
        final String name;
        final String category;
        final String unit;

        PantryOption(String name, String category, String unit) {
            this.name = name;
            this.category = category;
            this.unit = unit;
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
        addOptions("Vegetables", "pieces",
                "Avocado", "Broccoli", "Cabbage", "Carrot",
                "Cucumber", "Garlic", "Onion", "Potato",
                "Spinach", "Tomato");

        addOptions("Fruit", "pieces",
                "Apple", "Banana", "Lemon", "Mango",
                "Orange", "Pear");

        addOptions("Dairy", "millilitres (ml)",
                "Milk", "Cream");

        addOptions("Dairy", "grams (g)",
                "Butter", "Cheese", "Yoghurt");

        addOptions("Protein", "pieces",
                "Chicken breast", "Egg", "Fish fillet");

        addOptions("Grains", "grams (g)",
                "Flour", "Oats", "Pasta", "Rice");

        addOptions("Bakery", "pieces",
                "Bread", "Bread rolls", "Tortilla wraps");

        addOptions("Pantry", "millilitres (ml)",
                "Cooking oil", "Soy sauce", "Vinegar");

        addOptions("Pantry", "grams (g)",
                "Sugar", "Salt");
    }

    private void addOptions(
            String category, String unit, String... names) {
        for (String name : names) {
            catalogue.add(new PantryOption(name, category, unit));
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

            Button button = new Button(this);
            button.setText(option.name + "    ·    " + option.unit);
            button.setTextSize(14);
            button.setAllCaps(false);
            button.setGravity(
                    android.view.Gravity.CENTER_VERTICAL
                            | android.view.Gravity.START);
            button.setPadding(dp(16), 0, dp(16), 0);
            ButtonStyler.secondary(button);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(54));
            params.setMargins(0, 0, 0, dp(10));
            button.setLayoutParams(params);

            button.setOnClickListener(v -> selectIngredient(option));
            ingredientContainer.addView(button);
            results++;
        }

        tvNoIngredients.setVisibility(
                results == 0 ? View.VISIBLE : View.GONE);
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
