package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UdUserDaoJdbc implements UdUserDao {

  private final Connection connection;

  public UdUserDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public UserEntity create(UserEntity user) {
    try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO \"user\" (username, currency) VALUES (?, ?)",
        PreparedStatement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
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
    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM \"user\" WHERE id = ? ")) {
      ps.setObject(1, id);

      ps.execute();
      ResultSet rs = ps.getResultSet();

      if (rs.next()) {
        UserEntity result = new UserEntity();
        result.setId(rs.getObject("id", UUID.class));
        result.setUsername(rs.getString("username"));
        result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        result.setFirstname(rs.getString("firstname"));
        result.setSurname(rs.getString("surname"));
        result.setPhoto(rs.getBytes("photo"));
        result.setPhotoSmall(rs.getBytes("photo_small"));
        return Optional.of(result);
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<UserEntity> findAll() {
    List<UserEntity> ueList = new ArrayList<>();
    try (PreparedStatement ps = connection.prepareStatement(
            "SELECT * FROM \"user\""
    )) {
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          UserEntity result = new UserEntity();
          result.setId(rs.getObject("id", UUID.class));
          result.setUsername(rs.getString("username"));
          result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          result.setFirstname(rs.getString("firstname"));
          result.setSurname(rs.getString("surname"));
          result.setPhoto(rs.getBytes("photo"));
          result.setPhotoSmall(rs.getBytes("photo_small"));
          ueList.add(result);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find users", e);
    }
    return ueList;
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    try (PreparedStatement ps = connection.prepareStatement(
            "SELECT * FROM \"user\" WHERE username = ?"
    )) {
      ps.setObject(1, username);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          UserEntity ue = new UserEntity();
          ue.setId(rs.getObject("id", UUID.class));
          ue.setUsername(rs.getString("username"));
          ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          ue.setFirstname(rs.getString("firstname"));
          ue.setSurname(rs.getString("surname"));
          ue.setPhoto(rs.getBytes("photo"));
          ue.setPhotoSmall(rs.getBytes("photo_small"));
          ue.setFullname(rs.getString("full_name"));
          return Optional.of(ue);
        } else {
          return Optional.empty();
        }

      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find user with username: " + username, e);
    }
  }

  @Override
  public void delete(UserEntity user) {
    try (PreparedStatement ps = connection.prepareStatement(
            "DELETE FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, user.getId());
      ps.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException("Error deleting user with Id:  " + user.getId(), e);
    }
  }
}
