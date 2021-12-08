package com.cher.httpclient;

import com.cher.httpclient.pojo.MyJsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.net.ssl.SSLException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Test01_Fundamentals {

    // 官方文档：https://hc.apache.org/httpcomponents-client-4.5.x/index.html

    /**
     * 入门案例1 向 get /http/test 发起请求
     */
    @Test
    public void test01() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/http/test");
        CloseableHttpResponse response = httpClient.execute(httpGet);

        try {
            System.out.println(response.getStatusLine());

            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(entity));

            EntityUtils.consume(entity);

        } finally {
            response.close();
        }
    }

    /**
     * 入门案例2 向 post /http/test 发起请求
     * 并带上参数：username、password
     */
    @Test
    public void test02() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8080/http/test");

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("username", "vip"));
        nvps.add(new BasicNameValuePair("password", "secret"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        CloseableHttpResponse response = httpClient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());

            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(entity));

            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }

    /**
     * 用 HttpClient 发请求的最简模板
     */
    @Test
    public void test03() throws IOException {
        // 创建一个 HttpClient
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建一个请求
        // HttpClient支持HTTP/1.1定义的所有请求：GET、POST、PUT、DELETE、HEAD、TRACE、OPTIONS
        // 对应的请求类：HttpGET、HttpPost、HttpPut、HttpDelete、HttpHead、HttpTrace、HttpOptions
        HttpGet httpget = new HttpGet("http://localhost/");
        // 通过HttpClient执行这个请求
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            // 业务代码
        } finally {
            response.close();
        }
    }

    /**
     * 使用 HttpClient 的 URLBuilder 来构建 URIs
     */
    @Test
    public void test04() throws URISyntaxException {
        // http://www.google.com/search?q=httpclient&btnG=Google+Seach&aq=f&oq=
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.google.com")
                .setPath("/search")
                .setParameter("q", "httpclient")
                .setParameter("btnG", "Google Search")
                .setParameter("aq", "f")
                .setParameter("oq", "")
                .build();
        HttpGet httpGet = new HttpGet(uri);
        System.out.println(httpGet.getURI());
    }

    /**
     * 查看 HttpClient 的 response
     */
    @Test
    public void test05() {
        HttpResponse response =
                new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        System.out.println(response.getProtocolVersion());
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(response.getStatusLine().getReasonPhrase());
        System.out.println(response.getStatusLine().toString());
    }

    /**
     * Working with message headers
     *   添加响应头的方法
     *   获取响应头的方法
     */
    @Test
    public void test06() {
        HttpResponse response =
                new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        response.addHeader("Set-Cookie", "c1=a; path=/; domain=loclahost");
        response.addHeader("Set-Cookie", "c2=b; path=\"/\", c3=c; domain=\"localhost\"");

        // 一般方法获取响应头
        Header h1 = response.getFirstHeader("Set-Cookie");
        System.out.println(h1);
        Header h2 = response.getLastHeader("Set-Cookie");
        System.out.println(h2);
        Header[] hs = response.getHeaders("Set-Cookie");
        System.out.println(hs.length);

        System.out.println("==========");

        // 最高效的方法获取响应头
        HeaderIterator it = response.headerIterator("Set-Cookie");
        while (it.hasNext()) {
            System.out.println(it.next());
        }

        System.out.println("==========");

        // 将响应头内容拆分成独立的项
        HeaderElementIterator heit =
                new BasicHeaderElementIterator(response.headerIterator("Set-Cookie"));
        while (heit.hasNext()) {
            HeaderElement elem = heit.nextElement();
            System.out.println(elem.getName() + " = " + elem.getValue());
            NameValuePair[] params = elem.getParameters();
            for (int i = 0; i < params.length; i++) {
                System.out.println(" " + params[i]);
            }
        }
    }

    /**
     * Http Entity
     *
     * Entity分为三类：
     *   1. streamed
     *   2. self-contained
     *   3. wrapping
     *
     * Entity在发送 request 的时候创建，或者在 response 返回内容的时候创建。
     *
     * 读取 Entity 的内容的方法：
     *   1. 使用 HttpEntity#getContent() 方法，返回一个 java.io.InputStream
     *   2. 使用 HttpEntity#writeTo(OutputStream) 方法，写入指定流
     *   3. HttpEntity#getContentType()  -->  Content-Type: Header
     *   4. HttpEntity#getContentLength()  -->  Content-Length: long
     */
    @Test
    public void test07() throws IOException {
        StringEntity entity =
                new StringEntity("import message",
                        ContentType.create("text/plain", "UTF-8"));
        System.out.println(entity.getContentType());
        System.out.println(entity.getContentLength());

        // EntityUtils工具类 可以将Entity转换为字符串或字节数组
        System.out.println(EntityUtils.toString(entity));
        System.out.println(EntityUtils.toByteArray(entity).length);

        System.out.println("==========");

        // HttpEntity#getContent()
        InputStream is = entity.getContent();
        byte[] buffer = new byte[256];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            for (int i = 0; i < len; i++) {
                System.out.print((char) buffer[i]);
            }
        }
        System.out.println();
        is.close();

        System.out.println("==========");

        // HttpEntity#writeTo(OutputStream)
        entity.writeTo(System.out);
    }

    /**
     * Ensuring release of low level resources
     *
     * 注意点：
     *   使用 HttpEntity#getContent() 后，不仅要关闭 response，还要关闭流。
     *
     * 关闭流和关闭response的区别：
     *   关闭流，会保持连接直到内容读取/写入完毕后，才关闭
     *   关闭response，会立刻断开连接
     *
     * 可以用 EntityUtils#consume(HttpEntity) 来保证entity的数据读取/写入完毕。
     */
    @Test
    public void test08() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/http/test");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream is = entity.getContent();
                try {
                    // 业务代码
                    int len = 0;
                    byte[] buffer = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        System.out.write(buffer, 0, len);
                    }
                } finally {
                    is.close();  // 关闭流
                }
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();  // 关闭response
        }
    }

    /**
     * EntityUtils 的静态方法，可以更方便的读取 Entity 的数据（相比与用流来读取数据）
     *   EntityUtils.toString(HttpEntity)  -->  字符串 String
     *   EntityUtils.toByteArray(HttpEntity)  -->  字节数组 ByteArray
     * 一般情况下，不建议使用EntityUtils来读取数据，除非响应内容是一个可信任HTTP，且能确保响应数据的长度有限。
     */
    @Test
    public void test09() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/http/test");

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                if (len != -1 && len < 2048) {
                    System.out.println(EntityUtils.toString(entity));
                    // 原始的Entity只能读取一次，多次读取会报错
                    // System.out.println(EntityUtils.toString(entity)); --> 报错
                    EntityUtils.consume(entity);
                } else {
                    System.out.println("无数据或数据过长");
                }
            }
        }
    }

    /**
     * Entity读取了一次后，就无法再次读取了。
     * 有时候我们需要复用Entity，最简单的方法就是封装一层 BufferedHttpEntity。
     */
    @Test
    public void test10() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8080/http/test");

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                entity = new BufferedHttpEntity(entity);
            }
            // 此时读取多次也不会报错
            System.out.println(EntityUtils.toString(entity));
            System.out.println(EntityUtils.toString(entity));
        }
    }

    /**
     * Entity除了用于获取 response 响应数据，也可以用在 POST/PUT 请求中用于提交数据。
     * HttpClient 有四个类：
     *   1. StringEntity
     *   2. ByteArrayEntity
     *   3. InputStreamEntity
     *   4. FileEntity
     *   用于提交不同形式的数据。
     */
    @Test
    public void test11() {
        File file = new File("somefile.txt");
        FileEntity entity =
                new FileEntity(file, ContentType.create("text/plain", "UTF-8"));
        HttpPost httpPost = new HttpPost("http://localhost:8080/http/entity");
        httpPost.setEntity(entity);
    }

    /**
     * 提交 HTML表单 数据
     *   1. 使用 BasicNameValuePair 封装参数
     *   2. 使用 List 作为参数的容器
     *   3. 使用 URLEncodedFormEntity 将参数封装成 Entity
     *   4. 将 Entity 设置到请求对象中
     *   5. 发送请求
     */
    @Test
    public void test12() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            List<NameValuePair> formparams = new ArrayList<>();
            formparams.add(new BasicNameValuePair("param1", "value1"));
            formparams.add(new BasicNameValuePair("param2", "value2"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
            HttpPost httpPost = new HttpPost("http://localhost:8080/http/form");
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    long len = resEntity.getContentLength();
                    if (len != -1 && len < 2048) {
                        System.out.println(EntityUtils.toString(resEntity));

                    } else {
                        System.out.println("无数据或数据过长");
                    }
                }
            }
        }
    }

    /**
     * 可以通过 HttpEntity#setChunked() 设置chunk分块传输编码。
     * 这个设置在不支持分块传输的HTTP版本中会被忽略（如 HTTP/1.0）。
     */
    @Test
    public void test13() {
        StringEntity entity =
                new StringEntity("important message",
                        ContentType.create("plain/text", Consts.UTF_8));
        entity.setChunked(true);
        HttpPost httpPost = new HttpPost("http://localhost:8080/http/entity");
        httpPost.setEntity(entity);
    }

    /**
     * 可以自定义 ResponseHandler，然后用该 响应处理器 处理请求对象
     *   ResponseHandler 是一个函数式接口
     *   MyJsonObject 是自定义的POJO类
     */
    @Test
    public void test14() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://localhost:8080/http/json");

            ResponseHandler<MyJsonObject> rh = new ResponseHandler<MyJsonObject>() {
                @Override
                public MyJsonObject handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                    StatusLine statusLine = httpResponse.getStatusLine();
                    HttpEntity entity = httpResponse.getEntity();

                    if (statusLine.getStatusCode() >= 300) {
                        throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
                    }
                    if (entity == null) {
                        throw new ClientProtocolException("Response contains no content");
                    }

                    System.out.println(entity);

                    Gson gson = new GsonBuilder().create();
                    ContentType contentType = ContentType.getOrDefault(entity);
                    // Charset charset = contentType.getCharset();
                    // Reader reader = new InputStreamReader(entity.getContent(), charset);
                    Reader reader = new InputStreamReader(entity.getContent());
                    return gson.fromJson(reader, MyJsonObject.class);
                }
            };

            MyJsonObject myjson = httpClient.execute(httpGet, rh);
            System.out.println(myjson);
        }
    }

    /**
     * HttpClient interface
     *   HttpClient允许自定义策略。
     *   HttpClient是线程安全的，建议使用同一个HttpClient执行不同的多个请求对象。
     *   CloseableHttpClient是可关闭的，当其不再被需要时，调用 CloseableHttpClient#close() 关闭。
     */
    @Test
    public void test15() throws IOException {
        ConnectionKeepAliveStrategy keepAliveStrategy = new DefaultConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                long keepAlive = super.getKeepAliveDuration(response, context);
                if (keepAlive == -1) {
                    keepAlive = 5000;
                }
                return keepAlive;
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom()
                .setKeepAliveStrategy(keepAliveStrategy)
                .build();

        // 业务代码

        httpClient.close();
    }

    /**
     * RequestConfig
     *   原始的 HTTP 是无状态的、面向请求-响应 的协议。
     *   但是现实中，很多应用需要将状态信息持久化。
     *   因此 HttpClient 允许 Request 在特定的上下文环境中执行。
     *   同一个 HttpClient 执行的请求，相当于一次会话，RequestConfig可以复用。
     *
     * 如下：
     *   在 httpGet1 设置了一些请求配置后，httpGet2 中也会生效。
     *   可以通过 test17 中的 HttpClientContext 查看当前的 RequestConfig。
     */
    @Test
    public void test16() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(1000)
                    .setConnectTimeout(1000)
                    .build();
            HttpGet httpGet1 = new HttpGet("http://localhost:8080/http/test");
            httpGet1.setConfig(requestConfig);
            try (CloseableHttpResponse response1 = httpClient.execute(httpGet1)) {
                HttpEntity entity1 = response1.getEntity();
                System.out.println(EntityUtils.toString(entity1));
            }
            HttpGet httpGet2 = new HttpGet("http://localhost:8080/http/json");
            try (CloseableHttpResponse response2 = httpClient.execute(httpGet2)) {
                HttpEntity entity2 = response2.getEntity();
                System.out.println(EntityUtils.toString(entity2));
            }
        }
    }

    /**
     * HTTP protocol interceptors 拦截器
     *
     * 给HttpClient添加拦截器的方法：
     *   1. addInterceptorLast()
     *   2. addInterceptorFirst()
     *
     * 可以通过 HttpContext 的 getAttribute()、setAttribute() 使拦截器共享一些信息。
     *
     * HttpClientContext 的方法：
     *   1. getConnection()     当前连接的目标服务器
     *   1. getTargetHost()     当前连接的目标
     *   2. getHttpRoute()      当前完整连接路由
     *   3. getRequest()        当前请求
     *   4. getResponse()       当前响应
     *   5. getRequestConfig()  当前请求配置
     *
     * 在拦截器中使用的变量，也必须是线程安全的（synchronized）。
     * 如，不能使用 int、Integer 变量，但可以使用 AtomicInteger 类型。
     */
    @Test
    public void test17() throws IOException {
        CloseableHttpClient httpClient = HttpClients.custom()
                .addInterceptorLast(new HttpRequestInterceptor() {
                    @Override
                    public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
                        AtomicInteger count = (AtomicInteger) httpContext.getAttribute("count");
                        System.out.println("Count: " + count);

                        // 打印 HttpContext 上下文环境中的一些信息
                        HttpClientContext localContext = (HttpClientContext) httpContext;
                        System.out.println("Connection: " + localContext.getConnection());
                        System.out.println("TargetHost: " + localContext.getTargetHost());
                        System.out.println("HttpRoute: " + localContext.getHttpRoute());
                        System.out.println("Request: " + localContext.getRequest());
                        System.out.println("Response: " + localContext.getResponse());
                        System.out.println("RequestConfig: " + localContext.getRequestConfig());

                        httpRequest.addHeader("Count", Integer.toString(count.getAndIncrement()));
                    }
                })
                .build();

        AtomicInteger count = new AtomicInteger(1);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAttribute("count", count);

        HttpGet httpGet1 = new HttpGet("http://localhost:8080/http/test");
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(1000)
                .setConnectTimeout(1000)
                .build();
         httpGet1.setConfig(requestConfig);
        for (int i = 0; i < 5; i++) {
            try (CloseableHttpResponse response = httpClient.execute(httpGet1, localContext)) {
                HttpEntity entity = response.getEntity();
            }
        }

        HttpGet httpGet2 = new HttpGet("http://localhost:8080/http/json");
        // httpGet2.setConfig(requestConfig);
        // 在 httpGet2 中，仍然保持了 httpGet1 中设置的 RequestConfig
        for (int i = 0; i < 5; i++) {
            try (CloseableHttpResponse response = httpClient.execute(httpGet2, localContext)) {
                HttpEntity entity = response.getEntity();
            }
        }
    }

    /**
     * Automatic exception recovery  自动化异常恢复
     *   HttpClient 会自动尝试从 I/O 异常中进行恢复
     *   - 不会尝试从 逻辑 或 HTTP协议 错误中进行恢复
     *   - 会自动重试幂等方法
     *   - 当请求仍然在向服务器发送时，会自动重试由于传输异常而失败的方法
     *
     * Request retry handler 请求重试处理器
     *   我们可以定制自己的 请求重试处理器，只需要实现 HttpRequestRetryHandler 接口
     *     - 重写 retryRequest 方法
     *       - return false 表示不重试
     *       - return ture  表示重试
     *   也可以使用 StandardHttpRequestRetryHandler 代替默认请求重试处理器
     *     - 它会自动重试被 RFC-2616 定义为幂等的请求方法
     *     - 包括 get head put delete options trace
     */
    @Test
    public void test18() {
        HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= 5) {
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    // Timeout
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    // Unknown host
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    // Connection refused
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL handshake exception
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // retry if the request is considered idempotent
                    return true;
                }
                return false;
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom()
                .setRetryHandler(myRetryHandler)
                .build();
    }

    /**
     * Abort requests 中止请求
     *   在一些情况下，request并不能在期望时间内完成。（服务器压力大、客户端压力大……）
     *   HttpClient执行的请求，可以在任何阶段中止
     *   通过调用 HttpUriRequest#abort() 方法
     *     - 这个方法是线程安全的
     *     - 当request阻塞时，也能确保中止，并抛出 InterruptedIOException 异常
     */

    /**
     * HttpClient会自动处理所有重定向
     * 我们可以自定义 redirect strategy（重定向策略）来放宽 HTTP规范对POST方法的自动重定向的限制
     */
    @Test
    public void test19() {
        LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setRedirectStrategy(redirectStrategy)
                .build();
    }

    /**
     * Redirect handling  重定向处理
     *   调用 URIUtils#resolve() 方法
     *   可以通过 original request（初始请求） 和 context（上下文）
     *   构建最终请求的绝对路径
     */
    @Test
    public void test20() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpClientContext context = HttpClientContext.create();
            HttpGet httpGet = new HttpGet("http://localhost:8080/redirect/test1");

            try (CloseableHttpResponse response = httpClient.execute(httpGet, context)) {
                HttpHost target = context.getTargetHost();
                List<URI> redirectLocations = context.getRedirectLocations();
                URI location = URIUtils.resolve(httpGet.getURI(), target, redirectLocations);
                System.out.println("Final HTTP location: " + location.toASCIIString());
            }

        } catch (IOException | URISyntaxException exception) {
            exception.printStackTrace();
        }
    }

}
