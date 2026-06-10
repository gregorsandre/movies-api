package com.moviesapi.movies_api.controller;

import com.moviesapi.movies_api.dto.ActorRequest;
import com.moviesapi.movies_api.entity.Actor;
import com.moviesapi.movies_api.entity.Movie;
import com.moviesapi.movies_api.service.ActorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actors")
@Validated
public class ActorController {

    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Actor create(@Valid @RequestBody ActorRequest request) {
        return actorService.create(request);
    }

    @GetMapping
    public Page<Actor> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        if (name != null) {
            return actorService.findByName(name, pageRequest);
        }
        return actorService.findAll(pageRequest);
    }

    @GetMapping("/{id}")
    public Actor getById(@PathVariable Long id) {
        return actorService.findById(id);
    }

    @PatchMapping("/{id}")
    public Actor update(@PathVariable Long id, @RequestBody ActorRequest patch) {
        return actorService.update(id, patch);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @RequestParam(defaultValue = "false") boolean force) {
        actorService.delete(id, force);
    }

    @GetMapping("/{id}/movies")
    public Page<Movie> getMovies(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return actorService.getMoviesByActor(id, PageRequest.of(page, size));
    }
}
