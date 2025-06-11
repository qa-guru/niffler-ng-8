package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

  private final UserdataUserDao udUserDao = new UserdataUserDaoJdbc();
    private static final Config CFG = Config.getInstance();
    private final String url = CFG.userdataJdbcUrl();

  @Nonnull
  @Override
  public UserEntity create(UserEntity user) {
    return udUserDao.create(user);
  }

  @Nonnull
  @Override
  public UserEntity update(UserEntity user) {
    return udUserDao.update(user);
  }

  @Nonnull
  @Override
  public Optional<UserEntity> findById(UUID id) {
    return udUserDao.findById(id);
  }

  @Nonnull
  @Override
  public Optional<UserEntity> findByUsername(String username) {
    return udUserDao.findByUsername(username);
  }

  @Override
  public void sendInvitation(UserEntity requester, UserEntity addressee) {
    requester.addFriends(FriendshipStatus.PENDING, addressee);
      udUserDao.update(requester);
  }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
    addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
    udUserDao.update(requester);
    udUserDao.update(addressee);
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
