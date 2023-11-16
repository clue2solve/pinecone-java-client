package io.clue2solve.pinecone.javaclient.utils;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okio.Buffer;

import java.io.IOException;

/**
 * Interceptor to log the request and response
 */
public class OkHttpLoggingInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger("okhttp3.OkHttpClient");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Headers headers = request.headers().newBuilder()
            // .removeAll("Api-Key") // remove the Api-Key header
            .set("Api-Key", "SNIP") // replace the Api-Key header value with "SNIP"
            .build();
        logger.info("Sending request: {}\n{}", request.url(),  headers);

        String requestBody = null;
        if (request.body() != null) {
            Buffer requestBuffer = new Buffer();
            request.body().writeTo(requestBuffer);
            requestBody = requestBuffer.readUtf8();
            logger.info("Request Body:\n------\n{}\n------",JsonUtils.toPrettyFormat(requestBody));
        }

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Headers responseHeaders = response.headers().newBuilder()
            .removeAll("Api-Key") // remove the Api-Key header
            .build();
        logger.info("Received response for {} in {}ms\n{}", response.request().url(), (t2 - t1)/1000000, responseHeaders);

        MediaType contentType = response.body().contentType();
        String content = response.body().string(); // Only read once

        logger.info("Response body:\n------\n{}\n------", JsonUtils.toPrettyFormat(content));

        ResponseBody wrappedBody = ResponseBody.create(contentType, content);
        //noinspection KotlinInternalInJava
        return response.newBuilder().body(wrappedBody).build();
    }
}
