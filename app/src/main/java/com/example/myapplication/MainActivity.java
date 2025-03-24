package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main activity class, displays movie list, handles movie data loading and error scenarios
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView movieRecyclerView;
    private MovieAdapter adapter;
    private List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize movie list
        movies = new ArrayList<>();

        // Set up RecyclerView
        setupRecyclerView();

        // Load movie data
        loadMovieData();
    }

    /**
     * Set up RecyclerView and adapter
     */
    private void setupRecyclerView() {
        movieRecyclerView = findViewById(R.id.recyclerView);
        movieRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(this, movies);
        movieRecyclerView.setAdapter(adapter);
    }

    /**
     * Load movie data from JSON file
     */
    private void loadMovieData() {
        try {
            // Try to load movie data
            movies = JsonUtils.loadMoviesFromJson(this);

            // Check if there's valid movie data
            if (movies.isEmpty()) {
                showError(getString(R.string.no_movies_available));
            } else {
                // Count detected errors
                int totalErrorsDetected = 0;
                int titleErrorCount = 0;
                int yearErrorCount = 0;
                int genreErrorCount = 0;
                int posterErrorCount = 0;
                
                for (Movie movie : movies) {
                    // Check various errors
                    if (movie.hasTitleError()) {
                        titleErrorCount++;
                        totalErrorsDetected++;
                    }
                    if (movie.hasYearError()) {
                        yearErrorCount++;
                        totalErrorsDetected++;
                    }
                    if (movie.hasGenreError()) {
                        genreErrorCount++;
                        totalErrorsDetected++;
                    }
                    if (movie.hasPosterError()) {
                        posterErrorCount++;
                        totalErrorsDetected++;
                    }
                }
                
                // Display success message, including error detection information
                String message;
                if (totalErrorsDetected > 0) {
                    message = getString(R.string.movies_loaded_with_errors, 
                            movies.size(), totalErrorsDetected);
                    
                    // Add title error count information
                    if (titleErrorCount > 0) {
                        message += "\n" + getString(R.string.title_inferred_count, titleErrorCount);
                    }

                    // Log detected errors
                    Log.i("MainActivity", "Detected errors: Title errors " + titleErrorCount + 
                            ", Year errors " + yearErrorCount + ", Genre errors " + 
                            genreErrorCount + ", Poster errors " + posterErrorCount);
                } else {
                    message = getString(R.string.movies_loaded, movies.size());
                }
                
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                
                // Update adapter
                adapter.updateMovies(movies);
            }
        } catch (RuntimeException e) {
            // Handle exceptions
            if (e.getCause() instanceof FileNotFoundException) {
                showError(getString(R.string.error_file_not_found));
            } else {
                showError(getString(R.string.error_loading_data) + ": " + e.getMessage());
            }
        }
    }

    /**
     * Show error message
     * @param message Error message
     */
    private void showError(String message) {
        // Use Snackbar to display error message
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();

        // Also use Toast to display error message
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}