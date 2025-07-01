package guru.qa.niffler.jupiter.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestMethodContextExtension implements BeforeEachCallback, AfterEachCallback {

    public static final ThreadLocal<ExtensionContext> STORE = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        STORE.set(context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        STORE.remove();
    }

    public static ExtensionContext context() {
        return STORE.get();
    }
}
