package startup;

import link.LinkFilter;
import link.Links;
import page.Page;
import page.PageParserTool;
import page.RequestAndResponseTool;
import util.FileTool;
import util.URLTool;

import java.util.Set;

public class Crawler {

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
        FileTool fileTool = new FileTool();

        initCrawlerWithSeeds(seeds);

        //定义过滤器，提取链接
        LinkFilter filter = new LinkFilter() {
            public boolean accept(String url) {
                if (url.startsWith("http://meta.omaha.org.cn/"))
                    return true;
                else
                    return false;
            }
        };

        //循环条件：待抓取的链接不空且抓取的网页不多于 1000
        System.out.println(Links.getVisitedUrlNum());
        while (!Links.unVisitedUrlQueueIsEmpty() && Links.getVisitedUrlNum() <= 10000) {

            //先从待访问的序列中取出第一个；
            String visitUrl = (String) Links.removeHeadOfUnVisitedUrlQueue();
            if (visitUrl == null) {
                continue;
            }

            //根据URL得到page;
            Page page = RequestAndResponseTool.sendRequstAndGetResponse(visitUrl);

            //对page进行处理： 访问DOM的某个标签
//            Elements es = PageParserTool.select(page, "a");
//            if (!es.isEmpty()) {
//                System.out.println("下面将打印所有a标签： ");
//                System.out.println(es);
//            }

            //将保存文件
            FileTool.saveToLocal(page);

            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //将已经访问过的链接放入已访问的链接中；
            Links.addVisitedUrlSet(visitUrl);

            //得到超链接
            Set<String> links = PageParserTool.getLinks(page, "img");
            for (String link : links) {
                Links.addUnvisitedUrlQueue(link);
                System.out.println("新增爬取路径: " + link);
            }
        }
    }


    //main 方法入口
    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        int min = 10466;
        int max = 11982;
        String suffix[] = new String[max - min + 1];
        for (int i = 0; i <= max - min; i++) {
            suffix[i] = String.valueOf(min + i);
        }
        System.out.println(suffix[suffix.length - 1]);
        URLTool.makeURL(suffix);
        crawler.crawling(suffix);
    }
}