package guru.qa.niffler.jupiter.extention;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TextMethodContextExtension implements
        BeforeEachCallback, AfterEachCallback {

    private static final ThreadLocal<ExtensionContext> extensionContextThreadLocal = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        extensionContextThreadLocal.set(context);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        extensionContextThreadLocal.remove();
    }

    public static ExtensionContext context() {
       return extensionContextThreadLocal.get();
    }
}
