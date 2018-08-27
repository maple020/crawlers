package startup;

import link.LinkFilter;
import link.Links;
import org.apache.log4j.Logger;
import org.jsoup.select.Elements;
import page.Page;
import page.PageParserTool;
import page.RequestAndResponseTool;
import util.FileTool;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Crawler {

    private static Logger log = Logger.getLogger(Crawler.class);

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
        log.info(Links.getVisitedUrlNum());
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
//                log.info("下面将打印所有a标签： ");
//                log.info(es);
//            }

            //将保存文件
            FileTool.saveToLocal(page);

            try {
                Thread.currentThread().sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //将已经访问过的链接放入已访问的链接中；
            Links.addVisitedUrlSet(visitUrl);

            //得到超链接
            Set<String> links = PageParserTool.getLinks(page, "img");
            for (String link : links) {
                Links.addUnvisitedUrlQueue(link);
                log.info("新增爬取路径: " + link);
            }
        }
    }

    /**
     * med39 抓取过程
     *
     * @param seeds
     * @return
     */
    public void crawling39(String[] seeds, String filefolder) {

        //初始化 URL 队列
        initCrawlerWithSeeds(seeds);

        //定义过滤器，提取以 http://www.baidu.com 开头的链接
        LinkFilter filter = new LinkFilter() {
            public boolean accept(String url) {
                if (url.startsWith("http://med.39.net/cds/jbzd"))
                    return true;
                else
                    return false;
            }
        };

        //循环条件：待抓取的链接不空且抓取的网页不多于 100000
        while (!Links.unVisitedUrlQueueIsEmpty() && Links.getVisitedUrlNum() <= 100000) {
//            Links.showLinks();
            //先从待访问的序列中取出第一个；
            String visitUrl = (String) Links.removeHeadOfUnVisitedUrlQueue();
            if (visitUrl == null) {
                continue;
            }
            log.info("目前路径" + visitUrl);

            //根据URL得到page;
            Page page = RequestAndResponseTool.sendRequstAndGetResponse(visitUrl);
            //对page进行处理： 访问DOM的某个标签
            /**
             Elements es = PageParserTool.select(page, "a");

             if (!es.isEmpty()) {
             log.info("下面将打印所有a标签： ");
             log.info(es);
             }
             for (int i = 0; i < es.size(); i++) {
             Element e = es.get(i);
             String url = e.attr("href");
             log.info(url);
             }
             **/
//            Elements es = PageParserTool.select(page, "i");

            //休眠，以防服务器挂掉
            try {
                Thread.currentThread().sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //将保存文件
//            FileTool.saveToLocal(page, filefolder);

            //将已经访问过的链接放入已访问的链接中；
            Links.addVisitedUrlSet(visitUrl);

            //得到超链接
            Set<String> links = PageParserTool.getLinks(page, "a");
//            Pattern p = Pattern.compile("<a[^>]*>([^<]*)</a>");
            for (String link : links) {
//                Matcher m = p.matcher(link);
                link = link.toLowerCase();
                if (!(visitUrl.startsWith("http://med.39.net/cds/jbzd/1-")) || !(visitUrl.startsWith("http://med.39.net/cds/jbzd/2-")) ||
                        !(visitUrl.startsWith("http://med.39.net/cds/jbzd/5-")) || !(visitUrl.startsWith("http://med.39.net/cds/jbzd/8-")) ||
                        !(visitUrl.startsWith("http://med.39.net/cds/jbzd/6-")) || !(visitUrl.startsWith("http://med.39.net/cds/jbzd/7-")) ||
                        !(visitUrl.startsWith("http://med.39.net/cds/jbzd/3-")) || !(visitUrl.startsWith("http://med.39.net/cds/jbzd/4-"))) {
                    if (link.startsWith("http://med.39.net/cds/jbzd/") && !(link.contains("efault.aspx")) && !(link.contains("#")) && !(link.contains("list"))) {
//                    newlinks.put(link, es.toString());
                        Links.addUnvisitedUrlQueue(link);
                        log.info("新增爬取路径: " + link + ",目前还有" + Links.getUnVisitedUrlQueue().size() + "个链接等待爬取");
                    }
                }
            }

            if (visitUrl.startsWith("http://med.39.net/cds/jbzd/2-") || visitUrl.startsWith("http://med.39.net/cds/jbzd/1-") ||
                    visitUrl.startsWith("http://med.39.net/cds/jbzd/5-") || visitUrl.startsWith("http://med.39.net/cds/jbzd/7-") ||
                    visitUrl.startsWith("http://med.39.net/cds/jbzd/6-") || visitUrl.startsWith("http://med.39.net/cds/jbzd/8-") ||
                    visitUrl.startsWith("http://med.39.net/cds/jbzd/3-") || visitUrl.startsWith("http://med.39.net/cds/jbzd/4-")) {
                //图片--待用
//                Set<String> links1 = PageParserTool.getLinks(page, "img");
                //内容
                Elements es = PageParserTool.select(page, "p");
                //标题
                Elements es2 = PageParserTool.select(page, "strong");

                try {
                    FileTool.saveToLocal(es, es2, filefolder);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        }
    }


//omaha 方法入口
//    public static void main(String[] args) {
//        Crawler crawler = new Crawler();
//        int min = 5966;
//        min = 6106;
//        int max = 11982;
//        String suffix[] = new String[max - min + 1];
//        for (int i = 0; i <= max - min; i++) {
//            suffix[i] = String.valueOf(min + i);
//        }
//        log.info(suffix[suffix.length - 1]);
//        URLTool.makeURL(suffix);
//        crawler.crawling(suffix);
//    }

    //med39
    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        //外科疾病
//        String[] folders = new String[]{"外科疾病-普通外科", "外科疾病-骨科", "外科疾病-胸外科", "外科疾病-泌尿外科", "外科疾病-神经外科"
//                , "外科疾病-损伤", "外科疾病-外科感染", "外科疾病-休克", "外科疾病-多器官功能衰竭", "外科疾病-麻醉及有关的评分"
//                , "外科疾病-肿瘤", "外科疾病-中医、中西医结合病症"};
//        Integer[] sizes = new Integer[]{63, 55, 24, 20, 28, 12, 7, 3, 5, 6, 3, 32};
//        for (int j = 0; j < folders.length; j++) {
//            crawler.startup(folders[j], sizes[j], j, "http://med.39.net/cds/jbzd/list2-1-", 2, 7658);
//        }
        //儿科疾病
//        String[] folders_ek = new String[]{"儿科疾病-新生儿疾病", "儿科疾病-心血管系统疾病", "儿科疾病-消化系统疾病", "儿科疾病-呼吸系统疾病"
//                , "儿科疾病-泌尿系统疾病", "儿科疾病-血液系统疾病", "儿科疾病-内分泌及代谢疾病", "儿科疾病-神经系统疾病", "儿科疾病-精神系统疾病"
//                , "儿科疾病-传染病", "儿科疾病-结缔组织和免疫性疾病", "儿科疾病-营养紊乱性疾病", "儿科疾病-其他疾病", "儿科疾病-中医、中西医结合病症"
//                , "儿科疾病-儿外科疾病"};
//        Integer[] sizes_ek = new Integer[]{9, 25, 14, 21, 13, 24, 14, 22, 4, 24, 17, 6, 9, 13, 14};
//        for (int j = 0; j < folders_ek.length; j++) {
//            crawler.startup(folders_ek[j], sizes_ek[j], j, "http://med.39.net/cds/jbzd/list2-2-", 15, 7671);
//        }
        //妇产科疾病
//        String[] folders_fck = new String[]{"妇产科疾病-异常妊娠", "妇产科疾病-妊娠合并症", "妇产科疾病-异常分娩", "妇产科疾病-分娩合并症", "妇产科疾病-女性生殖器官炎症"
//                , "妇产科疾病-女性生殖器官肿瘤及肿瘤样病变", "妇产科疾病-女性生殖道其他病变", "妇产科疾病-子宫内膜异位症", "妇产科疾病-滋养细胞疾病"
//                , "妇产科疾病-月经病", "妇产科疾病-乳腺疾病", "妇产科疾病-胎儿、新生儿疾病", "妇产科疾病-妇产科未分类的疾病", "妇产科疾病-中医、西医结合病症"};
//        Integer[] sizes_fck = new Integer[]{13, 18, 4, 5, 9, 18, 8, 4, 5, 7, 4, 6, 9, 10};
//        for (int j = 0; j < folders_fck.length; j++) {
//            crawler.startup(folders_fck[j], sizes_fck[j], j, "http://med.39.net/cds/jbzd/list2-3-", 31, 7687);
//        }
        //口腔科疾病
//        String[] folders_kqk = new String[]{"口腔科疾病-牙体牙髓病", "口腔科疾病-牙周病", "口腔科疾病-口腔黏膜疾病", "口腔科疾病-口腔颌面部炎症"
//                , "口腔科疾病-口腔颌面部损伤", "口腔科疾病-口腔颌面部肿瘤及瘤样病变", "口腔科疾病-涎腺疾病", "口腔科疾病-颞下颌关节疾病"
//                , "口腔科疾病-口腔颌面部神经性疾病", "口腔科疾病-口腔颌面部先天性畸形及缺损", "口腔科疾病-口腔正畸科与牙颌畸形疾病"
//                , "口腔科疾病-系统性疾病在口腔的表现", "口腔科疾病-口腔颌面部有关综合征", "口腔科疾病-未分类疾病及各种指数"};
//        Integer[] sizes_kqk = new Integer[]{8, 8, 10, 3, 3, 12, 11, 4, 3, 3, 5, 5, 3, 6};
//        for (int j = 0; j < folders_kqk.length; j++) {
//            crawler.startup(folders_kqk[j], sizes_kqk[j], j, "http://med.39.net/cds/jbzd/list2-4-", 46, 7702);
//        }
        //眼科疾病
//        String[] folders_yk = new String[]{"眼科疾病-眼睑病","眼科疾病-泪腺疾病","眼科疾病-结膜病","眼科疾病-角膜病"
//                ,"眼科疾病-巩膜疾病","眼科疾病-葡萄膜病","眼科疾病-青光眼","眼科疾病-晶状体病","眼科疾病-玻璃体病"
//                ,"眼科疾病-视网膜病","眼科疾病-视神经与视路病变","眼科疾病-眼的屈光和调节","眼科疾病-眼外肌疾病","眼科疾病-眼眶病"
//                ,"眼科疾病-眼外伤及职业性眼病","眼科疾病-眼科综合症","眼科疾病-全身性疾病的眼部表现","眼科疾病-眼科中医病症"};
//        Integer[] sizes_yk = new Integer[]{6,2,4,8,1,6,6,4,3,15,5,3,5,4,10,6,9,7};
//        for (int j = 0; j < folders_yk.length; j++) {
//            crawler.startup(folders_yk[j], sizes_yk[j], j, "http://med.39.net/cds/jbzd/list2-5-", 61, 7717);
//        }
        //耳鼻咽喉科疾病
//        String[] folders_eb = new String[]{"耳鼻咽喉科疾病-耳科疾病", "耳鼻咽喉科疾病-鼻部疾病", "耳鼻咽喉科疾病-咽部疾病", "耳鼻咽喉科疾病-喉部疾病"
//                , "耳鼻咽喉科疾病-耳鼻咽喉科畸形病", "耳鼻咽喉科疾病-耳鼻咽喉科外伤", "耳鼻咽喉科疾病-神经耳鼻咽喉科"
//                , "耳鼻咽喉科疾病-耳鼻咽喉科急诊", "耳鼻咽喉科疾病-头颈部肿瘤和未分类疾病", "耳鼻咽喉科疾病-耳鼻咽喉科中医病症"};
//        Integer[] sizes_eb = new Integer[]{16, 15, 4, 8, 5, 5, 10, 3, 6, 5};
//        for (int j = 0; j < folders_eb.length; j++) {
//            crawler.startup(folders_eb[j], sizes_eb[j], j, "http://med.39.net/cds/jbzd/list2-6-", 80, 7736);
//        }
        //皮肤科疾病
//        String[] folders_pf = new String[]{"皮肤科疾病-感染性皮肤病", "皮肤科疾病-寄生虫、昆虫及动物性皮肤病", "皮肤科疾病-变应性皮肤病"
//                , "皮肤科疾病-物理、职业性皮肤病", "皮肤科疾病-红斑、丘疹、鳞屑性皮肤病"
//                , "皮肤科疾病-水疱及脓性皮肤病", "皮肤科疾病-结缔组织病、血管炎性皮肤病", "皮肤科疾病-皮肤障碍性皮肤病", "皮肤科疾病-内分泌、代谢、营养障碍性皮肤病"
//                , "皮肤科疾病-皮肤附属器疾病", "皮肤科疾病-皮肤肿瘤", "皮肤科疾病-性传播疾病","皮肤科疾病-皮肤病相关综合征","皮肤科疾病-未分类性皮肤病"
//                ,"皮肤科疾病-全身性疾病、妊娠及药物所致的皮肤表现","皮肤科疾病-中医、中西医结合的皮肤病",};
//        Integer[] sizes_pf = new Integer[]{19,4,8,10,9,7,16,3,5,4,13,9,5,14,4,8};
//        for (int j = 0; j < folders_pf.length; j++) {
//            crawler.startup(folders_pf[j], sizes_pf[j], j, "http://med.39.net/cds/jbzd/list2-7-", 91, 7747);
//        }
        //内科疾病
//        String[] folders_nk = new String[]{"内科疾病-循环系统疾病","内科疾病-消化系统疾病","内科疾病-呼吸系统疾病","内科疾病-泌尿系统疾病"
//                ,"内科疾病-造血系统疾病","内科疾病-内分泌系统疾病","内科疾病-神经系统疾病","内科疾病-精神疾病","内科疾病-传染病"
//                ,"内科疾病-新陈代谢疾病","内科疾病-结缔组织和免疫性疾病","内科疾病-职业病及理、化、生物因素疾病","内科疾病-老年人疾病"
//                ,"内科疾病-其他疾病","内科疾病-中医、中西医结合病症"};
//        Integer[] sizes_nk = new Integer[]{68,61,48,20,55,15,40,11,51,23,26,17,11,18,31};
//        for (int j = 0; j < folders_nk.length; j++) {
//            crawler.startup(folders_nk[j], sizes_nk[j], j, "http://med.39.net/cds/jbzd/list2-8-", 108, 7764);
//        }

//            crawler.crawling39(new String[]{"http://med.39.net/cds/jbzd/1-3-17188.shtml"},"测试");
    }

    public void startup(String folder, int size, int j, String urlpre, int m, int n) {
        Crawler crawler = new Crawler();
        log.info("开始进行" + folder + "的爬取，该目录共有" + size + "页");
        String urls[] = new String[size];
        for (int i = 1; i < (urls.length + 1); i++) {
            String url = urlpre + (j + m) + "-" + (j + n) + "-" + i + ".shtml";
            urls[i - 1] = url;
        }
        crawler.crawling39(urls, folder);
        log.info(folder + "爬取完成！");
    }
}