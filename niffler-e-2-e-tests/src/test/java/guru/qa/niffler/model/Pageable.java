package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Pageable(
        @JsonProperty Integer pageNumber,
        @JsonProperty Integer pageSize,
        @JsonProperty Sort sort,
        @JsonProperty Integer offset,
        @JsonProperty boolean paged,
        @JsonProperty boolean unpaged
) {
}
