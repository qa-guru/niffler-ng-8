package guru.qa.niffler.db.dao.impl.jdbc;

import guru.qa.niffler.db.dao.CategoryDao;
import guru.qa.niffler.db.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class CategoryDaoJdbc extends AbstractDao<CategoryEntity> implements CategoryDao {

    public CategoryDaoJdbc(String jdbcUrl) {
        super(jdbcUrl);
    }

    @Override
    public @Nonnull CategoryEntity create(CategoryEntity entity) {
        String sql = "INSERT INTO category (username, name, archived) VALUES(?, ?, ?) RETURNING *";
        return executeQuery(sql, entity.getUsername(), entity.getName(), entity.isArchived());
    }

    @Override
    public @Nonnull CategoryEntity update(CategoryEntity entity) {
        String sql = "UPDATE category SET username = ?, name = ?, archived = ? WHERE id = ? RETURNING *";
        return executeQuery(sql, entity.getUsername(), entity.getName(), entity.isArchived(), entity.getId());
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findById(UUID id) {
        String sql = "SELECT * FROM category WHERE id = ?";
        return executeQueryToOptional(sql, id);
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findByNameAndUsername(String name, String username) {
        String sql = "SELECT * FROM category WHERE name = ? and username = ?";
        return executeQueryToOptional(sql, name, username);
    }

    @Override
    public @Nonnull List<CategoryEntity> findAllByUsername(String username) {
        String sql = "SELECT * FROM category WHERE username = ?";
        return executeQueryToList(sql, username);
    }

    @Override
    public boolean delete(CategoryEntity entity) {
        String sql = "DELETE FROM category WHERE id = ?";
        return executeUpdateToBoolean(sql, entity.getId());
    }

    @Override
    public @Nonnull List<CategoryEntity> findAll() {
        String sql = "SELECT * FROM category";
        return executeQueryToList(sql);
    }

    @Override
    protected @Nonnull CategoryEntity mapResultSet(ResultSet rs) throws SQLException {
        return new CategoryEntity()
                .setId(rs.getObject("id", UUID.class))
                .setName(rs.getString("name"))
                .setUsername(rs.getString("username"))
                .setArchived(rs.getBoolean("archived"));
    }

}