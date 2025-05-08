package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.openqa.selenium.Keys;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MainPage extends BasePage {

    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement historyBox = $("#spendings");
    private final SelenideElement statisticsBox = $("#stat");
    private final SelenideElement searchInput = $("input");
    private final SelenideElement statImg = $("canvas[role='img']");
    private final SelenideElement profileImage = $(".MuiAvatar-img");
    private final ElementsCollection statisticCells = $$("#legend-container li");
    private final SelenideElement deleteBtn = $("#delete");
    private final SelenideElement dialogWindow = $("div[role='dialog']");
    private final SelenideElement contextMenuInAvatarBtn = $("button[aria-label='Menu']");
    private final ElementsCollection contextMenuElements = $$(".MuiList-padding li");

    @Step("Изменить spending с description {0}")
    public EditSpendingPage editSpending(String spendingDescription) {
        searchInput.setValue(spendingDescription).pressEnter();
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public EditSpendingPage editSpending(UserJson user, int i) {
        return editSpending(user.testData().spendings().get(i).description());
    }

    @Step("Удалить spending с description {0}")
    public MainPage deleteSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(0)
                .click();
        deleteBtn.click();
        dialogWindow.$(byText("Delete")).click();
        return new MainPage();
    }

    @Step("Check that table contains description {0}")
    public void checkThatTableContains(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
    }

    @Step("Assert main components")
    public MainPage assertMainComponents(){
        historyBox.shouldBe(visible, Duration.ofSeconds(10));
        statisticsBox.shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    @SneakyThrows
    @Step("Check statistic diagram")
    public MainPage checkStatisticDiagram(BufferedImage expected) {
        Selenide.sleep(3000);
        BufferedImage actual = ImageIO.read(Objects.requireNonNull(statImg.screenshot()));
        assertFalse(new ScreenDiffResult(expected, actual));
        return this;
    }

    @Step("Check statistic cell")
    public MainPage checkStatisticDiagramInfo(List<String> spendInfo) {
        for (String info : spendInfo){
            statisticCells.findBy(text(info)).shouldBe(visible);
        }
        return this;
    }


    public MainPage checkStatisticDiagramInfo(Map<CategoryJson, Double> info) {
        List<String> spendInfo = new ArrayList<>();
        info.forEach((key,value)->{
            String amount = value == (long) value.doubleValue()
                    ? String.format("%d", (long) value.doubleValue())
                    : String.valueOf(value);
            String spendEntry = String.format("%s %s ₽",key.name(),amount);
            spendInfo.add(spendEntry);
        });
        return checkStatisticDiagramInfo(spendInfo);
    }

    public MainPage checkStatisticDiagramInfoUnEditable(UserJson user) {
        List<SpendJson> spends = user.testData().spendings();

        double archivedSum = spends.stream()
                .filter(spend -> spend.category().archived())
                .mapToDouble(SpendJson::amount)
                .sum();

        Map<CategoryJson, Double> categorySums = spends.stream()
                .filter(spend -> !spend.category().archived())
                .collect(Collectors.groupingBy(
                        SpendJson::category,
                        Collectors.summingDouble(SpendJson::amount)
                ));

        Map<CategoryJson, Double> result = new HashMap<>();
        if(archivedSum > 0) {
            result.put(CategoryJson.createArchived(), archivedSum);
        }
        result.putAll(categorySums);


        return checkStatisticDiagramInfo(result);
    }

    @Step("Go to Profile")
    public ProfilePage goToProfile() {
        contextMenuInAvatarBtn.click();
        contextMenuElements.find(text("Profile")).click();
        return new ProfilePage();
    }

    @Step("Go to Friends")
    public FriendsPage goToFriendsList() {
        contextMenuInAvatarBtn.click();
        contextMenuElements.find(text("Friends")).click();
        return new FriendsPage();
    }

    @Step("Go to All People")
    public AllPeoplePage goToAllPeopleList() {
        contextMenuInAvatarBtn.click();
        contextMenuElements.find(text("All People")).click();
        return new AllPeoplePage();
    }

    public MainPage search(String spend) {
        searchInput.sendKeys(spend);
        searchInput.sendKeys(Keys.ENTER);
        return this;
    }

}
