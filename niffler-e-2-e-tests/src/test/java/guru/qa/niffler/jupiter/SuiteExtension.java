package guru.qa.niffler.jupiter;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public interface SuiteExtension extends BeforeAllCallback {

    @Override
    default void beforeAll(ExtensionContext context) throws Exception {
        final ExtensionContext rootContext = context.getRoot();
        rootContext.getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(
                        this.getClass(),
                        key -> {
                            beforeSuite(rootContext);
                            return new ExtensionContext.Store.CloseableResource() {

                                @Override
                                public void close() throws Throwable {
                                    afterSuite();
                                }
                            };
                        }
                );
    }

    default void beforeSuite(ExtensionContext context) {}

    default void afterSuite() {}
}
