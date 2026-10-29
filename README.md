# Smart Pantry Manager

Smart Pantry Manager is my project for the Mobile App Development 700 assignment. The aim of the app is to help reduce food waste by keeping track of the ingredients available at home and suggesting recipes that can be made with them.

Instead of just showing recipes and leaving the user to figure out whether they have everything, the app checks the ingredients and quantities available in the pantry. A recipe will only appear in the main suggestions list if the user has everything required to make it. If something is missing or there isn't enough of an ingredient, the recipe won't appear in the main list. The app also has an **Almost There** section for recipes that are missing just one ingredient.

## Main Features

- **Pantry Management:** Users can add, edit and delete ingredients. Each ingredient has a name, quantity, unit of measurement and an optional expiry date.
- **Pantry List:** Ingredients are displayed in a RecyclerView, with images to make the list easier to browse.
- **Recipe Suggestions:** The app checks the ingredients in the pantry against the requirements of each recipe. Only recipes with all the required ingredients in sufficient quantities appear in the main suggestions list.
- **Almost There:** Recipes that are missing only one ingredient are shown separately, so users can see what they could make if they had one more item.
- **Recipe Collection:** The app includes 20 recipes stored in the local database, along with their ingredients and preparation steps.
- **Recipe Details:** Users can open a recipe to view its ingredients, quantities, preparation instructions and image.
- **Ingredient Picker:** Users can browse and search through ingredients when adding items to their pantry.
- **Settings:** A separate settings screen provides basic application options.

## Database and Technologies

The app is developed in Java using Android Studio. SQLite, managed through `SQLiteOpenHelper`, is used to store pantry ingredients and recipe information directly on the device.

The database stores the ingredients, recipes and the ingredients required for each recipe. This allows the app to compare the pantry contents with recipe requirements and keep track of changes made by the user.

Using a local database means the pantry information remains available when the app is closed and opened again. The app also uses XML for its layouts and a custom RecyclerView adapter to display pantry ingredients.

## How to Run the Project

1. Clone or download this repository from GitHub.
2. Open Android Studio and select **File → Open**.
3. Select the project folder and allow Gradle synchronization to finish.
4. Select an Android emulator or connect a compatible Android device.
5. Click **Run** in Android Studio to build and launch the app.

The app is designed to run on a compatible Android device or emulator. Its pantry and recipe data are stored locally, so an internet connection is not required for the main features.

## Project Status

The main features have been implemented, including pantry management, local database storage, recipe suggestions, recipe details and the Almost There section. The app also includes ingredient and recipe images and has been tested on an Android device.

Further testing and final project preparation are still part of the process. This includes checking the main features, preparing the project documentation and recording the demonstration video.

## Purpose

The goal of Smart Pantry Manager is to make it easier to use ingredients that are already available at home instead of buying more food unnecessarily. By showing recipes based on what is actually in the pantry, the app aims to make meal planning simpler and help reduce food waste.
