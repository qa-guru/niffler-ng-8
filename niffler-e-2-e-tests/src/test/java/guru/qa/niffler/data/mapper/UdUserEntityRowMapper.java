package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UdUserEntityRowMapper implements RowMapper<UserEntity> {

    public static final UdUserEntityRowMapper INSTANCE = new UdUserEntityRowMapper();

    private UdUserEntityRowMapper() {
    }

    @Override
    public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserEntity user = new UserEntity();
        user.setId(rs.getObject("id", UUID.class));
        user.setUsername(rs.getString("username"));
        user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        user.setFirstName(rs.getString("first_name"));
        user.setSurname(rs.getString("surname"));
        user.setPhoto(rs.getBytes("photo"));
        user.setPhotoSmall(rs.getBytes("photo_small"));
        user.setFullName(rs.getString("full_name"));
        return user;
    }
}