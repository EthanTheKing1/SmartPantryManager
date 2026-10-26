package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etIngredientQuantity;
    private Spinner spinnerIngredientUnit;
    private DatabaseHelper databaseHelper;

    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        etIngredientName = findViewById(R.id.etIngredientName);
        etIngredientQuantity = findViewById(R.id.etIngredientQuantity);
        spinnerIngredientUnit = findViewById(R.id.spinnerIngredientUnit);

        Button btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        Button btnCancelIngredient = findViewById(R.id.btnCancelIngredient);

        databaseHelper = new DatabaseHelper(this);

        // An ID means we are editing an existing ingredient.
        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if (ingredientId != -1) {
            etIngredientName.setText(
                    getIntent().getStringExtra("ingredient_name"));

            double quantity =
                    getIntent().getDoubleExtra("ingredient_quantity", 0);

            etIngredientQuantity.setText(String.valueOf(quantity));

            String unit = getIntent().getStringExtra("ingredient_unit");

            if (unit != null) {
                for (int i = 0;
                     i < spinnerIngredientUnit.getCount();
                     i++) {

                    if (spinnerIngredientUnit.getItemAtPosition(i)
                            .toString().equals(unit)) {
                        spinnerIngredientUnit.setSelection(i);
                        break;
                    }
                }
            }

            btnSaveIngredient.setText("Update Ingredient");
        }

        btnSaveIngredient.setOnClickListener(v -> saveIngredient());
        btnCancelIngredient.setOnClickListener(v -> finish());
    }

    private void saveIngredient() {
        String name = etIngredientName.getText().toString().trim();
        String quantityText =
                etIngredientQuantity.getText().toString().trim();
        String unit = spinnerIngredientUnit.getSelectedItem().toString();

        if (name.isEmpty()) {
            etIngredientName.setError("Enter an ingredient name");
            etIngredientName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            etIngredientQuantity.setError("Enter a quantity");
            etIngredientQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);

            if (!Double.isFinite(quantity) || quantity <= 0) {
                etIngredientQuantity.setError(
                        "Quantity must be greater than zero");
                etIngredientQuantity.requestFocus();
                return;
            }

        } catch (NumberFormatException e) {
            etIngredientQuantity.setError("Enter a valid quantity");
            etIngredientQuantity.requestFocus();
            return;
        }

        boolean successful;

        if (ingredientId == -1) {
            // No existing ID: insert a new ingredient.
            successful = databaseHelper.addIngredient(
                    name, quantity, unit);
        } else {
            // Existing ID: update that ingredient.
            successful = databaseHelper.updateIngredient(
                    ingredientId, name, quantity, unit);
        }

        if (successful) {
            String message = ingredientId == -1
                    ? "Ingredient saved successfully!"
                    : "Ingredient updated successfully!";

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();

        } else {
            Toast.makeText(
                    this,
                    "Could not save changes. Please try again.",
                    Toast.LENGTH_LONG
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
