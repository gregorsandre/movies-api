package com.moviesapi.movies_api.service;

import com.moviesapi.movies_api.dto.GenreRequest;
import com.moviesapi.movies_api.entity.Genre;
import com.moviesapi.movies_api.entity.Movie;
import com.moviesapi.movies_api.exception.RelationshipConflictException;
import com.moviesapi.movies_api.exception.ResourceNotFoundException;
import com.moviesapi.movies_api.repository.GenreRepository;
import com.moviesapi.movies_api.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    public GenreService(GenreRepository genreRepository, MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional(readOnly = true)
    public Page<Genre> findAll(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Genre findById(Long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        return genre.orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));
    }

    @Transactional
    public Genre create(GenreRequest request) {
        Genre genre = new Genre();
        genre.setName(request.getName());
        return genreRepository.save(genre);
    }

    @Transactional
    public Genre update(Long id, GenreRequest patch) {
        Genre existing = findById(id);
        if (patch.getName() != null) {
            existing.setName(patch.getName());
        }
        return genreRepository.save(existing);
    }

    @Transactional
    public void delete(Long id, boolean force) {
        Genre genre = findById(id);
        if (!force && !genre.getMovies().isEmpty()) {
            throw new RelationshipConflictException(
                "Cannot delete genre '" + genre.getName() + "' because it has " +
                genre.getMovies().size() + " associated movies"
            );
        }
        if (force) {
            for (Movie movie : genre.getMovies()) {
                movie.getGenres().remove(genre);
                movieRepository.save(movie);
            }
        }
        genreRepository.delete(genre);
    }

    @Transactional(readOnly = true)
    public Page<Movie> getMoviesByGenre(Long id, Pageable pageable) {
        findById(id);
        return movieRepository.findByGenreId(id, pageable);
    }
}
