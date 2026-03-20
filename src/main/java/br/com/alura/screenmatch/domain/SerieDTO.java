package br.com.alura.screenmatch.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * JsonIgnoreProperties serve para ignorar os atributos que não forem encontrados.
 * ignoreUnknown é usado para definir se vai ignorar ou não (para ignorar deve setar ele com valor true).
 * JsonAlias serve somente para serialização (pode ser usado um array de chaves para serializar o json).
 * JsonProperty serve tanto pra serialização quanto para deserialização.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SerieDTO(
        @JsonAlias("Title") String titulo,
        @JsonAlias("totalSeasons") Integer totalTemporadas,
        @JsonAlias("imdbRating") String avaliacao
) {
}
