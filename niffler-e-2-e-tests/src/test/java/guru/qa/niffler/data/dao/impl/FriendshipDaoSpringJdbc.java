package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.FriendshipDao;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class FriendshipDaoSpringJdbc implements FriendshipDao {

    private static final Config CFG = Config.getInstance();

    private final JdbcTemplate jdbcTemplate =
            new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

    private static final RowMapper<FriendshipEntity> friendshipRowMapper = (rs, rowNum) -> {
        FriendshipEntity f = new FriendshipEntity();
        f.setId(rs.getObject("id", UUID.class));
        f.setUserId(rs.getObject("user_id", UUID.class));
        f.setFriendId(rs.getObject("friend_id", UUID.class));
        f.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
        f.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return f;
    };

    @Override
    public void create(FriendshipEntity... friendships) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, friendships[i].getUserId());
                        ps.setObject(2, friendships[i].getFriendId());
                        ps.setString(3, friendships[i].getStatus().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return friendships.length;
                    }
                }
        );
    }

    @Override
    public List<FriendshipEntity> findAllByUserId(UUID userId) {
        return jdbcTemplate.query(
                "SELECT * FROM friendship WHERE user_id = ?",
                friendshipRowMapper,
                userId
        );
    }

    @Override
    public List<FriendshipEntity> findAllByFriendId(UUID friendId) {
        return jdbcTemplate.query(
                "SELECT * FROM friendship WHERE friend_id = ?",
                friendshipRowMapper,
                friendId
        );
    }

    @Override
    public void updateStatus(UUID id, FriendshipStatus status) {
        jdbcTemplate.update(
                "UPDATE friendship SET status = ? WHERE id = ?",
                status.name(),
                id
        );
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM friendship");
    }
}