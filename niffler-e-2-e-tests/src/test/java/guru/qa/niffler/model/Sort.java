package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Sort(
        @JsonProperty boolean empty,
        @JsonProperty boolean sorted,
        @JsonProperty boolean unsorted
) {
}
