package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RECIPE_NAME = "recipe_name";
    public static final String COLUMN_RECIPE_STEPS = "preparation_steps";

    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";
    public static final String COLUMN_RECIPE_INGREDIENT_ID =
            "recipe_ingredient_id";
    public static final String COLUMN_REQUIRED_QUANTITY =
            "required_quantity";
    public static final String COLUMN_REQUIRED_UNIT =
            "required_unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createIngredientsTable(db);
        createRecipeTables(db);
        seedRecipes(db);
    }

    private void createIngredientsTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_QUANTITY + " REAL NOT NULL, " +
                        COLUMN_UNIT + " TEXT NOT NULL, " +
                        COLUMN_EXPIRY_DATE + " TEXT)"
        );
    }

    private void createRecipeTables(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPES + " (" +
                        COLUMN_RECIPE_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                        COLUMN_RECIPE_STEPS + " TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPE_INGREDIENTS + " (" +
                        COLUMN_RECIPE_INGREDIENT_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_REQUIRED_QUANTITY + " REAL NOT NULL, " +
                        COLUMN_REQUIRED_UNIT + " TEXT NOT NULL, " +
                        "FOREIGN KEY (" + COLUMN_RECIPE_ID + ") REFERENCES " +
                        TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + "))"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL(
                    "ALTER TABLE " + TABLE_INGREDIENTS +
                            " ADD COLUMN " + COLUMN_EXPIRY_DATE + " TEXT"
            );
        }

        createRecipeTables(db);

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_RECIPES, null);

        int recipeCount = 0;

        try {
            if (cursor.moveToFirst()) {
                recipeCount = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }

        if (recipeCount == 0) {
            seedRecipes(db);
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        insertRecipe(db, "Cheese Omelette",
                "1. Beat the eggs.\n2. Cook in a pan.\n" +
                        "3. Add cheese, fold and serve.",
                new String[][]{
                        {"eggs", "2", "pieces"},
                        {"cheese", "30", "grams"}
                });

        insertRecipe(db, "Tomato Pasta",
                "1. Boil the pasta.\n2. Cook chopped tomatoes.\n" +
                        "3. Mix together and serve.",
                new String[][]{
                        {"pasta", "150", "grams"},
                        {"tomato", "2", "pieces"}
                });

        insertRecipe(db, "Banana Pancakes",
                "1. Mash the banana.\n2. Mix with egg and flour.\n" +
                        "3. Cook small pancakes in a pan.",
                new String[][]{
                        {"banana", "1", "pieces"},
                        {"eggs", "1", "pieces"},
                        {"flour", "100", "grams"}
                });

        insertRecipe(db, "Garlic Butter Pasta",
                "1. Boil the pasta.\n2. Melt butter and cook garlic.\n" +
                        "3. Toss the pasta in the sauce.",
                new String[][]{
                        {"pasta", "150", "grams"},
                        {"butter", "20", "grams"},
                        {"garlic", "2", "pieces"}
                });

        insertRecipe(db, "Cheese Sandwich",
                "1. Place cheese between bread slices.\n" +
                        "2. Toast in a pan until golden.",
                new String[][]{
                        {"bread", "2", "pieces"},
                        {"cheese", "40", "grams"}
                });

        insertRecipe(db, "French Toast",
                "1. Beat the egg.\n2. Dip bread into the egg.\n" +
                        "3. Fry both sides until golden.",
                new String[][]{
                        {"bread", "2", "pieces"},
                        {"eggs", "1", "pieces"},
                        {"milk", "50", "millilitres"}
                });

        insertRecipe(db, "Tomato and Cheese Toast",
                "1. Place sliced tomato and cheese on bread.\n" +
                        "2. Toast until the cheese melts.",
                new String[][]{
                        {"bread", "2", "pieces"},
                        {"tomato", "1", "pieces"},
                        {"cheese", "30", "grams"}
                });

        insertRecipe(db, "Banana Smoothie",
                "1. Peel the banana.\n2. Blend with milk until smooth.",
                new String[][]{
                        {"banana", "1", "pieces"},
                        {"milk", "250", "millilitres"}
                });

        insertRecipe(db, "Fluffy Scrambled Eggs",
                "1. Beat the eggs with milk.\n" +
                        "2. Stir gently in a pan until cooked.",
                new String[][]{
                        {"eggs", "2", "pieces"},
                        {"milk", "30", "millilitres"}
                });

        insertRecipe(db, "Simple Flour Flatbread",
                "1. Mix flour and water into a dough.\n" +
                        "2. Roll flat and cook in a hot pan.",
                new String[][]{
                        {"flour", "150", "grams"},
                        {"water", "100", "millilitres"}
                });

        insertRecipe(db, "Garlic Butter Bread",
                "1. Mix softened butter with chopped garlic.\n" +
                        "2. Spread on bread and toast.",
                new String[][]{
                        {"bread", "2", "pieces"},
                        {"butter", "20", "grams"},
                        {"garlic", "1", "pieces"}
                });

        insertRecipe(db, "Tomato Omelette",
                "1. Beat the eggs.\n2. Add chopped tomato.\n" +
                        "3. Cook in a pan until set.",
                new String[][]{
                        {"eggs", "2", "pieces"},
                        {"tomato", "1", "pieces"}
                });

        insertRecipe(db, "Cheesy Pasta",
                "1. Boil the pasta.\n2. Drain and stir in cheese.",
                new String[][]{
                        {"pasta", "150", "grams"},
                        {"cheese", "50", "grams"}
                });

        insertRecipe(db, "Banana French Toast",
                "1. Mash the banana and mix with egg.\n" +
                        "2. Dip bread into the mixture and fry.",
                new String[][]{
                        {"banana", "1", "pieces"},
                        {"eggs", "1", "pieces"},
                        {"bread", "2", "pieces"}
                });

        insertRecipe(db, "Tomato Pasta with Cheese",
                "1. Boil the pasta.\n2. Cook chopped tomatoes.\n" +
                        "3. Combine and top with cheese.",
                new String[][]{
                        {"pasta", "150", "grams"},
                        {"tomato", "2", "pieces"},
                        {"cheese", "30", "grams"}
                });

        insertRecipe(db, "Creamy Garlic Pasta",
                "1. Boil the pasta.\n2. Cook garlic in butter.\n" +
                        "3. Add milk, stir, then combine with pasta.",
                new String[][]{
                        {"pasta", "150", "grams"},
                        {"garlic", "2", "pieces"},
                        {"butter", "15", "grams"},
                        {"milk", "100", "millilitres"}
                });

        insertRecipe(db, "Egg and Cheese Sandwich",
                "1. Cook the egg in a pan.\n" +
                        "2. Place it with cheese between bread slices.",
                new String[][]{
                        {"eggs", "1", "pieces"},
                        {"cheese", "30", "grams"},
                        {"bread", "2", "pieces"}
                });

        insertRecipe(db, "Banana Milk Bowl",
                "1. Slice the banana.\n2. Place in a bowl and add milk.",
                new String[][]{
                        {"banana", "1", "pieces"},
                        {"milk", "150", "millilitres"}
                });

        insertRecipe(db, "Tomato Garlic Pasta",
                "1. Boil the pasta.\n2. Cook chopped tomato and garlic.\n" +
                        "3. Mix the sauce with the pasta.",
                new String[][]{
                        {"pasta", "150", "grams"},
                        {"tomato", "2", "pieces"},
                        {"garlic", "2", "pieces"}
                });

        insertRecipe(db, "Cheese and Garlic Flatbread",
                "1. Make flatbread dough with flour and water.\n" +
                        "2. Flatten, add cheese and garlic, then cook in a pan.",
                new String[][]{
                        {"flour", "150", "grams"},
                        {"water", "100", "millilitres"},
                        {"cheese", "30", "grams"},
                        {"garlic", "1", "pieces"}
                });
    }

    private void insertRecipe(
            SQLiteDatabase db,
            String name,
            String steps,
            String[][] ingredients) {

        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COLUMN_RECIPE_NAME, name);
        recipeValues.put(COLUMN_RECIPE_STEPS, steps);

        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        if (recipeId == -1) {
            return;
        }

        for (String[] ingredient : ingredients) {
            ContentValues ingredientValues = new ContentValues();

            ingredientValues.put(COLUMN_RECIPE_ID, recipeId);
            ingredientValues.put(COLUMN_NAME, ingredient[0]);
            ingredientValues.put(
                    COLUMN_REQUIRED_QUANTITY,
                    Double.parseDouble(ingredient[1]));
            ingredientValues.put(COLUMN_REQUIRED_UNIT, ingredient[2]);

            db.insert(
                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    ingredientValues);
        }
    }

    public Cursor getAllRecipes() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_RECIPE_NAME + " ASC"
        );
    }

    public Cursor getRecipeIngredients(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                COLUMN_NAME + " ASC"
        );
    }



    public boolean addIngredient(
            String name, double quantity, String unit, String expiryDate) {

        SQLiteDatabase db = getWritableDatabase();
        String normalisedName = normaliseIngredientName(name);
        String normalisedExpiry = normaliseExpiryDate(expiryDate);

        Cursor cursor = db.query(
                TABLE_INGREDIENTS,
                new String[]{
                        COLUMN_ID,
                        COLUMN_NAME,
                        COLUMN_QUANTITY,
                        COLUMN_UNIT,
                        COLUMN_EXPIRY_DATE
                },
                null, null, null, null, null
        );

        try {
            while (cursor.moveToNext()) {
                int existingId = cursor.getInt(0);
                String existingName = cursor.getString(1);
                double existingQuantity = cursor.getDouble(2);
                String existingUnit = cursor.getString(3);
                String existingExpiry = cursor.getString(4);

                if (!normaliseIngredientName(existingName)
                        .equals(normalisedName)) {
                    continue;
                }

                if (!existingUnit.equalsIgnoreCase(unit)) {
                    continue;
                }

                if (!normaliseExpiryDate(existingExpiry)
                        .equals(normalisedExpiry)) {
                    continue;
                }

                ContentValues values = new ContentValues();
                values.put(
                        COLUMN_QUANTITY,
                        existingQuantity + quantity
                );

                return db.update(
                        TABLE_INGREDIENTS,
                        values,
                        COLUMN_ID + " = ?",
                        new String[]{String.valueOf(existingId)}
                ) > 0;
            }
        } finally {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name.trim());
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);

        if (normalisedExpiry == null) {
            values.putNull(COLUMN_EXPIRY_DATE);
        } else {
            values.put(COLUMN_EXPIRY_DATE, normalisedExpiry);
        }

        return db.insert(TABLE_INGREDIENTS, null, values) != -1;
    }

    private String normaliseExpiryDate(String expiryDate) {
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            return null;
        }

        return expiryDate.trim();
    }


    private String normaliseIngredientName(String name) {
        String normalised = name.trim().toLowerCase(
                java.util.Locale.ROOT);

        if (normalised.endsWith("ies") && normalised.length() > 3) {
            return normalised.substring(0, normalised.length() - 3) + "y";
        }

        if (normalised.endsWith("oes") && normalised.length() > 3) {
            return normalised.substring(0, normalised.length() - 2);
        }

        if (normalised.endsWith("s") && normalised.length() > 1) {
            return normalised.substring(0, normalised.length() - 1);
        }

        return normalised;
    }


    public boolean updateIngredient(
            int id, String name, double quantity,
            String unit, String expiryDate) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);

        if (expiryDate == null || expiryDate.isEmpty()) {
            values.putNull(COLUMN_EXPIRY_DATE);
        } else {
            values.put(COLUMN_EXPIRY_DATE, expiryDate);
        }

        int rowsUpdated = db.update(
                TABLE_INGREDIENTS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return rowsUpdated > 0;
    }

    public Cursor getAllIngredients() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_INGREDIENTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );
    }

    public boolean deleteIngredient(int id) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = db.delete(
                TABLE_INGREDIENTS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return rowsDeleted > 0;
    }
}
