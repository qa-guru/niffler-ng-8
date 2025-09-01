package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.api.AuthApiClient;
import guru.qa.niffler.jupiter.annotations.ApiLogin;
import guru.qa.niffler.jupiter.annotations.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OAuthTest extends BaseUITest {

    String actualLogin = CFG.mainUserLogin();
    String actualPass = CFG.mainUserPass();


    @Test
    @ApiLogin(username = "test",password = "12345")
    void oauthTest() {
        Selenide.open(CFG.frontUrl());
    }
}
