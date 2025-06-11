package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UsersJson(
        @JsonProperty List<UserJson> content,
        @JsonProperty Integer number,
        @JsonProperty Integer size,
        @JsonProperty Integer totalElements,
        @JsonProperty Pageable pageable,
        @JsonProperty boolean last,
        @JsonProperty Integer totalPages,
        @JsonProperty Sort sort,
        @JsonProperty boolean first,
        @JsonProperty Integer numberOfElements,
        @JsonProperty boolean empty
        ) {
}
