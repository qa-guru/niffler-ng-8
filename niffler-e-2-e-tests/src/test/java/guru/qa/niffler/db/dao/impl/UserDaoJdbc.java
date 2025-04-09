package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.db.dao.AbstractDao;
import guru.qa.niffler.db.dao.UserDao;
import guru.qa.niffler.db.entity.userdata.UserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserDaoJdbc extends AbstractDao<UserEntity> implements UserDao {

    public UserDaoJdbc() {
        super(CFG.userdataJdbcUrl());
    }

    @Override
    public UserEntity createUser(UserEntity entity) {
        String sql = "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?) RETURNING *";
        return executeQuery(sql, entity.getUsername(), entity.getCurrency().name(), entity.getFirstname(),
                entity.getSurname(), entity.getFullname(), entity.getPhoto(), entity.getPhotoSmall());
    }

    @Override
    public Optional<UserEntity> findUserById(UUID id) {
        String sql = "SELECT FROM \"user\" WHERE id = ?";
        return executeQueryToOptional(sql, id);
    }

    @Override
    public Optional<UserEntity> findUserByUsername(String username) {
        String sql = "SELECT FROM \"user\" WHERE username = ?";
        return executeQueryToOptional(sql, username);
    }

    @Override
    public boolean deleteUser(UserEntity entity) {
        String sql = "DELETE FROM \"user\" WHERE id = ?";
        return executeUpdateToBoolean(sql, entity.getId());
    }

    @Override
    protected UserEntity mapResultSet(ResultSet rs) throws SQLException {
        return new UserEntity()
                .setId(rs.getObject("id", UUID.class))
                .setUsername(rs.getString("username"))
                .setCurrency(CurrencyValues.valueOf(rs.getString("currency")))
                .setFirstname(rs.getString("firstname"))
                .setSurname(rs.getString("surname"))
                .setFullname(rs.getString("full_name"))
                .setPhoto(rs.getBytes("photo"))
                .setPhotoSmall(rs.getBytes("photo_small"));
    }

}