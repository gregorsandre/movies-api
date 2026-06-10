package com.moviesapi.movies_api;

import com.moviesapi.movies_api.entity.Actor;
import com.moviesapi.movies_api.entity.Genre;
import com.moviesapi.movies_api.entity.Movie;
import com.moviesapi.movies_api.repository.ActorRepository;
import com.moviesapi.movies_api.repository.GenreRepository;
import com.moviesapi.movies_api.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    public DataSeeder(GenreRepository genreRepository,
                      ActorRepository actorRepository,
                      MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(String... args) {
        if (genreRepository.count() > 0) return;

        // Genres
        Genre action = genre("Action");
        Genre drama = genre("Drama");
        Genre sciFi = genre("Sci-Fi");
        Genre thriller = genre("Thriller");
        Genre comedy = genre("Comedy");

        // Actors (15 total, each used in at least one movie)
        Actor keanu = actor("Keanu Reeves", "1964-09-02");
        Actor meryl = actor("Meryl Streep", "1949-06-22");
        Actor leo = actor("Leonardo DiCaprio", "1974-11-11");
        Actor sigourney = actor("Sigourney Weaver", "1949-10-08");
        Actor tom = actor("Tom Hanks", "1956-07-09");
        Actor cate = actor("Cate Blanchett", "1969-05-14");
        Actor brad = actor("Brad Pitt", "1963-12-18");
        Actor natalie = actor("Natalie Portman", "1981-06-09");
        Actor denzel = actor("Denzel Washington", "1954-12-28");
        Actor julianne = actor("Julianne Moore", "1960-12-03");
        Actor matt = actor("Matt Damon", "1970-10-08");
        Actor charlize = actor("Charlize Theron", "1975-08-07");
        Actor will = actor("Will Smith", "1968-09-25");
        Actor jodie = actor("Jodie Foster", "1962-11-19");
        Actor harrison = actor("Harrison Ford", "1942-07-13");

        // Movies (20 total, spanning 1977-2015, some with multiple genres and actors)
        movie("Star Wars: A New Hope", 1977, 121, List.of(action, sciFi), List.of(harrison));
        movie("Kramer vs. Kramer", 1979, 105, List.of(drama), List.of(meryl));
        movie("Raiders of the Lost Ark", 1981, 115, List.of(action), List.of(harrison));
        movie("Aliens", 1986, 137, List.of(action, sciFi), List.of(sigourney));
        movie("The Silence of the Lambs", 1991, 118, List.of(thriller), List.of(jodie));
        movie("Forrest Gump", 1994, 142, List.of(drama, comedy), List.of(tom));
        movie("Se7en", 1995, 127, List.of(thriller), List.of(brad));
        movie("Titanic", 1997, 194, List.of(drama), List.of(leo));
        movie("The Truman Show", 1998, 103, List.of(drama, comedy, sciFi), List.of(will));
        movie("The Matrix", 1999, 136, List.of(action, sciFi), List.of(keanu, sigourney));
        movie("Fight Club", 1999, 139, List.of(drama, thriller), List.of(brad));
        movie("Cast Away", 2000, 143, List.of(drama), List.of(tom));
        movie("Training Day", 2001, 122, List.of(thriller, drama), List.of(denzel));
        movie("Ali", 2001, 157, List.of(drama), List.of(will));
        movie("The Bourne Identity", 2002, 119, List.of(action, thriller), List.of(matt));
        movie("The Hours", 2002, 114, List.of(drama), List.of(meryl, julianne, cate));
        movie("The Aviator", 2004, 170, List.of(drama), List.of(leo, cate));
        movie("The Departed", 2006, 151, List.of(drama, thriller), List.of(leo, matt, brad));
        movie("Black Swan", 2010, 108, List.of(thriller, drama), List.of(natalie, julianne));
        movie("Mad Max: Fury Road", 2015, 120, List.of(action, sciFi), List.of(charlize));
    }

    private Genre genre(String name) {
        Genre g = new Genre();
        g.setName(name);
        return genreRepository.save(g);
    }

    private Actor actor(String name, String birthDate) {
        Actor a = new Actor();
        a.setName(name);
        a.setBirthDate(LocalDate.parse(birthDate));
        return actorRepository.save(a);
    }

    private void movie(String title, int year, int duration, List<Genre> genres, List<Actor> actors) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setReleaseYear(year);
        m.setDuration(duration);
        m.setGenres(genres);
        m.setActors(actors);
        movieRepository.save(m);
    }
}
