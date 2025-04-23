package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.Authority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

  private final Connection connection;

  public AuthAuthorityDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void create(AuthorityEntity... authority) {
    try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)",
        PreparedStatement.RETURN_GENERATED_KEYS)) {
      for (AuthorityEntity a : authority) {
        ps.setObject(1, a.getUserId());
        ps.setString(2, a.getAuthority().name());
        ps.addBatch();
        ps.clearParameters();
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

    @Override
    public AuthorityEntity create(AuthorityEntity authority) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"authority\" (user_id, authority)" +
                        "VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setObject(1, authority.getUserId());
            ps.setObject(2, authority.getAuthority().name());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            authority.setId(generatedKey);
            return authority;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthorityEntity> findById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"authority\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthorityEntity authority = new AuthorityEntity();
                    authority.setId(rs.getObject("id", UUID.class));
                    authority.setUserId(rs.getObject("user_id", UUID.class));
                    authority.setAuthority(
                            Authority.valueOf(
                                    rs.getString("authority")
                            )
                    );
                    return Optional.of(authority);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find authority with Id: " + id, e);
        }
    }

    @Override
    public List<AuthorityEntity> findAll() {

      List<AuthorityEntity> authorityList = new ArrayList<>();

      try(PreparedStatement ps = connection.prepareStatement(
              "SELECT * FROM \"authority\""
      )) {
          ps.execute();
          try (ResultSet rs = ps.getResultSet()) {

              while (rs.next()) {
                  AuthorityEntity authority = new AuthorityEntity();
                  authority.setId(rs.getObject("id", UUID.class));
                  authority.setUserId(rs.getObject("user_id", UUID.class));
                  authority.setAuthority(
                          Authority.valueOf(
                                  rs.getString("authority")
                          )
                  );
                  authorityList.add(authority);
              }
          }
      } catch (SQLException e) {
          throw new RuntimeException(e);
      }
        return authorityList;
    }

    @Override
    public List<AuthorityEntity> findByUserId(UUID userId) {
        List<AuthorityEntity> authorityList = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"authority\" WHERE user_id = ?"
        )) {
            ps.setObject(1, userId);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthorityEntity authority = new AuthorityEntity();
                    authority.setId(rs.getObject("id", UUID.class));
                    authority.setUserId(rs.getObject("user_id", UUID.class));
                    authority.setAuthority(
                            Authority.valueOf(
                                    rs.getString("authority")
                            )
                    );
                    authorityList.add(authority);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find authority with userId: " + userId, e);
        }
        return authorityList;
    }

    @Override
    public void delete(AuthorityEntity authority) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM \"authority\" WHERE id = ?"
        )) {
            ps.setObject(1, authority.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting authority with Id:  " + authority.getId(), e);
        }
    }
}
