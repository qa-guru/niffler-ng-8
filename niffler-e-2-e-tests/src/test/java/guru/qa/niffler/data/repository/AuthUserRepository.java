package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.dao.DataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {

    AuthUserEntity create(AuthUserEntity authUserEntity);

    Optional<AuthUserEntity> findById(UUID uuid);

    List<AuthUserEntity> findAll();

    AuthUserEntity extractData(ResultSet resultSet) throws SQLException, DataAccessException;
}