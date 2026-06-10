package com.moviesapi.movies_api.controller;

import com.moviesapi.movies_api.dto.GenreRequest;
import com.moviesapi.movies_api.entity.Genre;
import com.moviesapi.movies_api.entity.Movie;
import com.moviesapi.movies_api.service.GenreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genres")
@Validated
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Genre create(@Valid @RequestBody GenreRequest request) {
        return genreService.create(request);
    }

    @GetMapping
    public Page<Genre> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return genreService.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Long id) {
        return genreService.findById(id);
    }

    @PatchMapping("/{id}")
    public Genre update(@PathVariable Long id, @RequestBody GenreRequest patch) {
        return genreService.update(id, patch);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @RequestParam(defaultValue = "false") boolean force) {
        genreService.delete(id, force);
    }

    @GetMapping("/{id}/movies")
    public Page<Movie> getMovies(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return genreService.getMoviesByGenre(id, PageRequest.of(page, size));
    }
}
