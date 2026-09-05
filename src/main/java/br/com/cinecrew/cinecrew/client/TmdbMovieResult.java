package br.com.cinecrew.cinecrew.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbMovieResult(
        Long id,
        String title,
        String overview,

        @JsonProperty("poster_path")
        String posterPath,

        @JsonProperty("release_date")
        String releaseDate
) {}
