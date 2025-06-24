package guru.qa.niffler.db.tpl;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class Connections {

    private Connections() {
    }

    private static final Map<String, JdbcConnectionHolder> holders = new ConcurrentHashMap<>();

    public static @Nonnull JdbcConnectionHolder holder(String jdbcUrl) {
        return holders.computeIfAbsent(
                jdbcUrl,
                key -> new JdbcConnectionHolder(DataSources.dataSource(jdbcUrl))
        );
    }

    public static @Nonnull JdbcConnectionHolders holders(String... jdbcUrl) {
        List<JdbcConnectionHolder> holders = Arrays.stream(jdbcUrl)
                .map(Connections::holder)
                .collect(Collectors.toList());
        return new JdbcConnectionHolders(holders);
    }


    public static void closeAllConnections() {
        holders.values().forEach(JdbcConnectionHolder::closeAllConnections);
    }

}
