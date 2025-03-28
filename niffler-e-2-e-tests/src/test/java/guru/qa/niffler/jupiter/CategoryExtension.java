package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;


public class CategoryExtension implements BeforeEachCallback, ParameterResolver,AfterEachCallback {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private static final String BEFORE_EACH_CALLBACK_KEY ="_beforeEachCallback";

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                    CategoryJson categoryJson =
                            new CategoryJson(
                                    null,
                                    new Faker().commerce().department(),
                                    anno.username(),
                                    false
                            );

                    List<CategoryJson> activeCategories = spendApiClient.getCategories(categoryJson.username(),true);

                    if(activeCategories.size() >= 8) {
                        for(int i = 0; i< activeCategories.size()-7; i++) {
                            archiveCategory(activeCategories.get(i));
                        }
                    }

                    CategoryJson created = spendApiClient.addCategory(categoryJson);
                    if(anno.archived()){
                        created = archiveCategory(created);
                    }
                    context.getStore(NAMESPACE).put(context.getUniqueId()+BEFORE_EACH_CALLBACK_KEY, created);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                    CategoryJson categoryJson = context.getStore(NAMESPACE)
                            .get(context.getUniqueId()+BEFORE_EACH_CALLBACK_KEY
                                    , CategoryJson.class);
                    if(isUnarchived(categoryJson)){
                        CategoryJson created = archiveCategory(categoryJson);
                        context.getStore(NAMESPACE).put(context.getUniqueId()+"_afterEachCallback", created);
                    }
                });
    }

    private CategoryJson archiveCategory(CategoryJson categoryJson){
        CategoryJson archivedCategory =  new CategoryJson(
                categoryJson.id(),
                categoryJson.name(),
                categoryJson.username(),
                true
        );
        return spendApiClient.updateCategory(archivedCategory);
    }

    private boolean isUnarchived(CategoryJson categoryJson){
        return spendApiClient.getCategories(categoryJson.username(),true)
                .stream()
               .anyMatch(c -> c.id().equals(categoryJson.id()));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId()+BEFORE_EACH_CALLBACK_KEY, CategoryJson.class);
    }
}
