package com.software.androidthesis.http.requesst;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 22:22
 * @Decription:
 */
public class RetrofitManager {
    //maxSizeCache：定义了缓存大小，这里设置为10MB
    private long maxSizeCache = 10 * 1024 * 1024;           //最大缓存
    //缓存请求API对象
    //apiCacheMap：用于缓存Retrofit服务接口的实例，避免重复创建同一个服务接口
    private ConcurrentHashMap<String,Object> apiCacheMap = new ConcurrentHashMap();
    //instance：RetrofitManager类的单例实例
    private static RetrofitManager instance;
    //retrofit：Retrofit的实例
    private Retrofit retrofit;
    private final OkHttpClient client;//client：OkHttpClient的实例，用于处理所有的HTTP请求
    private static volatile String currentUrl = null; // 用于存储当前 URL

    /**
     * 这是一个私有构造函数，不允许外部直接实例化，确保单例模式的实施。构造函数的主要任务如下：
     * 1.设置缓存：创建一个指定大小的缓存目录和缓存实例，用于存储响应数据。
     * 2.SSL配置：配置一个信任所有证书的SSLContext和SSLSocketFactory，
     *      这通常不推荐在生产环境使用，因为它可能会导致安全问题（如中间人攻击）。
     *      这部分通常用于开发阶段或特定环境。
     * 3.创建OkHttpClient：配置OkHttpClient，包括超时设置、缓存策略、
     *      和主机名验证器（这里设置为始终返回 true，同样存在安全风险）。
     * 4.创建Retrofit实例：利用上面配置的OkHttpClient和基础URL构建Retrofit实例。
     */

    private RetrofitManager(final Context context, String url) {
        Gson customGson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        File httpCacheDirectory = new File(context.getExternalCacheDir(), "responses");
        //设置缓存 10M
        Cache cache = new Cache(httpCacheDirectory, maxSizeCache);
        //1.创建Retrofit对象
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        client = builder
                .readTimeout(30000, TimeUnit.MILLISECONDS) //设置读取超时时间
                .cache(cache) //设置缓存
                .connectTimeout(30000, TimeUnit.MILLISECONDS) //设置连接超时时间
                .build(); // 构建OkHttpClient对象
        retrofit = new Retrofit.Builder()
                .client(client) //设置HTTP客户端
                .baseUrl(url) //设置基础URL
                .addConverterFactory(GsonConverterFactory.create(customGson)) //添加Gson转换器
                .build(); //构建Retrofit对象
    }

    /**
     * 单例模式
     * getInstance(Context context)：这个方法用于获取RetrofitManager的单例实例。
     * 使用双重检查锁定模式，确保线程安全和单例的唯一性。
     */
    public static RetrofitManager getInstance(Context context, String url) {
        // 检查 instance 是否为 null 或 url 是否发生变化
        if (instance == null || !url.equals(currentUrl)) {
            synchronized (RetrofitManager.class) {
                // 再次检查以确保在锁等待期间状态没有变化
                if (instance == null || !url.equals(currentUrl)) {
                    instance = new RetrofitManager(context, url);
                    currentUrl = url; // 更新当前的 URL
                }
            }
        }
        return instance;
    }


    /**
     * 获取API服务接口
     * getApi(final Class<T> service)：此方法用于从缓存中获取API服务接口的实例。
     *      如果缓存中不存在，它会创建一个新的实例并存入缓存。
     * getApi(final Class<T> service, String baseUrl)：此方法允许为特定服务接口
     *      指定不同的基础URL，用于创建新的Retrofit实例。
     */
    public <T> T getApi(final Class<T> service) {
        Object cacheService = apiCacheMap.get(service.getName());
        if (cacheService != null) {
            return (T) cacheService;
        }
        T serviceInstance = retrofit.create(service);
        apiCacheMap.put(service.getName(), serviceInstance);
        return serviceInstance;
    }

    /**
     *
     * @param service
     * @param baseUrl 域名
     * @param <T>
     * @return
     */
    public <T> T getApi(final Class<T> service,String baseUrl) {
        Retrofit customRetrofit = buildRetrofit(baseUrl);
        return customRetrofit.create(service);
    }

    /**
     * 通过自定义域名构建Retrofit
     * @param baseUrl
     * @return
     */
    /**
     * 动态构建 Retrofit 实例
     * buildRetrofit(String baseUrl)：这是一个私有方法，用于构建一个新的Retrofit实例，
     *      配置了客户端、基础URL和Gson转换器。这使得可以灵活地为不同的服务指定不同的基础URL。
     */
    private Retrofit buildRetrofit(String baseUrl){
        return new Retrofit.Builder()
                .client(client)  // 定义访问的主机地址
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())  // 解析方法 Gson
                .build();
    }
}
