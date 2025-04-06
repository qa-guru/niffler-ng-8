package guru.qa.niffler.retrofit;

import okhttp3.ResponseBody;
import retrofit2.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TestResponseAdapterFactory extends CallAdapter.Factory {

    private TestResponseAdapterFactory() { }

    public static TestResponseAdapterFactory create() {
        return new TestResponseAdapterFactory();
    }

    @Override
    public CallAdapter<?, TestResponse<?, ?>> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != TestResponse.class) {
            return null;
        }

        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Response type must be parameterized (e.g. TestResponse<SuccessDto, ErrorDto>)");
        }
        Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
        Type successType = actualTypeArguments[0];
        Type errorType = actualTypeArguments[1];
        return new CallAdapter<>() {

            @Override
            public Type responseType() {
                return successType;
            }

            @Override
            public TestResponse<?, ?> adapt(Call<Object> call) {
                try {
                    Response<Object> response = call.execute();
                    ResponseBody errorBody = response.errorBody();
                    Object converErrorBody = null;
                    if (errorBody != null) {
                        converErrorBody = retrofit.responseBodyConverter(errorType, annotations)
                                .convert(errorBody);
                    }
                    return TestResponse.of(response, converErrorBody);
                } catch (IOException e) {
                    throw new RuntimeException("API call failed", e);
                }
            }
        };
    }

}
