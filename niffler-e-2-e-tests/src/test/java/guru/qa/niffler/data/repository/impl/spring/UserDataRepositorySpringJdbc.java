package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.spring.UserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.extractor.UdUserEntityListExtractor;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;


@ParametersAreNonnullByDefault
public class UserDataRepositorySpringJdbc implements UserdataUserRepository {
    private static final Config CFG = Config.getInstance();

    private static final UdUserDao userDao = new UserDaoSpringJdbc();

    @Override
    public UserEntity create(UserEntity user) {
        return userDao.create(user);
    }

    @Override
    public UserEntity update(UserEntity user) {
        return userDao.update(user);
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return jdbcTemplate.query(
                        """
                                select * from "user" u left join friendship f 
                                on u.id = f.requester_id 
                                or u.id = f.addressee_id 
                                where u.id = ?
                                """,
                        UdUserEntityListExtractor.instance,
                        id
        )
                .stream()
                .findFirst();
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return jdbcTemplate.query(
                        """
                                select * from "user" u  left join friendship f 
                                on u.id = f.requester_id 
                                or u.id = f.addressee_id 
                                where u.username = ?
                                """,
                        UdUserEntityListExtractor.instance,
                        username
        )
                .stream()
                .findFirst();
    }

    public List<UserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return jdbcTemplate.query(
                """
                SELECT * FROM "user" u 
                LEFT JOIN friendship f ON u.id = f.requester_id OR u.id = f.addressee_id
                """,
                UdUserEntityListExtractor.instance
        );
    }

    @Override
    public void addInvitation(UserEntity requester, UserEntity addressee, FriendshipStatus status) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.batchUpdate(
                "INSERT INTO friendship (requester_id, addressee_id, status) VALUES (?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        if (i == 0) {
                            ps.setObject(1, requester.getId());
                            ps.setObject(2, addressee.getId());
                        } else {
                            ps.setObject(1, addressee.getId());
                            ps.setObject(2, requester.getId());
                        }
                        ps.setString(3, status.name());
                    }

                    @Override
                    public int getBatchSize() {
                        return status.equals(ACCEPTED)
                                ? 2
                                : 1;
                    }
                });
    }

    @Override
    public void remove(UserEntity user) {
        userDao.delete(user);
    }
}