package guru.qa.niffler.jupiter.extensions;

import guru.qa.niffler.api.UserApiClient;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class UserClientExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(UsersClient.class)) {
                field.setAccessible(true);   //Достаёт даже приватные поля

                field.set(testInstance, "api".equals(System.getProperty("client.impl"))  //Прим: -D client.impl=api
                        ? new UserDbClient()
                        : new UserApiClient());
            }
        }
    }
}
