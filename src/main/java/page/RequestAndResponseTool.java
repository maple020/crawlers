package page;


import org.apache.commons.exec.OS;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.io.InputStream;

public class RequestAndResponseTool {


    public static Page sendRequstAndGetResponse(String url) {
        Page page = null;
        // 1.生成 HttpClinet 对象并设置参数
        HttpClient httpClient = new HttpClient();
        // 设置 HTTP 连接超时 30s
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        // 2.生成 GetMethod 对象并设置参数
        GetMethod getMethod = new GetMethod(url);
        Header header = new Header();
        header.setName("User-Agent");
        header.setValue("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        getMethod.setRequestHeader(header);
        // 设置 get 请求超时 30s
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 10000);
        // 设置请求重试处理
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        // 3.执行 HTTP GET 请求
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            // 判断访问的状态码
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + getMethod.getStatusLine());
            }
            // 4.处理 HTTP 响应内容
//            InputStream inputStream = getMethod.log.infoAsStream();
//            byte[] responseBody = new byte[inputStream.available()];
//            inputStream.read(responseBody);
            byte[] responseBody = getMethod.getResponseBody();// 读取为字节 数组
            String contentType = getMethod.getResponseHeader("Content-Type").getValue(); // 得到当前返回类型
            page = new Page(responseBody, url, contentType); //封装成为页面
        } catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            System.out.println("Please check your provided http address!");
            e.printStackTrace();
        } catch (IOException e) {
            // 发生网络异常
            e.printStackTrace();
        } finally {
            // 释放连接
            getMethod.releaseConnection();
        }
        return page;
    }
}
