package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendService;
import guru.qa.niffler.api.SpendServiceClient;
import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.retrofit.TestResponse;
import guru.qa.niffler.web.model.WebUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;
import java.util.function.Supplier;

import static guru.qa.niffler.util.RandomDataUtils.genCategoryName;

public class CategoryExtension implements ParameterResolver, AfterEachCallback {

    public static final SpendServiceClient SPEND_CLIENT = SpendService.client();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getCategory(extensionContext).isPresent() && parameterContext.getParameter().getType() == CategoryJson.class;
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        WebUser user = extensionContext.getStore(UserResolver.NAMESPACE).get(extensionContext.getUniqueId(), WebUser.class);
        Category categoryAnno = getCategory(extensionContext).get();

        CategoryJson categoryJson = new CategoryJson(
                null,
                genCategoryName(),
                user.username(),
                false
        );

        CategoryJson createdCategory = doHttpCall(() -> SPEND_CLIENT.addCategory(categoryJson));
        createdCategory = archiveCategoryIfNeeded(createdCategory, categoryAnno.archived());
        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), createdCategory);
        return createdCategory;
    }

    @Override
    public void afterEach(ExtensionContext context) {
        CategoryJson createdCategory = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        if (createdCategory != null) {
            archiveCategoryIfNeeded(createdCategory, !createdCategory.archived());
        }
    }

    private Optional<Category> getCategory(ExtensionContext extensionContext) {
        return AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), User.class)
                .filter(user -> user.categories().length > 0)
                .map(user -> user.categories()[0]);
    }

    private CategoryJson archiveCategoryIfNeeded(CategoryJson createdCategory, boolean needToArchive) {
        if (needToArchive) {
            CategoryJson archivedCategory = new CategoryJson(
                    createdCategory.id(),
                    createdCategory.name(),
                    createdCategory.username(),
                    true
            );
            createdCategory = doHttpCall(() -> SPEND_CLIENT.updateCategory(archivedCategory));
        }
        return createdCategory;
    }

    private CategoryJson doHttpCall(Supplier<TestResponse<CategoryJson, Void>> supplier) {
        TestResponse<CategoryJson, Void> response = supplier.get();
        if (!response.isSuccessful()) {
            Assertions.fail("Ошибка при вызове api. Ответ:\n" + response.getRetrofitRawResponse());
        }
        return response.getBody();
    }

}