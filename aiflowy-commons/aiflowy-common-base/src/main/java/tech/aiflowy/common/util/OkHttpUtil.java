package tech.aiflowy.common.util;

import okhttp3.*;
import okio.BufferedSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class OkHttpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(OkHttpUtil.class);
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");


    private static OkHttpClient getOkHttpClient() {
        return OkHttpClientUtil.buildDefaultClient();
    }


    public static String get(String url) {
        return executeString(url, "GET", null, null);
    }

    /**
     * 获取远程URL资源的文件大小（字节数）
     * 支持分块传输（Transfer-Encoding: chunked）的大文件，兼容普通文件
     * @param url 远程资源URL
     * @return 资源字节大小，失败/无有效大小返回 0L
     */
    public static long getFileSize(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = null;
        InputStream in = null;
        try {
            response = getOkHttpClient().newCall(request).execute();

            if (!response.isSuccessful()) {
                LOG.error("Failed to get file size, HTTP response code: {} for url: {}",
                        response.code(), url);
                return 0L;
            }

            ResponseBody body = response.body();
            if (body == null) {
                LOG.warn("Response body is null for url: {}", url);
                return 0L;
            }
            in = body.byteStream();

            byte[] buffer = new byte[1024 * 8];
            long totalBytes = 0L;
            int len;
            while ((len = in.read(buffer)) != -1) {
                totalBytes += len;
            }

            LOG.info("Success to get file size for url: {}, size: {} bytes (≈ {} M)",
                    url, totalBytes, String.format("%.2f", totalBytes / 1024.0 / 1024.0));
            return totalBytes;

        } catch (IOException e) {
            LOG.error("IO exception when getting file size for url: {}", url, e);
            return 0L;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.warn("Failed to close InputStream when getting file size", e);
                }
            }
            if (response != null) {
                response.close();
            }
        }
    }

    public static byte[] getBytes(String url) {
        return executeBytes(url, "GET", null, null);
    }

    public static String get(String url, Map<String, String> headers) {
        return executeString(url, "GET", headers, null);
    }

    public static InputStream getInputStream(String url) {
        try (Response response = getOkHttpClient().newCall(new Request.Builder().url(url).build()).execute();
             ResponseBody body = response.body();
             InputStream in = body != null ? body.byteStream() : null;
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            if (!response.isSuccessful() || in == null) {
                LOG.error("HTTP request failed with code: {} for url: {}", response.code(), url);
                return null;
            }

            byte[] buffer = new byte[1024 * 4];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException ioe) {
            LOG.error("HTTP getInputStream failed: " + url, ioe);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            throw e;
        }
        return null;
    }

    public static String post(String url, Map<String, String> headers, String payload) {
        return executeString(url, "POST", headers, payload);
    }

    public static byte[] postBytes(String url, Map<String, String> headers, String payload) {
        return executeBytes(url, "POST", headers, payload);
    }

    public static String put(String url, Map<String, String> headers, String payload) {
        return executeString(url, "PUT", headers, payload);
    }

    public static String delete(String url, Map<String, String> headers, String payload) {
        return executeString(url, "DELETE", headers, payload);
    }

    public static String multipartString(String url, Map<String, String> headers, Map<String, Object> payload) {
        try (Response response = multipart(url, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.string();
            }
        } catch (IOException ioe) {
            LOG.error("HTTP multipartString failed: " + url, ioe);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            throw e;
        }
        return null;
    }

    public static byte[] multipartBytes(String url, Map<String, String> headers, Map<String, Object> payload) {
        try (Response response = multipart(url, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.bytes();
            }
        } catch (IOException ioe) {
            LOG.error("HTTP multipartBytes failed: " + url, ioe);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            throw e;
        }
        return null;
    }


    public static String executeString(String url, String method, Map<String, String> headers, Object payload) {
        try (Response response = execute0(url, method, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.string();
            }
        } catch (IOException ioe) {
            LOG.error("HTTP executeString failed: " + url, ioe);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            throw e;
        }
        return null;
    }

    public static byte[] executeBytes(String url, String method, Map<String, String> headers, Object payload) {
        try (Response response = execute0(url, method, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.bytes();
            }
        } catch (IOException ioe) {
            LOG.error("HTTP executeBytes failed: " + url, ioe);
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            throw e;
        }
        return null;
    }

    private static Response execute0(String url, String method, Map<String, String> headers, Object payload) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }

        Request request;
        if ("GET".equalsIgnoreCase(method)) {
            request = builder.build();
        } else {
            RequestBody body = RequestBody.create(payload == null ? "" : payload.toString(), JSON_TYPE);
            request = builder.method(method, body).build();
        }

        return getOkHttpClient().newCall(request).execute();
    }

    public static Response multipart(String url, Map<String, String> headers, Map<String, Object> payload) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }

        MultipartBody.Builder mbBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        payload.forEach((key, value) -> {
            if (value instanceof File) {
                File file = (File) value;
                RequestBody body = RequestBody.create(file, MediaType.parse("application/octet-stream"));
                mbBuilder.addFormDataPart(key, file.getName(), body);
            } else if (value instanceof InputStream) {
                RequestBody body = new InputStreamRequestBody(MediaType.parse("application/octet-stream"), (InputStream) value);
                mbBuilder.addFormDataPart(key, key, body);
            } else if (value instanceof byte[]) {
                mbBuilder.addFormDataPart(key, key, RequestBody.create((byte[]) value));
            } else {
                mbBuilder.addFormDataPart(key, String.valueOf(value));
            }
        });

        MultipartBody multipartBody = mbBuilder.build();
        Request request = builder.post(multipartBody).build();

        return getOkHttpClient().newCall(request).execute();
    }

    public static Response multipart(String url, Map<String, String> headers, MultipartBody multipartBody) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);

        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.post(multipartBody).build();
        return getOkHttpClient().newCall(request).execute();
    }

    public static class InputStreamRequestBody extends RequestBody {
        private final InputStream inputStream;
        private final MediaType contentType;

        public InputStreamRequestBody(MediaType contentType, InputStream inputStream) {
            if (inputStream == null) throw new NullPointerException("inputStream == null");
            this.contentType = contentType;
            this.inputStream = inputStream;
        }

        @Override
        public MediaType contentType() {
            return contentType;
        }

        @Override
        public long contentLength() throws IOException {
            return inputStream.available() == 0 ? -1 : inputStream.available();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            IOUtil.copy(inputStream, sink);
        }
    }
}
