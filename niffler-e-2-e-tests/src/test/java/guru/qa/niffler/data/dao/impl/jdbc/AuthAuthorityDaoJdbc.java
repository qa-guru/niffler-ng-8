package guru.qa.niffler.data.dao.impl.jdbc;

//Закомментировано, поскольку в уроке 5.1 произошёл переход на Spring Jdbc
//  и метод AuthAuthorityDao.create стал принимать AuthAuthorityEntity...

import guru.qa.niffler.data.dao.interfaces.AuthAuthorityDao;
import guru.qa.niffler.data.entity.user.AuthAuthorityEntity;
import guru.qa.niffler.data.enums.AuthorityRoles;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthAuthorityEntity create(AuthAuthorityEntity... authorities) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority) " +
                        "VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            for (AuthAuthorityEntity authority : authorities) {
                ps.setObject(1, authority.getUserId());
                ps.setString(2, authority.getRole().name());
                ps.addBatch();
            }

            ps.executeBatch();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            authorities[0].setId(generatedKey);
            return authorities[0];
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        List<AuthAuthorityEntity> categories = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM authority")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuthAuthorityEntity ae = new AuthAuthorityEntity();
                    ae.setId(rs.getObject("id", UUID.class));
                    ae.setUserId(rs.getObject("user_id", UUID.class));
                    ae.setRole(AuthorityRoles.valueOf(rs.getString("authority")));
                    categories.add(ae);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }
}
