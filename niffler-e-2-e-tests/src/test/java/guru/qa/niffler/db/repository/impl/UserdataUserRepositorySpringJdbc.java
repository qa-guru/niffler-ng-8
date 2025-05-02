package guru.qa.niffler.db.repository.impl;

import guru.qa.niffler.db.dao.impl.spring_jdbc.mapper.UserdataFriendshipEntityRowMapper;
import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.db.repository.UserdataUserRepository;
import guru.qa.niffler.db.repository.mapper.UserdataUserEntityListResultSetExtractor;
import guru.qa.niffler.db.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

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
    private final RowMapper<UserdataFriendshipEntity> friendshipRowMapper;
    private final ResultSetExtractor<List<UserdataUserEntity>> userListResultSetExtractor;

    public UserdataUserRepositorySpringJdbc(String jdbcUrl) {
        this.jdbcTemplate = new JdbcTemplate(DataSources.dataSource(jdbcUrl));
        this.friendshipRowMapper = UserdataFriendshipEntityRowMapper.INSTANCE;
        this.userListResultSetExtractor = UserdataUserEntityListResultSetExtractor.INSTANCE;
    }

    @Override
    public UserdataUserEntity create(UserdataUserEntity entity) {
        String sql = "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?) RETURNING *";
        return jdbcTemplate.query(sql, userListResultSetExtractor,
                        entity.getUsername(), entity.getCurrency().name(), entity.getFirstname(),
                        entity.getSurname(), entity.getFullname(), entity.getPhoto(), entity.getPhotoSmall()
                ).stream()
                .findFirst().get();
    }

    @Override
    public Optional<UserdataUserEntity> findById(UUID id) {
        String sql = SELECT_ALL + " WHERE u.id = ?";
        return jdbcTemplate.query(sql, userListResultSetExtractor, id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<UserdataUserEntity> findByUsername(String username) {
        String sql = SELECT_ALL + " WHERE u.username = ?";
        return jdbcTemplate.query(sql, userListResultSetExtractor, username)
                .stream()
                .findFirst();
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
    public List<UserdataUserEntity> findAll() {
        return jdbcTemplate.query(SELECT_ALL, userListResultSetExtractor);
    }

    @Override
    public UserdataFriendshipEntity createFriendship(UserdataUserEntity requester, UserdataUserEntity addressee, FriendshipStatus status) {
        String sql = "INSERT INTO friendship (requester_id, addressee_id, created_date, status)  VALUES (?, ?, NOW(), ?) RETURNING *";
        return jdbcTemplate.queryForObject(sql, friendshipRowMapper, requester.getId(), addressee.getId(), status.name());
    }

}
