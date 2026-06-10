package com.moviesapi.movies_api.repository;

import com.moviesapi.movies_api.entity.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {

    @Query("SELECT a FROM Actor a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Actor> findByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT a FROM Movie m JOIN m.actors a WHERE m.id = :movieId")
    Page<Actor> findByMovieId(@Param("movieId") Long movieId, Pageable pageable);
}
