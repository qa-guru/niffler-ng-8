package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.authJdbcUrl();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);

        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, user.getAuthorities().get(i).getUser().getId());
                        ps.setString(2, user.getAuthorities().get(i).getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return user.getAuthorities().size();
                    }
                }
        );

        return user;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        AuthUserEntity updatedUser = authUserDao.update(user);

        List<AuthorityEntity> existingAuthorities = authAuthorityDao.findByUserId(user.getId());
        existingAuthorities.forEach(authAuthorityDao::delete);

        if (!user.getAuthorities().isEmpty()) {
            user.getAuthorities().forEach(a -> a.setUser(updatedUser));
            authAuthorityDao.create(user.getAuthorities().toArray(new AuthorityEntity[0]));
        }
        return updatedUser;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.query(
                            "SELECT a.id AS authority_id, a.authority, a.user_id, " +
                                    "u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired " +
                                    "FROM \"user\" u JOIN public.authority a ON u.id = a.user_id " +
                                    "WHERE u.id = ?",
                            AuthUserEntityResultSetExtractor.instance,
                            id
                    )
            );
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        Optional<AuthUserEntity> user = authUserDao.findByUsername(username);
        user.ifPresent(u ->
                u.setAuthorities(authAuthorityDao.findByUserId(u.getId()))
        );
        return user;
    }

    @Override
    public void remove(AuthUserEntity user) {
        List<AuthorityEntity> existingAuthorities = authAuthorityDao.findByUserId(user.getId());
        existingAuthorities.forEach(authAuthorityDao::delete);
        authUserDao.delete(user);
    }
}
