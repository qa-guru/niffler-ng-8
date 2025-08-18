package guru.qa.niffler.api.core;

import guru.qa.niffler.jupiter.extensions.ApiLoginExtension;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class CodeInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        final Response response = chain.proceed(request);
        if(response.isRedirect()){
            String location = response.header("Location", response.request().url().toString());
            if (location.contains("code=")) {
                ApiLoginExtension.setCode(StringUtils.substringAfter(location, "code="));
            }
        }
        return response;
    }
}
