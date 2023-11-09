package io.clue2solve.pinecone.javaclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

import java.io.IOException;

/**
 * Interceptor to log the request and response
 */
public class LoggingInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger("okhttp3.OkHttpClient");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        logger.info("Sending request {} on {}\n{}", request.url(), chain.connection(), request.headers());

        String requestBody = null;
        if (request.body() != null) {
            Buffer requestBuffer = new Buffer();
            request.body().writeTo(requestBuffer);
            requestBody = requestBuffer.readUtf8();
            logger.info(requestBody);
        }

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        logger.info("Received response for {} in {:.1f}ms\n{}", response.request().url(), (t2 - t1) / 1e6, response.headers());

        MediaType contentType = response.body().contentType();
        String content = response.body().string(); // Only read once
        logger.info("Response body: {}", content);

        ResponseBody wrappedBody = ResponseBody.create(contentType, content);
        return response.newBuilder().body(wrappedBody).build();
    }
}
