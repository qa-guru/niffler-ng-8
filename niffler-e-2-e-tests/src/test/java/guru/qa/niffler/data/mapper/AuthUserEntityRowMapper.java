package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.user.AuthUserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthUserEntityRowMapper implements RowMapper<AuthUserEntity> {

    public static final AuthUserEntityRowMapper INSTANCE = new AuthUserEntityRowMapper();

    private AuthUserEntityRowMapper() {
    }

    @Override
    public AuthUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthUserEntity user = new AuthUserEntity();
        user.setId(rs.getObject("id", UUID.class));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEnabled(rs.getBoolean("enabled"));
        user.setAccountNonExpired(rs.getBoolean("accountNonExpired"));
        user.setAccountNonLocked(rs.getBoolean("accountNonLocked"));
        user.setCredentialsNonExpired(rs.getBoolean("credentialsNonExpired"));
        return user;
    }
}