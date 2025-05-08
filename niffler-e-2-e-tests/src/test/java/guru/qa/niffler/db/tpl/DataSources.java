package guru.qa.niffler.db.tpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.commons.lang3.StringUtils;

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

    public static DataSource dataSource(String jdbcUrl) {
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
                    try {
                        InitialContext context = new InitialContext();
                        context.bind("java:comp/env/jdbc/" + uniqId, atomikosDs);
                    } catch (NamingException e) {
                        throw new RuntimeException(e);
                    }
                    return atomikosDs;
                }
        );
    }

}
