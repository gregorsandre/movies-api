package com.moviesapi.movies_api.service;

import com.moviesapi.movies_api.dto.ActorRequest;
import com.moviesapi.movies_api.entity.Actor;
import com.moviesapi.movies_api.entity.Movie;
import com.moviesapi.movies_api.exception.RelationshipConflictException;
import com.moviesapi.movies_api.exception.ResourceNotFoundException;
import com.moviesapi.movies_api.repository.ActorRepository;
import com.moviesapi.movies_api.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActorService {

    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    public ActorService(ActorRepository actorRepository, MovieRepository movieRepository) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional(readOnly = true)
    public Page<Actor> findAll(Pageable pageable) {
        return actorRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Actor findById(Long id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + id));
    }

    @Transactional
    public Actor create(ActorRequest request) {
        Actor actor = new Actor();
        actor.setName(request.getName());
        actor.setBirthDate(request.getBirthDate());
        Actor saved = actorRepository.save(actor);
        if (request.getMovieIds() != null) {
            replaceMovies(saved, request.getMovieIds());
        }
        return saved;
    }

    @Transactional
    public Actor update(Long id, ActorRequest patch) {
        Actor existing = findById(id);
        if (patch.getName() != null) {
            existing.setName(patch.getName());
        }
        if (patch.getBirthDate() != null) {
            existing.setBirthDate(patch.getBirthDate());
        }
        // null = unchanged, non-null (even empty list) = replace
        if (patch.getMovieIds() != null) {
            replaceMovies(existing, patch.getMovieIds());
        }
        return actorRepository.save(existing);
    }

    // Movie owns the join table, so the association must be changed through movie.getActors()
    private void replaceMovies(Actor actor, List<Long> movieIds) {
        for (Movie movie : new ArrayList<>(actor.getMovies())) {
            movie.getActors().remove(actor);
        }
        actor.getMovies().clear();
        for (Long movieId : movieIds) {
            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
            movie.getActors().add(actor);
            actor.getMovies().add(movie);
        }
    }

    @Transactional
    public void delete(Long id, boolean force) {
        Actor actor = findById(id);
        if (!force && !actor.getMovies().isEmpty()) {
            throw new RelationshipConflictException(
                "Cannot delete actor '" + actor.getName() + "' because they are associated with " +
                actor.getMovies().size() + " movies"
            );
        }
        if (force) {
            for (Movie movie : actor.getMovies()) {
                movie.getActors().remove(actor);
                movieRepository.save(movie);
            }
        }
        actorRepository.delete(actor);
    }

    @Transactional(readOnly = true)
    public Page<Movie> getMoviesByActor(Long id, Pageable pageable) {
        findById(id);
        return movieRepository.findByActorId(id, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Actor> findByName(String name, Pageable pageable) {
        return actorRepository.findByNameContaining(name, pageable);
    }
}
