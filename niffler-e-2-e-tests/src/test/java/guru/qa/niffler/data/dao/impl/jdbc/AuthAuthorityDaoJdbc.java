package guru.qa.niffler.data.dao.impl.jdbc;

//Закомментировано, поскольку в уроке 5.1 произошёл переход на Spring Jdbc
//  и метод AuthAuthorityDao.create стал принимать AuthAuthorityEntity...

public class AuthAuthorityDaoJdbc{
//        implements AuthAuthorityDao {
//
//    private final Connection connection;
//
//    public AuthAuthorityDaoJdbc(Connection connection) {
//        this.connection = connection;
//    }
//
//    @Override
//    public AuthAuthorityEntity create(AuthAuthorityEntity authority) {
//        try (PreparedStatement ps = connection.prepareStatement(
//                "INSERT INTO authority (user_id, authority) " +
//                        "VALUES (?, ?)",
//                Statement.RETURN_GENERATED_KEYS
//        )) {
//            ps.setObject(1, authority.getUserId());
//            ps.setString(2, String.valueOf(authority.getRole()));
//            ps.executeUpdate();
//
//            final UUID generatedKey;
//            try (ResultSet rs = ps.getGeneratedKeys()) {
//                if (rs.next()) {
//                    generatedKey = rs.getObject("id", UUID.class);
//                } else {
//                    throw new SQLException("Can`t find id in ResultSet");
//                }
//            }
//            authority.setId(generatedKey);
//            return authority;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
