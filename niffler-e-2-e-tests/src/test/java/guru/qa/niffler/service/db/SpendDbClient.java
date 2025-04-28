package guru.qa.niffler.service.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;


public abstract class SpendDbClient implements SpendClient {

    protected static final Config CFG = Config.getInstance();

    protected final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    protected SpendEntity spendEntity(String username, String categoryName){
        return SpendEntity.fromJson(
                SpendJson.spendJson(username,categoryName)
        );
    }
}