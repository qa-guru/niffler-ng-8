package guru.qa.niffler.data.dao.impl.jdbc;


import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.interfaces.AuthAuthorityDao;
import guru.qa.niffler.data.entity.user.AuthAuthorityEntity;
import guru.qa.niffler.data.enums.AuthorityRoles;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthAuthorityEntity create(AuthAuthorityEntity... authorities) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
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
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement("SELECT * FROM authority")) {
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
