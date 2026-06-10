package com.moviesapi.movies_api.repository;

import com.moviesapi.movies_api.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
