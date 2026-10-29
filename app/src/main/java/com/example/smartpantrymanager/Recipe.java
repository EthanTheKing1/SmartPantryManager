package com.example.smartpantrymanager;

public class Recipe {

    private final int id;
    private final String name;
    private final String preparationSteps;

    public Recipe(int id, String name, String preparationSteps) {
        this.id = id;
        this.name = name;
        this.preparationSteps = preparationSteps;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPreparationSteps() {
        return preparationSteps;
    }
}
