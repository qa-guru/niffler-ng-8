package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.UdUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.FriendShipId;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.userdataJdbcUrl();

    private final UdUserDao udUserDao = new UdUserDaoJdbc();

    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setString(5, user.getFullname());
            ps.setBytes(6, user.getPhoto());
            ps.setBytes(7, user.getPhotoSmall());

            ps.executeUpdate();

            final UUID generatedUserId;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedUserId = rs.getObject("id", UUID.class);
                } else {
                    throw new IllegalStateException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedUserId);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT u.*, f.requester_id, f.addressee_id, f.created_date, f.status " +
                        "FROM public.user u " +
                        "LEFT JOIN public.friendship f ON u.id = f.requester_id OR u.id = f.addressee_id " +
                        "WHERE u.id = ?")) {
            ps.setObject(1, id);

            ps.execute();

            UserEntity user = null;
            Map<FriendShipId, FriendshipEntity> friendships = new HashMap<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    if (user == null) {
                        user = new UserEntity();
                        user.setId(rs.getObject("id", UUID.class));
                        user.setUsername(rs.getString("username"));
                        user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                        user.setFirstname(rs.getString("firstname"));
                        user.setSurname(rs.getString("surname"));
                        user.setPhoto(rs.getBytes("photo"));
                        user.setPhotoSmall(rs.getBytes("photo_small"));
                        user.setFriendshipRequests(new ArrayList<>());
                        user.setFriendshipAddressees(new ArrayList<>());
                    }

                    UUID requesterId = rs.getObject("requester_id", UUID.class);
                    UUID addresseeId = rs.getObject("addressee_id", UUID.class);

                    if (requesterId != null && addresseeId != null) {
                        FriendShipId friendshipId = new FriendShipId();
                        friendshipId.setRequester(requesterId);
                        friendshipId.setAddressee(addresseeId);

                        FriendshipEntity friendship = new FriendshipEntity();
                        friendship.setCreatedDate(rs.getDate("created_date"));
                        friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));

                        UserEntity requester = new UserEntity();
                        requester.setId(requesterId);
                        UserEntity addressee = new UserEntity();
                        addressee.setId(addresseeId);

                        friendship.setRequester(requester);
                        friendship.setAddressee(addressee);

                        friendships.put(friendshipId, friendship);

                        if (user.getId().equals(requesterId)) {
                            user.getFriendshipRequests().add(friendship);
                        }
                        if (user.getId().equals(addresseeId)) {
                            user.getFriendshipAddressees().add(friendship);
                        }
                    }
                }

                return Optional.ofNullable(user);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return udUserDao.findByUsername(username);
    }

    @Override
    public UserEntity update(UserEntity user) {
        return udUserDao.update(user);
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date)" +
                        " VALUES (?, ?, ?, ?)")) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setObject(3, FriendshipStatus.PENDING.name());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date)" +
                        " VALUES (?, ?, ?, ?)")) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setObject(3, FriendshipStatus.ACCEPTED.name());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.addBatch();

            ps.setObject(1, addressee.getId());
            ps.setObject(2, requester.getId());
            ps.setObject(3, FriendshipStatus.ACCEPTED.name());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.addBatch();

            ps.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(UserEntity user) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                     "DELETE FROM public.friendship WHERE requester_id = ? OR addressee_id = ?")) {

            ps.setObject(1, user.getId());
            ps.setObject(2, user.getId());
            ps.executeUpdate();

            udUserDao.delete(user);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
