package guru.qa.niffler.db.repository.mapper;

import guru.qa.niffler.api.model.Authority;
import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthAuthorityEntityRowMapper {

    public static final AuthAuthorityEntityRowMapper INSTANCE = new AuthAuthorityEntityRowMapper();

    private AuthAuthorityEntityRowMapper() {
    }

    public AuthAuthorityEntity mapRow(ResultSet rs) throws SQLException {
        return new AuthAuthorityEntity()
                .setId(rs.getObject("id", UUID.class))
                .setAuthority(Authority.valueOf(rs.getString("authority")));
    }

}