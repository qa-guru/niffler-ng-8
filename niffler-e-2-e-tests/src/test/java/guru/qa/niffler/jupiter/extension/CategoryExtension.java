package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.api.model.UserParts;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.db.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static guru.qa.niffler.util.RandomDataUtils.genCategoryName;

public class CategoryExtension implements ParameterResolver, BeforeEachCallback, AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendClient spendDbClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        UserParts user = UserExtension.createdUser();
        List<Category> categories = getCategories(extensionContext);
        if (!categories.isEmpty()) {
            List<CategoryJson> createdCategories = new ArrayList<>();

            for (Category category : categories) {
                CategoryJson categoryJson = new CategoryJson(
                    null,
                    category.name().isEmpty() ? genCategoryName() : category.name(),
                    user.getUsername(),
                    category.archived()
                );
                CategoryJson createdCategory = spendDbClient.createCategory(categoryJson);
                createdCategories.add(createdCategory);
                user.getTestData().getCategories().add(createdCategory);
            }
            extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), createdCategories);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        Type parameterizedType = parameter.getParameterizedType();
        boolean isListOfCategory =
            type == List.class
                && parameterizedType instanceof ParameterizedType
                && ((ParameterizedType) parameterizedType).getActualTypeArguments()[0] == CategoryJson.class;
        return !getCategories(extensionContext).isEmpty() && (type == CategoryJson.class || isListOfCategory);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        List<CategoryJson> createdCategories = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class);
        Class<?> paramType = parameterContext.getParameter().getType();
        if (paramType == CategoryJson.class) {
            return createdCategories.get(0);
        } else if (paramType == List.class) {
            return createdCategories;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        List<CategoryJson> createdCategories = context.getStore(NAMESPACE).get(context.getUniqueId(), List.class);
        if (createdCategories != null) {
            createdCategories.forEach(spendDbClient::deleteCategory);
        }
    }

    private List<Category> getCategories(ExtensionContext extensionContext) {
        return AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), User.class)
            .filter(user -> user.categories().length > 0)
            .map(user -> Arrays.asList(user.categories()))
            .orElse(Collections.emptyList());
    }

}