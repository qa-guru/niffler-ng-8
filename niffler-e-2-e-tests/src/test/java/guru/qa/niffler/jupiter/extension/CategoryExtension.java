package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.SpendService;
import guru.qa.niffler.api.SpendServiceClient;
import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.web.model.User;
import guru.qa.niffler.retrofit.TestResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.function.Supplier;

public class CategoryExtension implements ParameterResolver, AfterEachCallback {

    public static final SpendServiceClient SPEND_CLIENT = SpendService.client();
    private static final Faker FAKER = Faker.instance();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), Category.class)
                && parameterContext.getParameter().getType() == CategoryJson.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Category categoryAnno = AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), Category.class).get();

        User user = extensionContext.getStore(UseUserResolver.NAMESPACE).get(extensionContext.getUniqueId(), User.class);
        User categoryUser = UseUserResolver.resolve(categoryAnno.user());
        user = chooseUser(categoryUser, user);

        CategoryJson categoryJson = new CategoryJson(
                null,
                FAKER.app().name(),
                user.username(),
                false
        );

        CategoryJson createdCategory = doHttpCall(() -> SPEND_CLIENT.addCategory(categoryJson));
        createdCategory = archiveCategoryIfNeeded(createdCategory, categoryAnno.archived());
        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), createdCategory);
        return createdCategory;
    }

    private User chooseUser(User categoryUser, User user) {
        if (user == null && categoryUser == null) {
            throw new IllegalStateException("Не указан пользователь для которого будет создаваться категория: " +
                    "\n@UseUser \nили \n@Category(user=@UseUser)");
        }
        if (categoryUser != null) {
            user = categoryUser;
        }
        return user;
    }

    @Override
    public void afterEach(ExtensionContext context) {
        CategoryJson createdCategory = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        archiveCategoryIfNeeded(createdCategory, !createdCategory.archived());
    }

    private CategoryJson archiveCategoryIfNeeded(CategoryJson createdCategory, boolean isArchived) {
        if (isArchived) {
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
