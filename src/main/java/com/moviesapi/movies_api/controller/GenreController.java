package com.moviesapi.movies_api.controller;

import com.moviesapi.movies_api.entity.Genre;
import com.moviesapi.movies_api.service.GenreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getAll() {
        return genreService.findAll();
    }

    @PostMapping
    public Genre create(@RequestBody Genre genre) {
        return genreService.save(genre);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        genreService.delete(id);
    }
}