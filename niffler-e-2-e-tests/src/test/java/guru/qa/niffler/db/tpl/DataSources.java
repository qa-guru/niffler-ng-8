package guru.qa.niffler.db.tpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.p6spy.engine.spy.P6DataSource;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DataSources {

    public DataSources() {
    }

    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public static @Nonnull DataSource dataSource(@Nonnull String jdbcUrl) {
        return dataSources.computeIfAbsent(
                jdbcUrl,
                url -> {
                    AtomikosDataSourceBean atomikosDs = new AtomikosDataSourceBean();
                    String uniqId = StringUtils.substringAfter(jdbcUrl, "5432/");
                    atomikosDs.setUniqueResourceName(uniqId);
                    atomikosDs.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                    Properties props = new Properties();
                    props.put("URL", jdbcUrl);
                    props.put("user", "postgres");
                    props.put("password", "secret");
                    atomikosDs.setXaProperties(props);
                    atomikosDs.setMaxPoolSize(12);
                    atomikosDs.setPoolSize(3);
                    P6DataSource p6DataSource = new P6DataSource(atomikosDs);
                    try {
                        InitialContext context = new InitialContext();
                        context.bind("java:comp/env/jdbc/" + uniqId, p6DataSource);
                    } catch (NamingException e) {
                        throw new RuntimeException(e);
                    }
                    return p6DataSource;
                }
        );
    }

}
