package com.software.androidthesis.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/5/12 15:28
 * @Decription:
 */
public class HttpUtil {

    private static OkHttpClient httpClient = new OkHttpClient.Builder().build();

    public static byte[] doGet(String url, Map<String, String[]> header, Map<String, String[]> params, String expectContentType) {
        Request.Builder builder = new Request.Builder();
        addHeader(builder, header);
        addUrlParam(builder, url, params);
        return requestExec(builder.build(), expectContentType);
    }

    public static byte[] doPost(String url, Map<String, String[]> header, Map<String, String[]> body, String expectContentType) {
        Request.Builder builder = new Request.Builder().url(url);
        addHeader(builder, header);
        addBodyParam(builder, body, "POST");
        return requestExec(builder.build(), expectContentType);
    }

    private static void addHeader(Request.Builder builder, Map<String, String[]> header) {
        if (header == null) {
            return;
        }
        for (String key : header.keySet()) {
            String[] values = header.get(key);
            if (values != null) {
                for (String value : values) {
                    builder.addHeader(key, value);
                }
            }
        }
    }

    private static void addUrlParam(Request.Builder builder, String url, Map<String, String[]> params) {
        if (params == null) {
            return;
        }
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            if (values != null) {
                for (String value : values) {
                    urlBuilder.addQueryParameter(key, value);
                }
            }
        }
        builder.url(urlBuilder.build());
    }

    private static void addBodyParam(Request.Builder builder, Map<String, String[]> body, String method) {
        if (body == null) {
            return;
        }
        FormBody.Builder formBodyBuilder = new FormBody.Builder(StandardCharsets.UTF_8);
        for (String key : body.keySet()) {
            String[] values = body.get(key);
            if (values != null) {
                for (String value : values) {
                    formBodyBuilder.add(key, value);
                }
            }
        }
        builder.method(method, formBodyBuilder.build());
    }

    private static byte[] requestExec(Request request, String expectContentType) {
        Objects.requireNonNull(request, "okHttp request is null");

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                ResponseBody body = response.body();
                if (body != null) {
                    String contentType = response.header("Content-Type");
                    byte[] bodyBytes = body.bytes();

                    // 如果不是音频，就打印出错信息（通常是 JSON 格式）
                    if (contentType != null && !contentType.contains(expectContentType)) {
                        String responseText = new String(bodyBytes, StandardCharsets.UTF_8);
                        System.err.println("返回类型不匹配，期望: " + expectContentType + ", 实际: " + contentType);
                        System.err.println("返回内容: " + responseText);
                        return null;
                    }

                    return bodyBytes;
                }
                System.err.println("response body is null");
            } else {
                System.err.println("request failed, http code: " + response.code());
            }
        } catch (IOException ioException) {
            System.err.println("request exec error: " + ioException.getMessage());
            ioException.printStackTrace();
        }

        return null;
    }

}
