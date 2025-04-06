package guru.qa.niffler.api.model;

import java.util.UUID;

public record CategoryJson(
    UUID id,
    String name,
    String username,
    boolean archived) {
}