package com.moviesapi.movies_api.repository;

import com.moviesapi.movies_api.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    Page<Movie> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.releaseYear = :year")
    Page<Movie> findByReleaseYear(@Param("year") Integer year, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN m.actors a WHERE a.id = :actorId")
    Page<Movie> findByActorId(@Param("actorId") Long actorId, Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Movie> findByTitleContaining(@Param("title") String title, Pageable pageable);
}
