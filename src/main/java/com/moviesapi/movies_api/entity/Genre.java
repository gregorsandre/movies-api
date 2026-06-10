package com.moviesapi.movies_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private List<Movie> movies = new ArrayList<>();

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Movie> getMovies() { return movies; }
    public void setMovies(List<Movie> movies) { this.movies = movies; }
}
