package guru.qa.niffler.db.repository.mapper;

import guru.qa.niffler.api.model.CurrencyValues;
import guru.qa.niffler.db.entity.userdata.UserdataUserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserdataUserEntityRowMapper {

    public static final UserdataUserEntityRowMapper INSTANCE = new UserdataUserEntityRowMapper();

    private UserdataUserEntityRowMapper() {
    }

    public UserdataUserEntity mapRow(ResultSet rs) throws SQLException {
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
