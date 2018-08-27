package startup;

import link.LinkFilter;
import link.Links;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import page.Page;
import page.PageParserTool;
import page.RequestAndResponseTool;
import util.FileTool;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCrawler {
    private static Logger log = Logger.getLogger(MyCrawler.class);

    /**
     * 使用种子初始化 URL 队列
     *
     * @param seeds 种子 URL
     * @return
     */
    private void initCrawlerWithSeeds(String[] seeds) {
        for (int i = 0; i < seeds.length; i++) {
            Links.addUnvisitedUrlQueue(seeds[i]);
        }
    }


    /**
     * 抓取过程
     *
     * @param seeds
     * @return
     */
    public void crawling(String[] seeds) {

        //初始化 URL 队列
        initCrawlerWithSeeds(seeds);

        //定义过滤器，提取以 http://www.baidu.com 开头的链接
        LinkFilter filter = new LinkFilter() {
            public boolean accept(String url) {
                if (url.startsWith("https://db.yaozh.com/clinicaldrug/"))
                    return true;
                else
                    return false;
            }
        };

        //循环条件：待抓取的链接不空且抓取的网页不多于 100000
        while (!Links.unVisitedUrlQueueIsEmpty() && Links.getVisitedUrlNum() <= 100000) {
            //先从待访问的序列中取出第一个；
            String visitUrl = (String) Links.removeHeadOfUnVisitedUrlQueue();
            if (visitUrl == null) {
                continue;
            }

            //根据URL得到page;
            Page page = RequestAndResponseTool.sendRequstAndGetResponse(visitUrl);
            Document document = page.getDoc();
            //对page进行处理： 访问DOM的某个标签
//            Elements es = PageParserTool.select(page, "table");
            //可以取出详细内容的es
            Elements es = document.select("table").select("tr");
//            System.out.println(es.get(2).getElementsByClass("toFindImg"));
//            for (int i = 0; i < es.size(); i++) {
//                Element e = es.get(i);
//                System.out.println(e.toString());
//            }

//            for (int i = 0; i < es.size(); i++) {
//                Element e = es.get(i);
//                String url = e.attr("href");
//                System.out.println(url);
//            }

//            if (!es.isEmpty()) {
//                System.out.println("下面将打印所有a标签： ");
//                System.out.println(es);
//            }
//
//            try {
//                Thread.currentThread().sleep(800);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            //将保存文件
            log.info("目前访问：" + visitUrl);
            try {
                FileTool.saveToLocal(es, "临床合理用药数据库");
            } catch (Exception e) {
                Page page1 = RequestAndResponseTool.sendRequstAndGetResponse(visitUrl);
                Document document1 = page1.getDoc();
                Elements es1 = document1.select("table").select("tr");
                FileTool.saveToLocal(es1, "临床合理用药数据库");


            }
            //将已经访问过的链接放入已访问的链接中；
            Links.addVisitedUrlSet(visitUrl);

            //得到超链接
            Set<String> links = PageParserTool.getLinks(page, "img");
//            Pattern p = Pattern.compile("<a[^>]*>([^<]*)</a>");
//            for (String link : links) {
////                Matcher m = p.matcher(link);
//                if (link.startsWith("http://med.39.net/upLoadImages/disease/day_110929/201109290405145626.gif")) {
//                    Links.addUnvisitedUrlQueue(link);
//                    System.out.println("新增爬取路径: " + link + ",目前还有" + Links.getUnVisitedUrlQueue().size() + "个链接等待爬取");
//                }
//            }

        }

    }




    //    //main 方法入口
//    public static void main(String[] args) {
//        MyCrawler crawler = new MyCrawler();
//        String urlpre = "https://db.yaozh.com/clinicaldrug/";
//        String urlsuf = ".html";
//        int q = 3248;
//        String url[] = new String[3447 - q];
//        for (int i = q + 1; i <= 3447; i++) {
//            url[i - (q + 1)] = urlpre + i + urlsuf;
//        }
//        crawler.crawling(url);
//    }
    //main 方法入口

}