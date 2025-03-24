package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Movie adapter class, used to display movie data in RecyclerView
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movies;

    /**
     * Constructor
     * @param context Context
     * @param movies Movie list
     */
    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // Set movie title, show error message if there's an error
        if (movie.hasTitleError()) {
            holder.textViewTitle.setText(context.getString(R.string.title_placeholder));
            holder.textViewTitle.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.textViewTitle.setText(movie.getTitle());
            holder.textViewTitle.setTextColor(context.getResources().getColor(android.R.color.black));
        }

        // Set movie year, show error message if there's an error
        if (movie.hasYearError()) {
            if (movie.getYear() != null) {
                // Has year but format error (like negative or decimal)
                holder.textViewYear.setText(context.getString(R.string.year_error_format, movie.getYear()));
            } else {
                // Complete parsing failure
                holder.textViewYear.setText(context.getString(R.string.year_error));
            }
            holder.textViewYear.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else if (movie.getYear() != null) {
            holder.textViewYear.setText(context.getString(R.string.year_label, movie.getYear()));
            holder.textViewYear.setTextColor(context.getResources().getColor(android.R.color.black));
        } else {
            holder.textViewYear.setText(context.getString(R.string.year_placeholder));
            holder.textViewYear.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Set movie genre, show error message if there's an error
        if (movie.hasGenreError() || movie.getGenre() == null) {
            holder.textViewGenre.setText(context.getString(R.string.genre_error));
            holder.textViewGenre.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.textViewGenre.setText(context.getString(R.string.genre_label, movie.getGenre()));
            holder.textViewGenre.setTextColor(context.getResources().getColor(android.R.color.black));
        }

        // Set movie poster, enhanced poster loading logic
        loadMoviePoster(holder.imageViewPoster, movie);
    }

    /**
     * Enhanced poster loading logic, try multiple ways to match movie posters
     * @param imageView ImageView to display the poster
     * @param movie Movie object
     */
    private void loadMoviePoster(ImageView imageView, Movie movie) {
        // If no movie object, display placeholder directly
        if (movie == null) {
            Log.d("MovieAdapter", "Movie object is null, using placeholder");
            imageView.setImageResource(R.drawable.placeholder_poster);
            return;
        }
        
        // Log movie information being loaded
        Log.d("MovieAdapter", "Trying to load movie poster: " + 
              (movie.getTitle() != null ? movie.getTitle() : "[No title]") + 
              ", posterResource: " + movie.getPosterResource());
        
        // If poster resource is null or movie has poster error, display placeholder
        if (movie.getPosterResource() == null || movie.hasPosterError()) {
            Log.d("MovieAdapter", "Movie poster resource is null or has error for: " + 
                  (movie.getTitle() != null ? movie.getTitle() : "[No title]") + ", using placeholder");
            imageView.setImageResource(R.drawable.placeholder_poster);
            return;
        }
        
        // Try direct mapping for other known movies with poster resource
        String resourceName = null;
        
        if ("interstellar_poster".equals(movie.getPosterResource())) {
            Log.d("MovieAdapter", "Using direct mapping for Interstellar");
            resourceName = "interstellar";
        } else if ("matrix_poster".equals(movie.getPosterResource())) {
            Log.d("MovieAdapter", "Using direct mapping for The Matrix");
            resourceName = "thematrix";
        } else if ("inception_poster".equals(movie.getPosterResource())) {
            Log.d("MovieAdapter", "Using direct mapping for Inception");
            resourceName = "inception";
        } else if ("dark_knight_poster".equals(movie.getPosterResource())) {
            Log.d("MovieAdapter", "Using direct mapping for Dark Knight");
            resourceName = "dark_knight";
        } else if ("pulp_fiction_poster".equals(movie.getPosterResource())) {
            Log.d("MovieAdapter", "Using direct mapping for Pulp Fiction");
            resourceName = "pulpfiction";
        } else if ("avatar_poster".equals(movie.getPosterResource())) {
            Log.d("MovieAdapter", "Using direct mapping for Avatar");
            resourceName = "avatar";
        } else if ("titanic_poster".equals(movie.getPosterResource())) {
            Log.d("MovieAdapter", "Using direct mapping for Titanic");
            resourceName = "titanic";
        } else if ("godfather_poster".equals(movie.getPosterResource())) {
            Log.d("MovieAdapter", "Using direct mapping for Godfather");
            resourceName = "thegodfather";
        }
        
        // If we found a direct mapping, try to load it
        if (resourceName != null) {
            int resourceId = context.getResources().getIdentifier(
                    resourceName, "drawable", context.getPackageName());
            if (resourceId != 0) {
                imageView.setImageResource(resourceId);
                return;
            }
        }
        
        // For other posters or fallback approach, try multiple ways to load the image
        int resourceId = 0;
        
        try {
            // Method 1: Try using posterResource attribute directly
            resourceId = context.getResources().getIdentifier(
                    movie.getPosterResource(), "drawable", context.getPackageName());
            Log.d("MovieAdapter", "Method 1: Trying to load " + movie.getPosterResource() + ", result: " + (resourceId != 0 ? "success" : "failure"));
            
            // Method 2: Try converting posterResource to lowercase without _poster suffix
            if (resourceId == 0 && movie.getPosterResource().endsWith("_poster")) {
                String strippedName = movie.getPosterResource().replace("_poster", "").toLowerCase();
                resourceId = context.getResources().getIdentifier(
                        strippedName, "drawable", context.getPackageName());
                Log.d("MovieAdapter", "Method 2: Trying to load " + strippedName + ", result: " + (resourceId != 0 ? "success" : "failure"));
            }
            
            // Method 3: Try other common transformations of the poster name
            if (resourceId == 0) {
                // Try removing possible file extensions and prefixes/suffixes
                String cleanName = movie.getPosterResource()
                        .replace("_poster", "")
                        .replace(".png", "")
                        .replace(".jpg", "");
                resourceId = context.getResources().getIdentifier(
                        cleanName, "drawable", context.getPackageName());
                Log.d("MovieAdapter", "Method 3: Trying to load cleaned name " + cleanName + ", result: " + (resourceId != 0 ? "success" : "failure"));
                
                // If that doesn't work, try with "the" prefix for known movies
                if (resourceId == 0) {
                    String withThePrefix = "the" + cleanName;
                    resourceId = context.getResources().getIdentifier(
                            withThePrefix, "drawable", context.getPackageName());
                    Log.d("MovieAdapter", "Method 3.1: Trying with 'the' prefix: " + withThePrefix + ", result: " + (resourceId != 0 ? "success" : "failure"));
                }
            }
            
            // Set poster image
            if (resourceId != 0) {
                Log.d("MovieAdapter", "Successfully found resource ID: " + resourceId + " for movie: " + 
                     (movie.getTitle() != null ? movie.getTitle() : "[No title]"));
                imageView.setImageResource(resourceId);
            } else {
                // All attempts failed, use placeholder
                Log.w("MovieAdapter", "Cannot find poster resource for movie: " + 
                     (movie.getTitle() != null ? movie.getTitle() : "[No title]") + ", using placeholder");
                imageView.setImageResource(R.drawable.placeholder_poster);
            }
        } catch (Exception e) {
            // Exception handling, log and use placeholder
            Log.e("MovieAdapter", "Failed to load poster: " + e.getMessage() + " for movie: " + 
                 (movie.getTitle() != null ? movie.getTitle() : "[No title]"));
            imageView.setImageResource(R.drawable.placeholder_poster);
        }
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    /**
     * Update movie list data
     * @param movies New movie list
     */
    public void updateMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    /**
     * Movie ViewHolder class, holds views in list items
     */
    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPoster;
        TextView textViewTitle;
        TextView textViewYear;
        TextView textViewGenre;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewYear = itemView.findViewById(R.id.textViewYear);
            textViewGenre = itemView.findViewById(R.id.textViewGenre);
        }
    }
}