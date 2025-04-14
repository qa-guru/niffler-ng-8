package guru.qa.niffler.db.dao.impl.spring_jdbc;

import guru.qa.niffler.db.dao.UserdataUserDao;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoSpringJdbc implements UserdataUserDao {

    public static final UserdataUserEntityRowMapper USEDATA_USER_ROW_MAPPER = UserdataUserEntityRowMapper.INSTANCE;
    private final JdbcTemplate jdbcTemplate;

    public UserdataUserDaoSpringJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public UserdataUserEntity createUserdata(UserdataUserEntity entity) {
        String sql = "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, USEDATA_USER_ROW_MAPPER,
                entity.getUsername(), entity.getCurrency().name(), entity.getFirstname(),
                entity.getSurname(), entity.getFullname(), entity.getPhoto(), entity.getPhotoSmall()
        );
    }

    @Override
    public Optional<UserdataUserEntity> findUserdataById(UUID id) {
        String sql = "SELECT * FROM \"user\" WHERE id = ?";
        UserdataUserEntity entity = jdbcTemplate.queryForObject(sql, USEDATA_USER_ROW_MAPPER, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<UserdataUserEntity> findUserdataByUsername(String username) {
        String sql = "SELECT * FROM \"user\" WHERE username = ?";
        UserdataUserEntity entity = jdbcTemplate.queryForObject(sql, USEDATA_USER_ROW_MAPPER, username);
        return Optional.ofNullable(entity);
    }

    @Override
    public boolean deleteUserdata(UserdataUserEntity entity) {
        String sql = "DELETE FROM \"user\" WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, entity.getId());
        return rowsAffected > 0;
    }

    @Override
    public List<UserdataUserEntity> findAllUserdata() {
        String sql = "SELECT * FROM \"user\"";
        return jdbcTemplate.query(sql, USEDATA_USER_ROW_MAPPER);
    }
}
