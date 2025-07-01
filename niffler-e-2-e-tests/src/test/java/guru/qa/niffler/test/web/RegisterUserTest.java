package guru.qa.niffler.test.web;


import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.extensions.BrowserExtension;
import guru.qa.niffler.model.users.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegisterUserTest extends BaseUITest {


    //Проверено с UserApiClient
    @Test
    void testRegisterUser() {
        UserJson user = RandomDataUtils.generateUser();
        userClient.createUser(user);

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user);
    }
}
