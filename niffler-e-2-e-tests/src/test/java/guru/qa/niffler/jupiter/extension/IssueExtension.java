package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.ApiClients;
import guru.qa.niffler.api.GhEndpointClient;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.SearchOption;


public class IssueExtension implements ExecutionCondition {

  private static final GhEndpointClient GH_ENDPOINT_CLIENT = ApiClients.ghClient();

  @SneakyThrows
  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {

    return AnnotationSupport.findAnnotation(
        context.getTestMethod(),
        DisabledByIssue.class
    ).or(
        () -> AnnotationSupport.findAnnotation(
            context.getRequiredTestClass(),
            DisabledByIssue.class,
            SearchOption.INCLUDE_ENCLOSING_CLASSES
        )
    ).map(
        byIssue ->
            "open".equals(GH_ENDPOINT_CLIENT.getIssueState(byIssue.value()))
                  ? ConditionEvaluationResult.disabled("Disabled by issue #" + byIssue.value())
                  : ConditionEvaluationResult.enabled("Issue closed")
    ).orElseGet(
        () -> ConditionEvaluationResult.enabled("Annotation @DisabledByIssue not found")
    );
  }
}
