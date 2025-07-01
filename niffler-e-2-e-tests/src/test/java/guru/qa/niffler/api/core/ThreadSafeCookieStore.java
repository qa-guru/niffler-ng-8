package guru.qa.niffler.api.core;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public enum ThreadSafeCookieStore implements CookieStore {

    INSTANCE;

    private final ThreadLocal<CookieStore> threadLocal = ThreadLocal.withInitial(
            this::InMemoryCookieStore
    );

    private CookieStore getCookieStore() {
        return threadLocal.get();
    }

    private CookieStore InMemoryCookieStore() {
        return new CookieManager().getCookieStore();
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        getCookieStore().add(uri, cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return getCookieStore().get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return getCookieStore().getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return getCookieStore().getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return getCookieStore().remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return getCookieStore().removeAll();
    }

    public String cookieValue(String name) {
        return getCookies().stream().filter(c -> c.getName().equals(name))
                .map(HttpCookie::getValue).findFirst().orElseThrow();
    }
}
