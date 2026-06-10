package com.moviesapi.movies_api.controller;

import com.moviesapi.movies_api.dto.MovieRequest;
import com.moviesapi.movies_api.entity.Actor;
import com.moviesapi.movies_api.entity.Movie;
import com.moviesapi.movies_api.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@Validated
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Movie create(@Valid @RequestBody MovieRequest request) {
        return movieService.create(request);
    }

    @GetMapping
    public Page<Movie> getAll(
            @RequestParam(required = false) Long actor,
            @RequestParam(required = false) Long genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        // priority: actor → genre → year → all
        if (actor != null) return movieService.findByActor(actor, pageRequest);
        if (genre != null) return movieService.findByGenre(genre, pageRequest);
        if (year != null) return movieService.findByYear(year, pageRequest);
        return movieService.findAll(pageRequest);
    }

    @GetMapping("/search")
    public Page<Movie> search(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return movieService.searchByTitle(title, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Movie getById(@PathVariable Long id) {
        return movieService.findById(id);
    }

    @PatchMapping("/{id}")
    public Movie update(@PathVariable Long id, @RequestBody MovieRequest patch) {
        return movieService.update(id, patch);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @RequestParam(defaultValue = "false") boolean force) {
        movieService.delete(id, force);
    }

    @GetMapping("/{id}/actors")
    public Page<Actor> getActors(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return movieService.getActorsByMovie(id, PageRequest.of(page, size));
    }
}
