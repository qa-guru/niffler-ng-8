package guru.qa.niffler.db.repository.mapper;

import guru.qa.niffler.db.entity.auth.AuthUserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthUserEntityRowMapper {

    public static final AuthUserEntityRowMapper INSTANCE = new AuthUserEntityRowMapper();

    private AuthUserEntityRowMapper() {
    }

    public AuthUserEntity mapRow(ResultSet rs) throws SQLException {
        return new AuthUserEntity()
                .setId(rs.getObject("id", UUID.class))
                .setUsername(rs.getString("username"))
                .setPassword(rs.getString("password"))
                .setEnabled(rs.getBoolean("enabled"))
                .setAccountNonExpired(rs.getBoolean("account_non_expired"))
                .setAccountNonLocked(rs.getBoolean("account_non_locked"))
                .setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
    }

}
