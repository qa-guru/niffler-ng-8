package guru.qa.niffler.db.dao.impl.spring_jdbc.mapper;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserdataUserEntityRowMapper implements RowMapper<UserdataUserEntity> {

    public static final UserdataUserEntityRowMapper INSTANCE = new UserdataUserEntityRowMapper();

    private UserdataUserEntityRowMapper() {
    }

    @Override
    public UserdataUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserdataUserEntity()
                .setId(rs.getObject("id", UUID.class))
                .setUsername(rs.getString("username"))
                .setCurrency(CurrencyValues.valueOf(rs.getString("currency")))
                .setFirstname(rs.getString("firstname"))
                .setSurname(rs.getString("surname"))
                .setFullname(rs.getString("full_name"))
                .setPhoto(rs.getBytes("photo"))
                .setPhotoSmall(rs.getBytes("photo_small"));
    }

}
