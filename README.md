Smart Pantry Manager

This is my project for the Mobile App Development 700 assignment. The idea is to build an Android app that helps reduce food waste by keeping track of the ingredients you have at home and suggesting recipes you can make with them.

The main thing that makes this app different from a normal recipe app is the strict-matching rule. A recipe should only be suggested if the user has every required ingredient in the correct quantity. If even one ingredient is missing, that recipe should not appear in the main suggestions list.

Main Features

Pantry Management: Users can add, edit and delete ingredients. Each ingredient will have a name, quantity and unit of measurement.

Pantry List: A screen will display the ingredients currently stored in the pantry using a RecyclerView.

Recipe Suggestions: The app will compare the ingredients in the pantry with the requirements of each recipe. Only recipes that meet all the requirements will be suggested.

Recipe Collection: The app will include 15–20 recipes with their ingredients and preparation steps. These recipes will be stored in the database and made available when the app is first used.

Recipe Details: Users will be able to select a suggested recipe to view its ingredients and preparation instructions.

Settings: A settings screen will provide basic application options.

Database and Technologies

## Database and Technologies

I will use SQLite with SQLiteOpenHelper to store the pantry ingredients and recipes. I chose SQLite because it allows the app to store data directly on the device without needing an internet connection or a separate database server.

SQLiteOpenHelper will help me create and manage the database, while SQL queries will be used to add, view, update and delete records. This will also allow the pantry data to remain available when the app is closed and opened again.

The application is being developed in Java using Android Studio.

How to Run the Project

1. Clone or download this repository from GitHub.
2. Open Android Studio and select File → Open.
3. Choose the project folder and wait for Gradle synchronisation to finish.
4. Select an Android emulator or connect a compatible Android device.
5. Click the Run button in Android Studio to build and launch the app.

Once implemented, the recipe database will be populated with the initial collection of recipes when the app is first run.

Project Status

The project has been created in Android Studio, and GitHub is being used to track its development. The next steps are to build the screens, implement database storage and CRUD operations, and develop and test the strict recipe-matching logic.

I will update this README as the project develops.