package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.interfaces.UserDao;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UdUserDAOJdbc implements UserDao {

    private static final Config CFG = Config.getInstance();

    private final Connection connection;

    public UdUserDAOJdbc(Connection connection) {
        this.connection = connection;
    }


    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"user\" (username,currency,firstname,surname,photo,photo_small,full_name)" +
                        "VALUES (?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, String.valueOf(user.getCurrency()));
            ps.setString(3, user.getFirstName());

            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullName());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else throw new SQLException("Can't find id in ResultSet");
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM user WHERE id=?")) {
            ps.setObject(1, uuid);
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    UserEntity ue = new UserEntity();

                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUsername(rs.getString("username"));
                    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    ue.setSurname(rs.getString("surname"));
                    ue.setPhoto(rs.getBytes("photo"));
                    ue.setPhotoSmall(rs.getBytes("photo_small"));
                    ue.setFullName(rs.getString("full_name"));
                    return Optional.of(ue);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserEntity> findAll() {
        return List.of();
    }

//    @Override
//    public List<UserEntity> findByUsername(String username) {
//        List<UserEntity> users = new ArrayList<>();
//        try (PreparedStatement ps = connection.prepareStatement(
//                "SELECT * FROM user WHERE username=?")) {
//            ps.setString(1, username);
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    UserEntity ue = new UserEntity();
//                    ue.setId(rs.getObject("id", UUID.class));
//                    ue.setUsername(rs.getString("username"));
//                    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
//                    ue.setSurname(rs.getString("surname"));
//                    ue.setPhoto(rs.getBytes("photo"));
//                    ue.setPhotoSmall(rs.getBytes("photo_small"));
//                    ue.setFullName(rs.getString("full_name"));
//                    users.add(ue);
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return users;
//    }
//
//    @Override
//    public void deleteUser(UserEntity user) {
//        try (PreparedStatement ps = connection.prepareStatement(
//                "DELETE FROM user WHERE id=?")) {
//            ps.setObject(1, user.getId());
//            ps.execute();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}