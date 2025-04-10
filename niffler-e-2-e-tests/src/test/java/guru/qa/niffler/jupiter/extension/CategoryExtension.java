package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.model.CategoryJson;
import guru.qa.niffler.db.service.SpendDbClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.web.model.WebUser;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

import static guru.qa.niffler.util.RandomDataUtils.genCategoryName;

public class CategoryExtension implements ParameterResolver, AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendDbClient spendDbClient = new SpendDbClient();

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
                categoryAnno.archived()
        );

        CategoryJson createdCategory = spendDbClient.createCategory(categoryJson);
        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), createdCategory);
        return createdCategory;
    }

    @Override
    public void afterEach(ExtensionContext context) {
        CategoryJson createdCategory = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        if (createdCategory != null) {
            spendDbClient.deleteCategory(createdCategory);
        }
    }

    private Optional<Category> getCategory(ExtensionContext extensionContext) {
        return AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), User.class)
                .filter(user -> user.categories().length > 0)
                .map(user -> user.categories()[0]);
    }

}