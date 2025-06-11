package guru.qa.niffler.api.core;

import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

public class CodeInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        final Response response = chain.proceed(chain.request());
        if(response.isRedirect()){
            String location = Objects.requireNonNull(
                    response.header("Location")
            );
            final String separator = "code=";
            if(location.contains(separator)) {
                ApiLoginExtension.setCode(
                        StringUtils.substringAfter(
                                location, separator
                        )
                );
            }
        }
        return response;
    }
}
