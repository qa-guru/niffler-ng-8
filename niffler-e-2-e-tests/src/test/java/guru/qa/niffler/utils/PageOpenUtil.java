package guru.qa.niffler.utils;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.BasePage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegistrationPage;

public class PageOpenUtil {

    private static final Config CFG = Config.getInstance();

    public static <T extends BasePage<?>> T open(Class<T> clazz){
        final String url;
        final String route;
        if (clazz.isAssignableFrom(LoginPage.class) || clazz.isAssignableFrom(RegistrationPage.class)){
            route = CFG.authUrl();
        } else {
            route = CFG.frontUrl();
        }
        try {
            url = route + clazz.getDeclaredConstructor().newInstance().getUrl();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Selenide.open(url,clazz);
    }
}
