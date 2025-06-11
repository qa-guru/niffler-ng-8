package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.spend.SpendsTable;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
    private final SelenideElement historyBox = $("#spendings");
    private final SelenideElement statisticsBox = $("#stat");
    private final SelenideElement contextMenuInAvatarBtn = $("button[aria-label='Menu']");
    private final ElementsCollection contextMenuElements = $$(".MuiList-padding li");

    @Getter
    private final SearchField searchField = new SearchField(driver);

    @Getter
    private final StatComponent statComponent = new StatComponent(driver);

    @Getter
    private final SpendsTable spendTable = new SpendsTable(driver);

    public MainPage(@Nullable SelenideDriver driver){
        super(driver);
    }

    public MainPage(){
        super();
    }

    @Override
    public String getUrl() {
        return "main";
    }

    @Step("Assert main components")
    public MainPage assertMainComponents(){
        historyBox.shouldBe(visible, Duration.ofSeconds(10));
        statisticsBox.shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }
}
