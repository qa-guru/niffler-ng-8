package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.UserdataUserDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserdataUserDaoSpringJdbc extends AbstractSpringDao<UserdataUserEntity> implements UserdataUserDao {

    public UserdataUserDaoSpringJdbc(String jdbcUrl) {
        super(jdbcUrl, UserdataUserEntityRowMapper.INSTANCE);
    }

    public UserdataUserDaoSpringJdbc(DataSource dataSource) {
        super(dataSource, UserdataUserEntityRowMapper.INSTANCE);
    }

    @Override
    public @Nonnull UserdataUserEntity create(UserdataUserEntity entity) {
        String sql = "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, rowMapper,
            entity.getUsername(), entity.getCurrency().name(), entity.getFirstname(),
            entity.getSurname(), entity.getFullname(), entity.getPhoto(), entity.getPhotoSmall()
        );
    }

    @Override
    public @Nonnull UserdataUserEntity update(UserdataUserEntity entity) {
        String sql = "UPDATE \"user\" SET username = ?, currency = ?, firstname = ?, surname = ?, full_name = ?, photo = ?, photo_small = ? " +
            "WHERE id = ? RETURNING *";
        return jdbcTemplate.queryForObject(sql, rowMapper,
            entity.getUsername(), entity.getCurrency().name(), entity.getFirstname(), entity.getSurname(),
            entity.getFullname(), entity.getPhoto(), entity.getPhotoSmall(), entity.getId()
        );
    }

    @Override
    public @Nonnull Optional<UserdataUserEntity> findById(UUID id) {
        String sql = "SELECT * FROM \"user\" WHERE id = ?";
        UserdataUserEntity entity = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public @Nonnull Optional<UserdataUserEntity> findByUsername(String username) {
        String sql = "SELECT * FROM \"user\" WHERE username = ?";
        UserdataUserEntity entity = jdbcTemplate.queryForObject(sql, rowMapper, username);
        return Optional.ofNullable(entity);
    }

    @Override
    public boolean delete(UserdataUserEntity entity) {
        String sql = "DELETE FROM \"user\" WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, entity.getId());
        return rowsAffected > 0;
    }

    @Override
    public @Nullable List<UserdataUserEntity> findAll() {
        String sql = "SELECT * FROM \"user\"";
        return jdbcTemplate.query(sql, rowMapper);
    }

}
