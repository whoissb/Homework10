package com.example.myapplication;


/**
 * Movie model class
 * Contains basic movie information: title, year, genre and poster resource ID
 */
public class Movie {
    private String title;
    private Integer year;
    private String genre;
    private String posterResource;
    
    // Error flags
    private boolean hasTitleError;
    private boolean hasYearError;
    private String yearErrorMsg;
    private boolean hasGenreError;
    private boolean hasPosterError;

    /**
     * Movie class constructor
     * @param title Movie title
     * @param year Movie release year
     * @param genre Movie genre
     * @param posterResource Movie poster resource ID
     */
    public Movie(String title, Integer year, String genre, String posterResource) {
        // Initialize fields
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.posterResource = posterResource; // Poster resource can be null, use default placeholder
        
        // Initialize error flags
        this.hasTitleError = (title == null || title.trim().isEmpty());
        this.hasYearError = (year == null);
        this.yearErrorMsg = "";
        this.hasGenreError = (genre == null);
        this.hasPosterError = (posterResource == null);

        // Set default genre if null
        if (this.genre == null) {
            this.genre = "Unknown Genre";
        }
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public Integer getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public String getPosterResource() {
        return posterResource;
    }
    
    // Error flag getter methods
    public boolean hasTitleError() {
        return hasTitleError;
    }
    
    public boolean hasYearError() {
        return hasYearError;
    }
    
    public String getYearErrorMsg() {
        return yearErrorMsg;
    }
    
    public boolean hasGenreError() {
        return hasGenreError;
    }
    
    public boolean hasPosterError() {
        return hasPosterError;
    }

    // Setter methods
    public void setTitle(String title) {
        this.title = title;
        this.hasTitleError = (title == null || title.trim().isEmpty());
    }

    public void setYear(Integer year) {
        this.year = year;
        this.hasYearError = (year == null);
    }

    public void setGenre(String genre) {
        this.genre = genre;
        // Set to default if null, but still mark as error
        if (genre == null) {
            this.genre = "Unknown Genre";
            this.hasGenreError = true;
        } else {
            this.hasGenreError = false;
        }
    }

    public void setPosterResource(String posterResource) {
        this.posterResource = posterResource;
        this.hasPosterError = (posterResource == null);
    }
    
    // Error flag setter methods
    public void setHasTitleError(boolean hasTitleError) {
        this.hasTitleError = hasTitleError;
    }
    
    public void setHasYearError(boolean hasYearError) {
        this.hasYearError = hasYearError;
    }
    
    public void setYearErrorMsg(String yearErrorMsg) {
        this.yearErrorMsg = yearErrorMsg;
    }
    
    public void setHasGenreError(boolean hasGenreError) {
        this.hasGenreError = hasGenreError;
    }
    
    public void setHasPosterError(boolean hasPosterError) {
        this.hasPosterError = hasPosterError;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year=" + year +
                ", genre='" + genre + '\'' +
                ", posterResource='" + posterResource + '\'' +
                ", hasTitleError=" + hasTitleError +
                ", hasYearError=" + hasYearError +
                ", hasGenreError=" + hasGenreError +
                ", hasPosterError=" + hasPosterError +
                '}';
    }
}