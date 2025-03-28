package guru.qa.niffler.page;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Pages {
    REGISTER("register",RegistrationPage.class),
    LOGIN("login",LoginPage.class),
    MAIN("main",MainPage.class),
    PROFILE("profile",ProfilePage.class),
    FRIENDS("people/friends",null),
    ALL_PEOPLE("people/all",null),
    ADD_SPENDING("spending",null),
    EDIT_SPENDING("spending/%s",EditSpendingPage.class);

    private String url;
    private Class<?> className;

    public static String getUrlByClass(Class<?> clazz) {
        for (Pages page : values()) {
            if (page.className != null && page.className.equals(clazz)) {
                return page.url;
            }
        }
        throw new IllegalArgumentException("Не найден URL для класса: " + clazz.getSimpleName());
    }
}
