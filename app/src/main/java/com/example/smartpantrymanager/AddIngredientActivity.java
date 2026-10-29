package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etIngredientQuantity;
    private Spinner spinnerIngredientUnit;
    private TextView tvSelectedExpiryDate;

    private DatabaseHelper databaseHelper;
    private int ingredientId = -1;
    private String expiryDate = null;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        etIngredientName = findViewById(R.id.etIngredientName);
        etIngredientQuantity = findViewById(R.id.etIngredientQuantity);
        spinnerIngredientUnit = findViewById(R.id.spinnerIngredientUnit);
        tvSelectedExpiryDate = findViewById(R.id.tvSelectedExpiryDate);

        Button btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        Button btnCancelIngredient = findViewById(R.id.btnCancelIngredient);
        Button btnSelectExpiryDate = findViewById(R.id.btnSelectExpiryDate);
        Button btnClearExpiryDate = findViewById(R.id.btnClearExpiryDate);
        ButtonStyler.primary(btnSaveIngredient);
        ButtonStyler.secondary(btnSelectExpiryDate);
        ButtonStyler.secondary(btnClearExpiryDate);
        ButtonStyler.secondary(btnCancelIngredient);

        GradientDrawable saveBackground = new GradientDrawable();
        saveBackground.setColor(Color.rgb(212, 184, 121));
        saveBackground.setCornerRadius(dp(14));

        btnSaveIngredient.setBackgroundTintList(null);
        btnSaveIngredient.setBackground(saveBackground);
        btnSaveIngredient.setTextColor(Color.rgb(16, 37, 29));

        databaseHelper = new DatabaseHelper(this);

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

            expiryDate = getIntent().getStringExtra("ingredient_expiry_date");
            updateExpiryDateLabel();

            btnSaveIngredient.setText("Update Ingredient");
        }

        else {
            String catalogueName =
                    getIntent().getStringExtra("catalogue_name");

            String catalogueUnit =
                    getIntent().getStringExtra("catalogue_unit");

            if (catalogueName != null) {
                etIngredientName.setText(catalogueName);
            }

            if (catalogueUnit != null) {
                for (int i = 0;
                     i < spinnerIngredientUnit.getCount();
                     i++) {

                    if (spinnerIngredientUnit.getItemAtPosition(i)
                            .toString().equals(catalogueUnit)) {
                        spinnerIngredientUnit.setSelection(i);
                        break;
                    }
                }
            }
        }


        btnSelectExpiryDate.setOnClickListener(v -> showDatePicker());

        btnClearExpiryDate.setOnClickListener(v -> {
            expiryDate = null;
            updateExpiryDateLabel();
        });

        btnSaveIngredient.setOnClickListener(v -> saveIngredient());
        btnCancelIngredient.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        if (expiryDate != null) {
            try {
                calendar.setTime(dateFormat.parse(expiryDate));
            } catch (Exception ignored) {
                // Use today's date if the saved date cannot be parsed.
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    expiryDate = dateFormat.format(selectedDate.getTime());
                    updateExpiryDateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void updateExpiryDateLabel() {
        if (expiryDate == null || expiryDate.isEmpty()) {
            tvSelectedExpiryDate.setText("No expiry date selected");
        } else {
            tvSelectedExpiryDate.setText("Expiry date: " + expiryDate);
        }
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
            successful = databaseHelper.addIngredient(
                    name, quantity, unit, expiryDate);
        } else {
            successful = databaseHelper.updateIngredient(
                    ingredientId, name, quantity, unit, expiryDate);
        }

        if (successful) {
            Toast.makeText(
                    this,
                    ingredientId == -1
                            ? "Ingredient saved successfully!"
                            : "Ingredient updated successfully!",
                    Toast.LENGTH_SHORT
            ).show();

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
