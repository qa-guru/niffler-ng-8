package guru.qa.niffler.db.dao.impl.spring_jdbc.mapper;

import guru.qa.niffler.api.model.Authority;
import guru.qa.niffler.db.entity.auth.AuthAuthorityEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthAuthorityEntityRowMapper implements RowMapper<AuthAuthorityEntity> {

    public static final AuthAuthorityEntityRowMapper INSTANCE = new AuthAuthorityEntityRowMapper();

    private AuthAuthorityEntityRowMapper() {
    }

    @Override
    public @Nonnull AuthAuthorityEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        return new AuthAuthorityEntity()
                .setId(rs.getObject("id", UUID.class))
                .setAuthority(Authority.valueOf(rs.getString("authority")));
    }

}
