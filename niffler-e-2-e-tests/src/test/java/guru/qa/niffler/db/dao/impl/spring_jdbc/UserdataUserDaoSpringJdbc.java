package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.UserdataUserDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoSpringJdbc extends AbstractSpringDao<UserdataUserEntity> implements UserdataUserDao {

    public UserdataUserDaoSpringJdbc(String jdbcUrl) {
        super(jdbcUrl, UserdataUserEntityRowMapper.INSTANCE);
    }

    @Override
    public UserdataUserEntity create(UserdataUserEntity entity) {
        String sql = "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, rowMapper,
                entity.getUsername(), entity.getCurrency().name(), entity.getFirstname(),
                entity.getSurname(), entity.getFullname(), entity.getPhoto(), entity.getPhotoSmall()
        );
    }

    @Override
    public Optional<UserdataUserEntity> findById(UUID id) {
        String sql = "SELECT * FROM \"user\" WHERE id = ?";
        UserdataUserEntity entity = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<UserdataUserEntity> findByUsername(String username) {
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
    public List<UserdataUserEntity> findAll() {
        String sql = "SELECT * FROM \"user\"";
        return jdbcTemplate.query(sql, rowMapper);
    }
}
