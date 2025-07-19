package guru.qa.niffler.db.repository.impl.spring_jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.UserdataFriendshipEntityRowMapper;
import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.db.repository.UserdataUserRepository;
import guru.qa.niffler.db.repository.mapper.UserdataUserEntityListResultSetExtractor;
import guru.qa.niffler.db.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("DataFlowIssue")
@ParametersAreNonnullByDefault
public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    protected static final String JDBC_URL = Config.getInstance().userdataJdbcUrl();

    private static final String SELECT_ALL = """
        SELECT
            u.*,
            f.requester_id,
            f.addressee_id,
            f.created_date,
            f.status,
            r.id AS requester_id,
            r.username AS requester_username,
            r.currency AS requester_currency,
            r.firstname AS requester_firstname,
            r.surname AS requester_surname,
            r.full_name AS requester_full_name,
            r.photo AS requester_photo,
            r.full_name AS requester_photo_small,
            a.id AS addressee_id,
            a.username AS addressee_username,
            a.currency AS addressee_currency,
            a.firstname AS addressee_firstname,
            a.surname AS addressee_surname,
            a.full_name AS addressee_full_name,
            a.photo AS addressee_photo,
            a.full_name AS addressee_photo_small
        FROM "user" u
        LEFT JOIN friendship f ON f.requester_id = u.id OR f.addressee_id = u.id
        LEFT JOIN "user" r ON r.id = f.requester_id
        LEFT JOIN "user" a ON a.id = f.addressee_id
        """;

    private final JdbcTemplate jdbcTemplate;
    private final ResultSetExtractor<List<UserdataUserEntity>> userListResultSetExtractor;

    public UserdataUserRepositorySpringJdbc() {
        this.jdbcTemplate = new JdbcTemplate(DataSources.dataSource(JDBC_URL));
        this.userListResultSetExtractor = UserdataUserEntityListResultSetExtractor.INSTANCE;
    }

    @Override
    public @Nonnull UserdataUserEntity create(UserdataUserEntity entity) {
        String sql = "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, UserdataUserEntityRowMapper.INSTANCE,
            entity.getUsername(), entity.getCurrency().name(), entity.getFirstname(),
            entity.getSurname(), entity.getFullname(), entity.getPhoto(), entity.getPhotoSmall()
        );
    }

    @Override
    public @Nonnull Optional<UserdataUserEntity> findById(UUID id) {
        String sql = SELECT_ALL + " WHERE u.id = ?";
        return jdbcTemplate.query(sql, userListResultSetExtractor, id)
            .stream()
            .findFirst();
    }

    @Override
    public @Nonnull Optional<UserdataUserEntity> findByUsername(String username) {
        String sql = SELECT_ALL + " WHERE u.username = ?";
        return jdbcTemplate.query(sql, userListResultSetExtractor, username)
            .stream()
            .findFirst();
    }

    @Override
    public @Nonnull UserdataUserEntity update(UserdataUserEntity entity) {
        String userSql = """
                UPDATE "user" SET username = ?, currency = ?, firstname = ?, surname = ?, full_name = ?, photo = ?, photo_small = ?
                WHERE id = ?
            """;
        jdbcTemplate.update(userSql,
            entity.getUsername(), entity.getCurrency().name(), entity.getFirstname(), entity.getSurname(),
            entity.getFullname(), entity.getPhoto(), entity.getPhotoSmall(), entity.getId()
        );
        updateFriendships(entity.getFriendshipRequests());
        updateFriendships(entity.getFriendshipAddressees());

        return findById(entity.getId()).orElseThrow();
    }

    public void updateFriendships(List<UserdataFriendshipEntity> friendships) {
        String sql = """
                UPDATE friendship
                SET status = ?, createdDate = ?
                WHERE requester_id = ? AND addressee_id = ?
            """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                UserdataFriendshipEntity friendship = friendships.get(i);
                ps.setString(1, friendship.getStatus().name());
                ps.setDate(2, new Date(friendship.getCreatedDate().getTime()));
                ps.setObject(3, friendship.getRequester().getId());
                ps.setObject(3, friendship.getAddressee().getId());
            }

            @Override
            public int getBatchSize() {
                return friendships.size();
            }
        });
    }

    public List<UserdataFriendshipEntity> selectFriendships(@Nullable UUID requesterId,
                                                            @Nullable UUID addresseeId,
                                                            @Nullable FriendshipStatus status) {
        String sql = "SELECT * FROM friendship";
        StringJoiner sj = new StringJoiner(" AND ", " WHERE ", "");
        ArrayList<Object> params = new ArrayList<>();
        if (requesterId != null) {
            sj.add("requester_id = ?");
            params.add(requesterId);
        }
        if (addresseeId != null) {
            sj.add("addressee_id = ?");
            params.add(addresseeId);
        }
        if (status != null) {
            sj.add("status = ?");
            params.add(status);
        }
        sql = sql + sj;
        return jdbcTemplate.query(sql, UserdataFriendshipEntityRowMapper.INSTANCE, params.toArray());
    }

    @Override
    public boolean delete(UserdataUserEntity entity) {
        UUID userId = entity.getId();
        String friendshipDeleteSql = "DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?";
        jdbcTemplate.update(friendshipDeleteSql, userId, userId);

        String userDeleteSql = "DELETE FROM \"user\" WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(userDeleteSql, userId);
        return rowsAffected > 0;
    }

    @Override
    public @Nullable List<UserdataUserEntity> findAll() {
        return jdbcTemplate.query(SELECT_ALL, userListResultSetExtractor);
    }

    @Override
    public @Nonnull UserdataFriendshipEntity createFriendship(UserdataUserEntity requester,
                                                              UserdataUserEntity addressee,
                                                              FriendshipStatus status) {
        String sql = "INSERT INTO friendship (requester_id, addressee_id, created_date, status)  VALUES (?, ?, NOW(), ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, UserdataFriendshipEntityRowMapper.INSTANCE,
            requester.getId(), addressee.getId(), status.name()
        );
    }

}
