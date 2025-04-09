package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;


public class CategoryExtension implements BeforeEachCallback, ParameterResolver,AfterEachCallback {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendDbClient spendDbClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .filter(anno -> anno.categories().length > 0)
                .ifPresent(anno -> {
                    CategoryJson categoryJson =
                            new CategoryJson(
                                    null,
                                    RandomDataUtils.randomCategoryName(),
                                    anno.username(),
                                    anno.categories()[0].archived()
                            );

                    CategoryJson created = spendDbClient.createCategory(categoryJson);

                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .filter(anno -> anno.categories().length > 0)
                .ifPresent(anno -> {
                    CategoryJson categoryJson = context.getStore(NAMESPACE)
                            .get(context.getUniqueId()
                                    , CategoryJson.class);
                    if(categoryJson!=null) {
                        spendDbClient.deleteCategory(categoryJson);
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
