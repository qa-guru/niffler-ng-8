package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.entity.currency.CurrencyValues;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class UdUserDaoJdbc implements UdUserDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.userdataJdbcUrl();
    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO \"user\" (username, currency, full_name, firstname, surname, photo, photo_small) " +
                        "VALUES ( ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFullname());
            ps.setString(4, user.getFirstname());
            ps.setString(5, user.getSurname());
            ps.setBytes(6, user.getPhoto());
            ps.setBytes(7, user.getPhotoSmall());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity update(UserEntity user) {
        try (PreparedStatement userPs = holder(url).connection().prepareStatement(
                """
                        UPDATE "user"
                            SET currency = ?,
                            full_name    = ?,
                            firstname    = ?,
                            surname      = ?,
                            photo        = ?,
                            photo_small  = ?
                            WHERE id = ?
                        """);
             PreparedStatement friendsPs = holder(url).connection().prepareStatement(
                     """
                             INSERT INTO friendship (requester_id, addressee_id, status)
                              VALUES (?, ?, ?)
                              ON CONFLICT (requester_id, addressee_id)
                                DO UPDATE SET status = ?
                             """
             )
        ){
            userPs.setString(1, user.getCurrency().name());
            userPs.setString(2, user.getFullname());
            userPs.setString(3, user.getFirstname());
            userPs.setString(4, user.getSurname());
            userPs.setBytes(5, user.getPhoto());
            userPs.setBytes(6, user.getPhotoSmall());
            userPs.setObject(7, user.getId());
            userPs.executeUpdate();

            List<FriendshipEntity> allFriendships = new ArrayList<>();
            for (FriendshipEntity fe : user.getFriendshipRequests()) {
                allFriendships.add(fe);
            }
            for (FriendshipEntity fe : user.getFriendshipAddressees()) {
                allFriendships.add(fe);
            }

            for (FriendshipEntity fe : allFriendships){
                friendsPs.setObject(1, fe.getRequester().getId());
                friendsPs.setObject(2, fe.getAddressee().getId());
                friendsPs.setObject(3, fe.getStatus().name());
                friendsPs.setObject(4, fe.getStatus().name());
                friendsPs.addBatch();
                friendsPs.clearParameters();
            }

            friendsPs.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
            try (PreparedStatement ps = holder(url).connection().prepareStatement(
                    "SELECT * FROM user WHERE id = ?"
            )) {
                ps.setObject(1, id);
                ps.execute();
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        UserEntity ue = new UserEntity();
                        ue.setId(rs.getObject("id", UUID.class));
                        ue.setUsername(rs.getString("username"));
                        ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                        ue.setFullname(rs.getString("full_name"));
                        ue.setFirstname(rs.getString("firstname"));
                        ue.setSurname(rs.getString("surname"));
                        ue.setPhoto(rs.getBytes("photo"));
                        ue.setPhotoSmall(rs.getBytes("photo_small"));
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
    public Optional<UserEntity> findByUsername(String username) {
            try (PreparedStatement ps = holder(url).connection().prepareStatement(
                    "SELECT * FROM user WHERE username = ?"
            )) {
                ps.setString(1, username);
                ps.execute();
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        UserEntity ue = new UserEntity();
                        ue.setId(rs.getObject("id", UUID.class));
                        ue.setUsername(rs.getString("username"));
                        ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                        ue.setFullname(rs.getString("full_name"));
                        ue.setFirstname(rs.getString("firstname"));
                        ue.setSurname(rs.getString("surname"));
                        ue.setPhoto(rs.getBytes("photo"));
                        ue.setPhotoSmall(rs.getBytes("photo_small"));
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
    public void delete(UserEntity user) {
        try (
                Connection conn = holder(url).connection();
                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM \"user\" WHERE id = ?"
                );
                PreparedStatement friendsPs = conn.prepareStatement(
                        "DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?"
                )
        ) {
            friendsPs.setObject(1, user.getId());
            friendsPs.setObject(2, user.getId());
            friendsPs.executeUpdate();

            ps.setObject(1, user.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<UserEntity> findAll() {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM user"
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                List<UserEntity> list = new ArrayList<>();
                while (rs.next()) {
                    UserEntity ue = new UserEntity();
                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUsername(rs.getString("username"));
                    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    ue.setFullname(rs.getString("full_name"));
                    ue.setFirstname(rs.getString("firstname"));
                    ue.setSurname(rs.getString("surname"));
                    ue.setPhoto(rs.getBytes("photo"));
                    ue.setPhotoSmall(rs.getBytes("photo_small"));
                    list.add(ue);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
