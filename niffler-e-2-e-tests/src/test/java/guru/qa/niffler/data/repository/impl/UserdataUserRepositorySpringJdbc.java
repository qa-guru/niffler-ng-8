package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.extractor.UserdataEntityResultSetExtractor;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.userdataJdbcUrl();

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "SELECT u.*, f.requester_id, f.addressee_id, f.status, f.created_date " +
                                "FROM \"user\" u " +
                                "LEFT JOIN friendship f ON u.id = f.requester_id OR u.id = f.addressee_id " +
                                "WHERE u.id = ?",
                        UserdataEntityResultSetExtractor.instance,
                        id
                )
        );
    }
    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        jdbcTemplate.update(
                "INSERT INTO friendship (requester_id, addressee_id, status) VALUES (?, ?, ?)",
                requester.getId(),
                addressee.getId(),
                FriendshipStatus.PENDING.name()
        );
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        jdbcTemplate.update(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date)" +
                        " VALUES (?, ?, ?, ?)",
                requester.getId(),
                addressee.getId(),
                FriendshipStatus.ACCEPTED.name(),
                new java.sql.Date(System.currentTimeMillis())
        );

        jdbcTemplate.update(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date)" +
                        " VALUES (?, ?, ?, ?)",
                addressee.getId(),
                requester.getId(),
                FriendshipStatus.ACCEPTED.name(),
                new java.sql.Date(System.currentTimeMillis())
        );
    }
}
