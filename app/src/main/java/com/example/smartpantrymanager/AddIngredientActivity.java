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

        boolean inserted =
                databaseHelper.addIngredient(name, quantity, unit);

        if (inserted) {
            Toast.makeText(
                    this,
                    "Ingredient saved successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {
            Toast.makeText(
                    this,
                    "Could not save ingredient. Please try again.",
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
