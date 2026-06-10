package com.moviesapi.movies_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class ActorRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotNull
    @Past
    private LocalDate birthDate;

    private List<Long> movieIds;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public List<Long> getMovieIds() { return movieIds; }
    public void setMovieIds(List<Long> movieIds) { this.movieIds = movieIds; }
}
