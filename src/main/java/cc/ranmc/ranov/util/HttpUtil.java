package cc.ranmc.ranov.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.Duration;
import java.util.function.Consumer;

public class HttpUtil {

    private static final OkHttpClient client = new OkHttpClient();

    public static void get(String url, Consumer<String> callback) {
        new Thread(() -> {
            OkHttpClient c = client.newBuilder().callTimeout(Duration.ofMillis(8000)).build();
            Request request = new Request.Builder().url(url).build();
            try (Response response = c.newCall(request).execute()) {
                if (!response.isSuccessful()) callback.accept("");
                callback.accept(response.body().string());
            } catch (Exception e) {
                callback.accept("");
            }
        }).start();
    }

}
