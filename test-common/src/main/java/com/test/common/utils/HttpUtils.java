package com.test.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import com.test.common.entity.HttpClientEntity;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.XMLType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @program: 调用第三方接口工具类
 * @author: ty
 * @create: 2021-07-14 14:48
 **/
@Slf4j
public class HttpUtils {
    private static CloseableHttpClient httpclient;
    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36";
    // 池化管理
    private static PoolingHttpClientConnectionManager poolConnManager = null;

    static {
        try {
            System.out.println("初始化HttpClientTest~~~开始");
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
            // 配置同时支持 HTTP 和 HTPPS
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
            // 初始化连接管理器
            poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            poolConnManager.setMaxTotal(HttpClientEntity.getMaxTotalConnections());// 同时最多连接数
            // 设置最大路由
            poolConnManager.setDefaultMaxPerRoute(HttpClientEntity.getMaxRouteConnections());
            // 此处解释下MaxtTotal和DefaultMaxPerRoute的区别：
            // 1、MaxtTotal是整个池子的大小；
            // 2、DefaultMaxPerRoute是根据连接到的主机对MaxTotal的一个细分；比如：
            // MaxtTotal=400 DefaultMaxPerRoute=200
            // 而我只连接到http://www.abc.com时，到这个主机的并发最多只有200；而不是400；
            // 而我连接到http://www.bac.com 和
            // http://www.ccd.com时，到每个主机的并发最多只有200；即加起来是400（但不能超过400）；所以起作用的设置是DefaultMaxPerRoute
            // 初始化httpClient
            httpclient = getConnection();

            System.out.println("初始化HttpClientTest~~~结束");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static CloseableHttpClient getConnection() {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(HttpClientEntity.getConnectTimeout()).setConnectionRequestTimeout(HttpClientEntity.getWaitTimeout()).setSocketTimeout(HttpClientEntity.getReadTimeout()).build();
        CloseableHttpClient httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(config)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(HttpClientEntity.getRetryCount(), false)).build();
        return httpClient;
    }

    /**
     * 发送HttpGet请求 * * @param url * 请求地址 * @return 返回字符串
     */
    public static String sendGet(String url, String token) {
        String result = null;
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", userAgent);
            httpGet.setHeader("Authorization", "token " + token);
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                return result;
            } else {
                log.error("请求{}返回错误码：{},{}", url, code, result);
                return null;
            }
        } catch (Exception e) {
            log.error("处理失败 {}" + e);
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }

        }
        return result;
    }

    /**
     * 发送HttpPost请求，参数为map * * @param url * 请求地址 * @param map * 请求参数 * @return 返回字符串
     */
    public static String sendPost(String url, Map<String, String> map) {
        // 设置参数
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        // 编码
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        // 取得HttpPost对象
        HttpPost httpPost = new HttpPost(url);
        // 防止被当成攻击添加的
        httpPost.setHeader("User-Agent", userAgent);
        // 参数放入Entity
        httpPost.setEntity(formEntity);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            // 执行post请求
            response = httpclient.execute(httpPost);
            // 得到entity
            HttpEntity entity = response.getEntity();
            // 得到字符串
            result = EntityUtils.toString(entity, "UTF-8");
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                return result;
            } else {
                log.error("请求{}返回错误码：{},{}", url, code, result);
                return null;
            }
        } catch (IOException e) {
            log.error(url, e);
            result = e.getMessage();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return result;
    }


    /**
     * 发送HttpPost请求，参数为json字符串 * * @param url * @param jsonStr * @return
     */
    public static String sendPost(String url, String jsonStr) {
        String result = null;
        // 字符串编码
        StringEntity entity = new StringEntity(jsonStr, Consts.UTF_8);
        // 设置content-type
        entity.setContentType("application/json");
        HttpPost httpPost = new HttpPost(url);
        // 防止被当成攻击添加的
        httpPost.setHeader("User-Agent", userAgent);
        // 接收参数设置
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            result = EntityUtils.toString(httpEntity, "UTF-8");
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                return result;
            } else {
                log.error("请求{}返回错误码：{},{}", url, code, result);
                return null;
            }
        } catch (IOException e) {
            log.error(url, e);
            result = e.getMessage();
        } finally {
            // 关闭CloseableHttpResponse
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return result;
    }

    public static String sendPostXml(String url, String jsonStr) {
        String result = null;
        // 字符串编码
        StringEntity entity = new StringEntity(jsonStr, Consts.UTF_8);
        // 设置content-type
        entity.setContentType("application/xml");
        HttpPost httpPost = new HttpPost(url);
        // 防止被当成攻击添加的
        httpPost.setHeader("User-Agent", userAgent);
        // 接收参数设置
        httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            result = EntityUtils.toString(httpEntity, "UTF-8");
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                return result;
            } else {
                log.error("请求{}返回错误码：{},{}", url, code, result);
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            // 关闭CloseableHttpResponse
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * 发送不带参数的HttpPost请求 * * @param url * @return
     */
    public static String sendPost(String url) {
        String result = null;
        // 得到一个HttpPost对象
        HttpPost httpPost = new HttpPost(url);
        // 防止被当成攻击添加的
        httpPost.setHeader("User-Agent", userAgent);
        CloseableHttpResponse response = null;
        try {
            // 执行HttpPost请求，并得到一个CloseableHttpResponse
            response = httpclient.execute(httpPost);
            // 从CloseableHttpResponse中拿到HttpEntity
            HttpEntity entity = response.getEntity();
            // 将HttpEntity转换为字符串
            result = EntityUtils.toString(entity, "UTF-8");
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                return result;
            } else {
                log.error("请求{}返回错误码：{},{}", url, code, result);
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            // 关闭CloseableHttpResponse
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * webservice 返回 string
     *
     * @param url   路径地址
     * @param cardmethod webservice调用方法
     * @param nameSpace  webservice命名空间
     * @param param    webservice入参
     * @param timeOut    超时时间
     * @return
     */
    public static Object sendWebservicePcm(String url,String cardmethod, String nameSpace, Map<String, Object> param, String timeOut) throws Exception {
        Object returnStr = null;
        if (StringUtils.isBlank(url) || StringUtils.isBlank(cardmethod) || StringUtils.isBlank(nameSpace)) {
            log.info("webservice地址未设置");
        } else {
            Service service = new Service(); // 创建一个Service实例，注意是必须的！
            Call call = (Call) service.createCall(); // 创建Call实例，也是必须的
            call.setTargetEndpointAddress(new java.net.URL(url)); // 为Call设置服务的位置
            call.setTimeout(Integer.valueOf(timeOut));
            call.setOperationName(new QName(nameSpace, cardmethod));// WSDL里面描述的接口名称
            List<Object> params = new ArrayList<>();
            param.forEach((key, value)->{
                if(value instanceof Boolean){
                    call.addParameter(key, XMLType.XSD_BOOLEAN, ParameterMode.IN);// 接口的参数
                } else if (value instanceof Date){
                    call.addParameter(key, XMLType.XSD_DATETIME, ParameterMode.IN);// 接口的参数
                } else if (value instanceof String){
                    call.addParameter(key, XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数
                } else if (value instanceof Double){
                    call.addParameter(key, XMLType.XSD_DOUBLE, ParameterMode.IN);// 接口的参数
                }else {
                    log.error("参数类型未指定,key：{},value：{}",key,value);
                }
                params.add(value);
            });
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);// 设置返回类型
            returnStr = call.invoke(params.toArray());
        }
        return returnStr;
    }

    /**
     * webservice 返回 string
     *
     * @param url   路径地址
     * @param cardmethod webservice调用方法
     * @param nameSpace  webservice命名空间
     * @param param    webservice入参
     * @param timeOut    超时时间
     * @return
     */
    public static Object sendWebserviceBop(String url,String cardmethod, String nameSpace, Map<String, Object> param, String timeOut) throws Exception {
        Object returnStr = null;
        if (StringUtils.isBlank(url) || StringUtils.isBlank(cardmethod) || StringUtils.isBlank(nameSpace)) {
            log.info("webservice地址未设置");
        } else {
            Service service = new Service(); // 创建一个Service实例，注意是必须的！
            Call call = (Call) service.createCall(); // 创建Call实例，也是必须的
            call.setTargetEndpointAddress(new java.net.URL(url)); // 为Call设置服务的位置
            call.setOperationName(new QName(nameSpace, cardmethod));// WSDL里面描述的接口名称
            call.setTimeout(Integer.valueOf(timeOut));
            List<Object> params = new ArrayList<>();
            OperationDesc oper = new OperationDesc();
            param.forEach((key, value)->{
                if(value instanceof Boolean){
                    ParameterDesc pd = new ParameterDesc(new QName(nameSpace, key), ParameterDesc.IN, XMLType.XSD_BOOLEAN, boolean.class, false, false);
                    oper.addParameter(pd);
                } else if (value instanceof Calendar){
                    ParameterDesc pd = new ParameterDesc(new QName(nameSpace, key), ParameterDesc.IN, XMLType.XSD_DATETIME, Calendar.class, false, false);
                    oper.addParameter(pd);
                } else if (value instanceof String){
                    ParameterDesc pd = new ParameterDesc(new QName(nameSpace, key), ParameterDesc.IN, XMLType.XSD_STRING, String.class, false, false);
                    pd.setOmittable(true);
                    oper.addParameter(pd);
                } else if (value instanceof Double){
                    ParameterDesc pd = new ParameterDesc(new QName(nameSpace, key), ParameterDesc.IN, XMLType.XSD_DOUBLE, double.class, false, false);
                    oper.addParameter(pd);
                }else {
                    log.error("参数类型未指定,key：{},value：{}",key,value);
                }
                params.add(value);
            });
            oper.setStyle(Style.WRAPPED);
            oper.setUse(Use.LITERAL);
            oper.setReturnType(XMLType.XSD_BOOLEAN);
            oper.setReturnClass(boolean.class);
            oper.setReturnQName(new QName(nameSpace, "PointScoreResultResult"));
            call.setOperation(oper);
            returnStr = call.invoke(params.toArray());
        }
        return returnStr;
    }

    public static InputStream httpSendByte(String url, byte[] PostData) {
        URL u = null;
        HttpURLConnection con = null;
        InputStream inputStream = null;
        //尝试发送请求
        try {
            u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "binary/octet-stream");
            OutputStream outStream = con.getOutputStream();
            outStream.write(PostData);
            outStream.flush();
            outStream.close();
            //读取返回内容
            inputStream = con.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
            return inputStream;
        }
    }

    public static byte[] httpPost(final String uri, final String params){
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            // 读取超时
            connection.setReadTimeout(5000);
            // 连接超时
            connection.setConnectTimeout(5000);
            // 缓存
            connection.setUseCaches(false);
            // 发送方式
            connection.setRequestMethod("POST");
            // 允许输入输出
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 设置headers
            connection.setRequestProperty("Content-Type","application/json;charset=utf-8");
            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
            // 参数设置
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(params.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            // 返回码
            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                System.out.println("请求失败:" + connection.getResponseMessage());
                System.out.println("响应编码:" + connection.getResponseCode());
                return null;
            }else{
                System.out.println("请求成功:" + connection.getResponseMessage());
                System.out.println("响应编码:" + connection.getResponseCode());
                // 返回headers
                Map<String, List<String>> map = connection.getHeaderFields();
                System.out.println(map.toString());
                // 返回流
                inputStream = connection.getInputStream();
                // 返回bytes
                byte[] buffer = new byte[1024];
                int len = 0;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((len = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.close();
                // 返回响应内容
                return bos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭inputStream和httpResponse
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }
}
