package util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLTool {
    public static String turnFormat(String url) {
        String realURL = null;
        try {
            realURL = URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return realURL;
    }

    public static String[] makebaiduURL(String[] diseases) {
        for (int i = 0; i < diseases.length; i++) {
            diseases[i] = "https://baike.baidu.com/item/" + turnFormat(diseases[i]);
        }
        return diseases;
    }

    public static String[] makeURL(String[] seeds) {
        for (int i = 0; i < seeds.length; i++) {
            seeds[i] = "http://meta.omaha.org.cn//atc/get?id=" + seeds[i];

        }
        return seeds;
    }

    public static void main(String[] args) {
        URLTool urlTool = new URLTool();
        String[] a = urlTool.makebaiduURL(new String[]{"肝癌", "肝炎"});
        for (int i = 0; i < a.length; i++) {
            String jb = a[i];
            System.out.println(jb);

        }
    }
}
