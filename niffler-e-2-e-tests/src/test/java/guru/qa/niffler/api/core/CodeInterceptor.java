package guru.qa.niffler.api.core;

import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class CodeInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.isRedirect()) {
            String location = Objects.requireNonNull(response.header("Location"));
            if (location.contains("code=")) {
                String code = HttpUrl.parse(location).queryParameter("code");
                ApiLoginExtension.setCode(code);
            }
        }
        return response;
    }
}
