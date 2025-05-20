package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NonStaticBrowserExtension implements
    BeforeEachCallback,
    AfterEachCallback,
    TestExecutionExceptionHandler,
    LifecycleMethodExecutionExceptionHandler {

  private static final ThreadLocal<Set<SelenideDriver>> driversList = new ThreadLocal<>();

  public static void add(SelenideDriver driver) {
    if (driver != null) {
      driversList.get().add(driver);
    }
  }

  public static Set<SelenideDriver> getDrivers() {
      return driversList.get();
  }

  public static Optional<SelenideDriver> find(Driver driverToFind) {
    if (driverToFind == null) {
      return Optional.empty();
    }

      return driversList.get().stream()
            .filter(Objects::nonNull)
            .filter(sd -> Objects.equals(
                    sd.driver().getWebDriver(),
                    driverToFind.getWebDriver()
            ))
            .findFirst();
  }

  @Override
  public void afterEach(ExtensionContext context) {
    Set<SelenideDriver> drivers = driversList.get();
    if (drivers != null) {
      drivers.stream()
              .filter(Objects::nonNull)
              .filter(SelenideDriver::hasWebDriverStarted)
              .forEach(SelenideDriver::close);
    }
    driversList.remove();
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    driversList.set(ConcurrentHashMap.newKeySet());
    SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
        .savePageSource(false)
        .screenshots(false)
    );
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  @Override
  public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  @Override
  public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  private static void doScreenshot() {
    Set<SelenideDriver> drivers = driversList.get();
    if (drivers != null) {
      drivers.stream()
              .filter(Objects::nonNull)
              .filter(SelenideDriver::hasWebDriverStarted)
              .filter(d -> d.getWebDriver() instanceof TakesScreenshot)
              .forEach(driver -> {
                byte[] screenshot = ((TakesScreenshot) driver.getWebDriver())
                        .getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screen on fail", new ByteArrayInputStream(screenshot));
              });
    }
  }
}
