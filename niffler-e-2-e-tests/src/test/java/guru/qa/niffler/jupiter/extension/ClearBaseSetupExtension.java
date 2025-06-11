package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class ClearBaseSetupExtension implements SuiteExtension {

    private static final Config CFG = Config.getInstance();
    private final JdbcTemplate authJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    private final JdbcTemplate spendJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    private final JdbcTemplate userdataJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

    private final JdbcTransactionTemplate authTxTemplate = new JdbcTransactionTemplate(CFG.authJdbcUrl());
    private final JdbcTransactionTemplate udTxTemplate = new JdbcTransactionTemplate(CFG.userdataJdbcUrl());
    private final JdbcTransactionTemplate spendTxTemplate = new JdbcTransactionTemplate(CFG.spendJdbcUrl());

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl(),
            CFG.userdataJdbcUrl(),
            CFG.authJdbcUrl()
    );

    @Override
    public void beforeSuite(ExtensionContext context) {
        backupDatabaseTables();
        clearDatabaseTables();
    }

    @Override
    public void afterSuite() {
        restoreDatabaseTables();
        dropBackupTables();
    }



    @SuppressWarnings("unchecked")
    private void backupDatabaseTables() {
        xaTransactionTemplate.execute(()-> {
            spendTxTemplate.execute(() -> {
                spendJdbcTemplate.execute("CREATE TEMP TABLE category_backup AS SELECT * FROM category;");
                spendJdbcTemplate.execute("CREATE TEMP TABLE spend_backup AS SELECT * FROM spend;");
                return null;
            });

            udTxTemplate.execute(() -> {
                userdataJdbcTemplate.execute("CREATE TEMP TABLE friendship_backup AS SELECT * FROM friendship;");
                userdataJdbcTemplate.execute("CREATE TEMP TABLE userdata_user_backup AS SELECT * FROM \"user\";");
                return null;
            });

            authTxTemplate.execute(() -> {
                authJdbcTemplate.execute("CREATE TEMP TABLE authority_backup AS SELECT * FROM authority;");
                authJdbcTemplate.execute("CREATE TEMP TABLE auth_user_backup AS SELECT * FROM \"user\";");
                return null;
            });
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    private void clearDatabaseTables() {
        xaTransactionTemplate.execute(()-> {
            spendJdbcTemplate.execute("TRUNCATE TABLE category, spend CASCADE;");
            userdataJdbcTemplate.execute("TRUNCATE TABLE friendship, \"user\" CASCADE;");
            authJdbcTemplate.execute("TRUNCATE TABLE authority, \"user\" CASCADE;");
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    private void restoreDatabaseTables() {
        xaTransactionTemplate.execute(()-> {
            spendTxTemplate.execute(() -> {
                spendJdbcTemplate.execute("INSERT INTO category SELECT * FROM category_backup;");
                spendJdbcTemplate.execute("INSERT INTO spend SELECT * FROM spend_backup;");
                return null;
            });

            udTxTemplate.execute(() ->{
                userdataJdbcTemplate.execute("INSERT INTO friendship SELECT * FROM friendship_backup;");
                userdataJdbcTemplate.execute("INSERT INTO \"user\" SELECT * FROM userdata_user_backup;");
                return null;
            });

            authTxTemplate.execute(()->{
                authJdbcTemplate.execute("INSERT INTO authority SELECT * FROM authority_backup;");
                authJdbcTemplate.execute("INSERT INTO \"user\" SELECT * FROM auth_user_backup;");
                return null;
            });
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    private void dropBackupTables() {
        xaTransactionTemplate.execute(()-> {
            spendTxTemplate.execute(() -> {
                spendJdbcTemplate.execute("DROP TABLE IF EXISTS category_backup;");
                spendJdbcTemplate.execute("DROP TABLE IF EXISTS spend_backup;");
                return null;
            });

            udTxTemplate.execute(() ->{
                userdataJdbcTemplate.execute("DROP TABLE IF EXISTS friendship_backup;");
                userdataJdbcTemplate.execute("DROP TABLE IF EXISTS userdata_user_backup;");
                    return null;
            });

            authTxTemplate.execute(()->{
                authJdbcTemplate.execute("DROP TABLE IF EXISTS authority_backup;");
                authJdbcTemplate.execute("DROP TABLE IF EXISTS auth_user_backup;");
                return null;
            });
            return null;
        });
    }
}