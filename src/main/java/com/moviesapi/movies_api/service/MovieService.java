package com.moviesapi.movies_api.service;

import com.moviesapi.movies_api.dto.MovieRequest;
import com.moviesapi.movies_api.entity.Actor;
import com.moviesapi.movies_api.entity.Genre;
import com.moviesapi.movies_api.entity.Movie;
import com.moviesapi.movies_api.exception.RelationshipConflictException;
import com.moviesapi.movies_api.exception.ResourceNotFoundException;
import com.moviesapi.movies_api.repository.ActorRepository;
import com.moviesapi.movies_api.repository.GenreRepository;
import com.moviesapi.movies_api.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;

    public MovieService(MovieRepository movieRepository,
                        GenreRepository genreRepository,
                        ActorRepository actorRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
    }

    @Transactional(readOnly = true)
    public Page<Movie> findAll(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Movie findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    @Transactional
    public Movie create(MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setDuration(request.getDuration());
        movie.getGenres().addAll(resolveGenres(request.getGenreIds()));
        movie.getActors().addAll(resolveActors(request.getActorIds()));
        return movieRepository.save(movie);
    }

    @Transactional
    public Movie update(Long id, MovieRequest patch) {
        Movie existing = findById(id);
        if (patch.getTitle() != null) {
            existing.setTitle(patch.getTitle());
        }
        if (patch.getReleaseYear() != null) {
            existing.setReleaseYear(patch.getReleaseYear());
        }
        if (patch.getDuration() != null) {
            existing.setDuration(patch.getDuration());
        }
        // null = unchanged, non-null (even empty list) = replace
        if (patch.getGenreIds() != null) {
            existing.getGenres().clear();
            existing.getGenres().addAll(resolveGenres(patch.getGenreIds()));
        }
        if (patch.getActorIds() != null) {
            existing.getActors().clear();
            existing.getActors().addAll(resolveActors(patch.getActorIds()));
        }
        return movieRepository.save(existing);
    }

    @Transactional
    public void delete(Long id, boolean force) {
        Movie movie = findById(id);
        boolean hasRelationships = !movie.getActors().isEmpty() || !movie.getGenres().isEmpty();
        if (!force && hasRelationships) {
            throw new RelationshipConflictException(
                "Cannot delete movie '" + movie.getTitle() + "' because it has associated actors or genres"
            );
        }
        if (force) {
            movie.getGenres().clear();
            movie.getActors().clear();
            movieRepository.save(movie);
        }
        movieRepository.delete(movie);
    }

    @Transactional(readOnly = true)
    public Page<Actor> getActorsByMovie(Long movieId, Pageable pageable) {
        findById(movieId);
        return actorRepository.findByMovieId(movieId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Movie> findByGenre(Long genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            throw new ResourceNotFoundException("Genre not found with id: " + genreId);
        }
        return movieRepository.findByGenreId(genreId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Movie> findByYear(Integer year, Pageable pageable) {
        return movieRepository.findByReleaseYear(year, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Movie> findByActor(Long actorId, Pageable pageable) {
        if (!actorRepository.existsById(actorId)) {
            throw new ResourceNotFoundException("Actor not found with id: " + actorId);
        }
        return movieRepository.findByActorId(actorId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Movie> searchByTitle(String title, Pageable pageable) {
        return movieRepository.findByTitleContaining(title, pageable);
    }

    private List<Genre> resolveGenres(List<Long> ids) {
        if (ids == null) return List.of();
        return ids.stream()
                .map(gid -> genreRepository.findById(gid)
                        .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + gid)))
                .toList();
    }

    private List<Actor> resolveActors(List<Long> ids) {
        if (ids == null) return List.of();
        return ids.stream()
                .map(aid -> actorRepository.findById(aid)
                        .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + aid)))
                .toList();
    }
}
