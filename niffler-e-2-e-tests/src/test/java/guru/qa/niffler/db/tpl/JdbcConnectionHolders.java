package guru.qa.niffler.db.tpl;

import javax.annotation.Nonnull;
import java.util.List;

public class JdbcConnectionHolders implements AutoCloseable {

    private final List<JdbcConnectionHolder> holders;

    public JdbcConnectionHolders(@Nonnull List<JdbcConnectionHolder> holders) {
        this.holders = holders;
    }

    @Override
    public void close() {
        holders.forEach(JdbcConnectionHolder::close);
    }

}
