package page;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class RequestAndResponseTool {


    public static Page sendRequstAndGetResponse(String url) {
        Page page = null;
        // 1.生成 HttpClinet 对象并设置参数
        HttpClient httpClient = new HttpClient();
        // 设置 HTTP 连接超时
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
        // 2.生成 GetMethod 对象并设置参数
        GetMethod getMethod = new GetMethod(url);
        Header header = new Header();
        header.setName("User-Agent");
        header.setValue("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        getMethod.setRequestHeader(header);
        // 设置 get 请求超时
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
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
            InputStream input = getMethod.getResponseBodyAsStream();
//            byte[] responseBody = getMethod.getResponseBody();// 读取为字节 数组 -- 转换为下方法 --解决报错
            String contentType = getMethod.getResponseHeader("Content-Type").getValue(); // 得到当前返回类型
            byte[] responseBody = toByteArray(input);

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

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    public String doPost(String url, JSONObject jsonObject, String charset) {
        Map map = jsonObject;
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String url = "http://admin.tingwen.me/index.php/api/interfaceXykj/touList";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "100");
        JSONObject json = new JSONObject(params);
        RequestAndResponseTool client = new RequestAndResponseTool();
        String result = client.doPost(url, json, "UTF-8");

        System.out.println(result);
    }

}
