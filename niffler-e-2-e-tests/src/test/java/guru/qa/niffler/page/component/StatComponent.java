package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.StatConditions.color;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

    public StatComponent() {
        super($("#stat"));
    }

    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
    private final SelenideElement chart = $("canvas[role='img']");
    @Step("Check that statistic image matches the expected image")
    @Nonnull
    public StatComponent checkStatisticImage(BufferedImage expectedImage) throws IOException {
        Selenide.sleep(2000);
        assertFalse(
                new ScreenDiffResult(
                        chartScreenshot(),
                        expectedImage
                ),
                ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
        );
        return this;
    }

    @Step("Get screenshot of stat chart")
    @Nonnull
    public BufferedImage chartScreenshot() throws IOException {
        return ImageIO.read(requireNonNull(chart.screenshot()));
    }

    @Step("Check that stat bubbles contains colors {expectedColors}")
    @Nonnull
    public StatComponent checkBubbles(Color... expectedColors) {
        bubbles.should(color(expectedColors));
        return this;
    }

    @Step("Check statistic cell")
    public StatComponent checkStatisticDiagramInfo(List<String> spendInfo) {
        for (String info : spendInfo){
            bubbles.findBy(text(info)).shouldBe(visible);
        }
        return this;
    }


    public StatComponent checkStatisticDiagramInfo(Map<CategoryJson, Double> info) {
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

    public StatComponent checkStatisticDiagramInfoUnEditable(UserJson user) {
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
}