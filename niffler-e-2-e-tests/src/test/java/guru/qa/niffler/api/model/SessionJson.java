package guru.qa.niffler.api.model;

import java.util.Date;

public record SessionJson(String username,
                          Date issuedAt,
                          Date expiresAt) {
}
