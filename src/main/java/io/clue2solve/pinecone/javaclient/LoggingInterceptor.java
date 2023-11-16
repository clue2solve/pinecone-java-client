package io.clue2solve.pinecone.javaclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        Headers headers = request.headers().newBuilder()
            .removeAll("Api-Key") // remove the Api-Key header
            .build();
        logger.info("Sending request {} on {}\n{}", request.url(), chain.connection(), headers);

        String requestBody = null;
        if (request.body() != null) {
            Buffer requestBuffer = new Buffer();
            request.body().writeTo(requestBuffer);
            requestBody = requestBuffer.readUtf8();
            logger.info(requestBody);
        }

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Headers responseHeaders = response.headers().newBuilder()
            .removeAll("Api-Key") // remove the Api-Key header
            .build();
        logger.info("Received response for {} in {:.1f}ms\n{}", response.request().url(), (t2 - t1) / 1e6, responseHeaders);

        MediaType contentType = response.body().contentType();
        String content = response.body().string(); // Only read once
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        String prettyContent = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(content, Object.class));
        logger.info("Response body: {}", prettyContent);

        ResponseBody wrappedBody = ResponseBody.create(contentType, content);
        //noinspection KotlinInternalInJava
        return response.newBuilder().body(wrappedBody).build();
    }
}
