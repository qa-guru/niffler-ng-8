package guru.qa.niffler.jupiter.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestMethodContextExtension implements BeforeEachCallback, AfterEachCallback {

    private static final ThreadLocal<ExtensionContext> store = new ThreadLocal<>();

    @Override
    public void beforeEach(final ExtensionContext context) {
        store.set(context);
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        store.remove();
    }

    public static ExtensionContext context(){
        return store.get();
    }
}
