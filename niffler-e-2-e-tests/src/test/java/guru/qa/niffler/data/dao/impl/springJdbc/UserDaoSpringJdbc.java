package guru.qa.niffler.data.dao.impl.springJdbc;

import guru.qa.niffler.data.dao.interfaces.UserDao;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDaoSpringJdbc implements UserDao {

    private DataSource dataSource;

    public UserDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into \"user\" (username, currency, firstname, surname, photo, photo_small, full_name)" +
                            " values (?, ? , ? , ? , ? , ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullName());

            return ps;
        }, keyHolder);
        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID uuid) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        UdUserEntityRowMapper.INSTANCE,
                        uuid
                )
        );
    }

    @Override
    public List<UserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                UdUserEntityRowMapper.INSTANCE
        );
    }
}