package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.core.TradeSafeCookieStore;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class CookieStoreExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        TradeSafeCookieStore.INSTANCE.removeAll();
    }
}