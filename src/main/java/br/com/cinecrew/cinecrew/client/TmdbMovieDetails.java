package br.com.cinecrew.cinecrew.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbMovieDetails(
        Long id,
        String title,
        String overview,

        @JsonProperty("poster_path")
        String posterPath,

        @JsonProperty("release_date")
        String releaseDate,
        Integer runTime
) {}
