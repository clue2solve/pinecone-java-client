package io.clue2solve.pinecone.javaclient;

import ch.qos.logback.classic.Logger;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;

public class LoggingInterceptor implements Interceptor {
    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger("okhttp3.OkHttpClient");
        logger.info(String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));


        Buffer requestBuffer = new Buffer();
        request.body().writeTo(requestBuffer);
        logger.info("OkHttp", requestBuffer.readUtf8());


        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long t2 = System.nanoTime();
        logger.info(String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        logger.info("response body: " + response.body().string());

        return response;
    }
}
