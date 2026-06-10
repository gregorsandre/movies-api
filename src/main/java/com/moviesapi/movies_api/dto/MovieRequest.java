package com.moviesapi.movies_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class MovieRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotNull
    @Min(1888)
    private Integer releaseYear;

    @NotNull
    @Positive
    private Integer duration;

    private List<Long> genreIds;

    private List<Long> actorIds;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public List<Long> getGenreIds() { return genreIds; }
    public void setGenreIds(List<Long> genreIds) { this.genreIds = genreIds; }

    public List<Long> getActorIds() { return actorIds; }
    public void setActorIds(List<Long> actorIds) { this.actorIds = actorIds; }
}
