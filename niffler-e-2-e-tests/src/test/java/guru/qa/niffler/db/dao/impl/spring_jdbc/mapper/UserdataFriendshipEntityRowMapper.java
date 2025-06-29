package guru.qa.niffler.db.dao.impl.spring_jdbc.mapper;

import guru.qa.niffler.db.entity.userdata.FriendshipStatus;
import guru.qa.niffler.db.entity.userdata.UserdataFriendshipEntity;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserdataFriendshipEntityRowMapper implements RowMapper<UserdataFriendshipEntity> {

    public static final UserdataFriendshipEntityRowMapper INSTANCE = new UserdataFriendshipEntityRowMapper();

    private UserdataFriendshipEntityRowMapper() {
    }

    @Override
    public @Nonnull UserdataFriendshipEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        return new UserdataFriendshipEntity()
                .setRequester(new UserdataUserEntity().setId(rs.getObject("requester_id", UUID.class)))
                .setAddressee(new UserdataUserEntity().setId(rs.getObject("addressee_id", UUID.class)))
                .setCreatedDate(rs.getTimestamp("created_date"))
                .setStatus(FriendshipStatus.valueOf(rs.getString("status")));
    }

}
