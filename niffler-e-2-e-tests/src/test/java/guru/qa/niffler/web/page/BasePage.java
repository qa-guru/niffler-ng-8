package guru.qa.niffler.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePage {

    protected <T> T initElement(SelenideElement element, Class<T> tClass) {
        try {
            Constructor<T> constructor = tClass.getConstructor(SelenideElement.class);
            return constructor.newInstance(element);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> List<T> initElements(ElementsCollection elements, Class<T> tClass) {
        List<T> result = new ArrayList<>();
        for (SelenideElement element : elements) {
            T t = initElement(element, tClass);
            result.add(t);
        }
        return result;
    }

}