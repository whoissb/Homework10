package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON utility class for reading and parsing movie data from the assets folder
 */
public class JsonUtils {
    private static final String TAG = "JsonUtils";
    private static final String FILE_NAME = "movies.json";

    /**
     * Load movie data from the assets folder
     * @param context Context
     * @return List of movies
     */
    public static List<Movie> loadMoviesFromJson(Context context) {
        List<Movie> movies = new ArrayList<>();
        int totalMovies = 0;
        int skippedMovies = 0;

        try {
            // Read JSON file content
            String jsonString = readJsonFromAssets(context, FILE_NAME);

            // Parse JSON data
            JSONArray jsonArray = new JSONArray(jsonString);
            totalMovies = jsonArray.length();

            // Iterate through JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // Check if it's an empty object (no properties)
                    if (jsonObject.length() == 0) {
                        Log.e(TAG, "Movie #" + (i + 1) + " is an empty object");
                        skippedMovies++;
                        continue;
                    }

                    Movie movie = parseMovieFromJson(jsonObject);
                    if (movie != null) {
                        movies.add(movie);
                    }
                } catch (JSONException e) {
                    // Handle single movie parsing exception
                    Log.e(TAG, "Error parsing movie #" + (i + 1) + ": " + e.getMessage());
                    skippedMovies++;
                } catch (IllegalArgumentException e) {
                    // Handle movie data validation exception
                    Log.e(TAG, "Movie #" + (i + 1) + " has invalid data: " + e.getMessage());
                    skippedMovies++;
                }
            }
            Log.i(TAG, "Movie data loading complete: Total " + totalMovies + ", Successful " + movies.size() 
                   + ", Skipped " + skippedMovies);
        } catch (IOException e) {
            // Handle file reading exception
            Log.e(TAG, "Cannot read movie data file: " + e.getMessage());
            throw new RuntimeException("Cannot read movie data file", e);
        } catch (JSONException e) {
            // Handle JSON parsing exception
            Log.e(TAG, "JSON format error: " + e.getMessage());
            throw new RuntimeException("JSON format error", e);
        }

        return movies;
    }

    /**
     * Parse movie data from JSON object
     * @param jsonObject JSON object
     * @return Movie object
     * @throws JSONException JSON parsing exception
     */
    private static Movie parseMovieFromJson(JSONObject jsonObject) throws JSONException {
        // Get field values and track errors
        String title = null;
        boolean titleError = false;
        
        if (jsonObject.has("title") && !jsonObject.isNull("title")) {
            title = jsonObject.getString("title");
            // Validate title is not empty
            if (title.trim().isEmpty()) {
                Log.e(TAG, "Title is an empty string");
                titleError = true;
            }
        } else {
            // Title is null or missing, record error
            Log.e(TAG, "Movie title is null or missing");
            titleError = true;
        }

        // Process year field, detect but don't automatically fix errors
        Integer year = null;
        boolean yearError = false;
        String yearErrorMsg = "";
        
        try {
            if (jsonObject.has("year")) {
                if (jsonObject.get("year") instanceof String) {
                    try {
                        year = Integer.parseInt(jsonObject.getString("year"));
                        if (year <= 0) {
                            yearError = true;
                            yearErrorMsg = "Year is negative: " + year;
                            Log.e(TAG, yearErrorMsg);
                        }
                    } catch (NumberFormatException e) {
                        yearError = true;
                        yearErrorMsg = "Year is not a valid number: " + jsonObject.getString("year");
                        Log.e(TAG, yearErrorMsg);
                    }
                } else if (jsonObject.get("year") instanceof Double) {
                    Double doubleYear = jsonObject.getDouble("year");
                    yearError = true;
                    yearErrorMsg = "Year is a decimal: " + doubleYear;
                    Log.e(TAG, yearErrorMsg);
                    year = doubleYear.intValue(); // Still save integer part for display
                } else {
                    year = jsonObject.getInt("year");
                    if (year <= 0) {
                        yearError = true;
                        yearErrorMsg = "Year is negative: " + year;
                        Log.e(TAG, yearErrorMsg);
                    }
                }
            } else {
                yearError = true;
                yearErrorMsg = "Year field is missing";
                Log.e(TAG, yearErrorMsg);
            }
        } catch (Exception e) {
            yearError = true;
            yearErrorMsg = "Error parsing year: " + e.getMessage();
            Log.e(TAG, yearErrorMsg);
        }

        // Get genre - record errors but don't automatically fix
        String genre = null;
        boolean genreError = false;
        
        if (jsonObject.has("genre") && !jsonObject.isNull("genre")) {
            genre = jsonObject.getString("genre");
        } else {
            genreError = true;
            Log.e(TAG, "Movie genre is missing");
        }

        // Get poster resource ID
        String posterResource = null;
        boolean posterError = false;
        
        if (jsonObject.has("poster") && !jsonObject.isNull("poster")) {
            posterResource = jsonObject.getString("poster");
            // Check if poster is empty string
            if (posterResource.trim().isEmpty()) {
                posterError = true;
                Log.e(TAG, "Poster resource is an empty string");
            }
        } else {
            posterError = true;
            Log.e(TAG, "Poster resource is missing or null");
        }

        // Create movie object - create regardless of errors, let UI layer handle display
        Movie movie = new Movie(title, year, genre, posterResource);
        
        // Set error flags to help UI layer display
        movie.setHasTitleError(titleError);
        movie.setHasYearError(yearError);
        movie.setYearErrorMsg(yearErrorMsg);
        movie.setHasGenreError(genreError);
        movie.setHasPosterError(posterError);
        
        return movie;
    }

    /**
     * Read JSON file content from assets folder
     * @param context Context
     * @param fileName File name
     * @return JSON string
     * @throws IOException File reading exception
     */
    private static String readJsonFromAssets(Context context, String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream = context.getAssets().open(fileName);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }
}