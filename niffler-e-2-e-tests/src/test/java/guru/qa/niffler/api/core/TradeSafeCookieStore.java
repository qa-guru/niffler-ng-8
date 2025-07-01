package guru.qa.niffler.api.core;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public enum TradeSafeCookieStore implements CookieStore {

    INSTANCE;

    private final ThreadLocal<CookieStore> tradeSafeCookieStore = ThreadLocal.withInitial(this::inMemoryCookieStore);

    private CookieStore inMemoryCookieStore() {
        return new CookieManager().getCookieStore();
    }

    private CookieStore getStore() {
        return tradeSafeCookieStore.get();
    }

    public String cookieValue(String name) {
        return getCookies().stream()
            .filter(c -> c.getName().equals(name))
            .map(HttpCookie::getValue)
            .findFirst()
            .orElseThrow();
    }

    public String xsrfValue() {
        return cookieValue("X-XSRF-TOKEN");
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        getStore().add(uri, cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return getStore().get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return getStore().getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return getStore().getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return getStore().remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return getStore().removeAll();
    }
}
