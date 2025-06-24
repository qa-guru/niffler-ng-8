package guru.qa.niffler.db.dao.impl.spring_jdbc.mapper;

import guru.qa.niffler.db.entity.auth.AuthUserEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthUserEntityRowMapper implements RowMapper<AuthUserEntity> {

    public static final AuthUserEntityRowMapper INSTANCE = new AuthUserEntityRowMapper();

    private AuthUserEntityRowMapper() {
    }

    @Override
    public @Nonnull AuthUserEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
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
