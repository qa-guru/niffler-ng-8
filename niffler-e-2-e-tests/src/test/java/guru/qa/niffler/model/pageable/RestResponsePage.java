package guru.qa.niffler.model.pageable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class RestResponsePage<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestResponsePage(
                            @JsonProperty List<T> content,
                            @JsonProperty Integer number,
                            @JsonProperty Integer size,
                            @JsonProperty Integer totalElements,
                            @JsonProperty Pageable pageable,
                            @JsonProperty boolean last,
                            @JsonProperty Integer totalPages,
                            @JsonProperty Sort sort,
                            @JsonProperty boolean first,
                            @JsonProperty Integer numberOfElements,
                            @JsonProperty boolean empty){
        super(content,pageable,totalElements);
    }

    public RestResponsePage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public RestResponsePage(List<T> content) {
        super(content);
    }

    public RestResponsePage() {
        super(new ArrayList<T>());
    }
}
