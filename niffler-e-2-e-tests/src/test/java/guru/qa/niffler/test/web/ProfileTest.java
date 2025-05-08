package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;

import java.awt.image.BufferedImage;

@WebTest
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @User
    @ScreenShotTest(value = "img/expected-avatar.png")
    void checkProfileImageTest(UserJson user, BufferedImage expectedProfileImage)  {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user)
                .goToProfile()
                .uploadAvatar("img/avatar.png")
                .checkAvatar(expectedProfileImage);
    }
}
