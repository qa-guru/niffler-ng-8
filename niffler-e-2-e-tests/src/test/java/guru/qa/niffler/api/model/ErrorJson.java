package guru.qa.niffler.api.model;

public record ErrorJson(String type,
                        String title,
                        int status,
                        String detail,
                        String instance) {
}
