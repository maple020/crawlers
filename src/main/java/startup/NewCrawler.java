package startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import link.LinkFilter;
import link.Links;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import page.Page;
import page.PageParserTool;
import page.RequestAndResponseTool;
import util.ExcelMaker;
import util.FileTool;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class NewCrawler {

    private static Logger log = Logger.getLogger(NewCrawler.class);

    private Map initCookies(Map cookies, String user, String pwd, String url) throws Exception {
        LoginModule loginModule = new LoginModule();
        cookies = loginModule.login("Hitales001", "Hitales", "https://www.yaozh.com/login/");
        return cookies;
    }

//    private Map initDatas(Map datas, String user, String pwd, String url) throws Exception {
//        LoginModule loginModule = new LoginModule();
//        datas = loginModule.getDatas(user, pwd, url);
//        return datas;
//    }

    public void craw(String urls[]) throws Exception {
        Map cookies = new HashMap();
//        Map datas = new HashMap();
//
//        if (datas == null || datas.isEmpty()) {
//            datas = initDatas(datas, "Hitales001", "Hitales", "https://www.yaozh.com/login/");
//        }
        if (cookies == null || cookies.isEmpty()) {
            cookies = initCookies(cookies, "Hitales001", "Hitales", "https://www.yaozh.com/login/");
        }

        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];
            Connection con2 = Jsoup
                    .connect(url);
            con2.header("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
            // 设置cookie和post上面的map数据
            Connection.Response response = con2.ignoreContentType(true).method(Method.POST)
                    .cookies(cookies).execute();
            Document doc = response.parse();
            Elements es = doc.getElementsByTag("a");
            Elements e = doc.getElementsByClass("priority1");
            Set<String> links = getLinks(es, "a");
            Set<String> usefulLinks = new HashSet();
            for (String link : links) {
                if (link.contains("docx") || link.contains("pdf")) {
                    usefulLinks.add(link);
                }
            }
            String[] crawlinks = new String[usefulLinks.size()];
            int q = 0;
            for (String link : usefulLinks) {
                crawlinks[q] = link;
                q += 1;
            }

//            crawling(crawlinks);
            log.info(usefulLinks.size() + "已成功保存");

        }

    }

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
    public void crawling(String[] seeds, HSSFWorkbook wb, String filename) {

        //初始化 URL 队列
        initCrawlerWithSeeds(seeds);

        //定义过滤器，提取以 http://www.baidu.com 开头的链接
        LinkFilter filter = new LinkFilter() {
            public boolean accept(String url) {
                if (url.startsWith("https://db.yaozh.com/"))
                    return true;
                else
                    return false;
            }
        };

        //循环条件：待抓取的链接不空且抓取的网页不多于 100000
        while (!Links.unVisitedUrlQueueIsEmpty() && Links.getVisitedUrlNum() <= 1000) {
            //先从待访问的序列中取出第一个；
            String visitUrl = (String) Links.removeHeadOfUnVisitedUrlQueue();
            if (visitUrl == null) {
                continue;
            }

            //根据URL得到page;
            Page page = RequestAndResponseTool.sendRequstAndGetResponse(visitUrl);

            Document doc = page.getDoc();
//            Elements es = doc.getElementsByClass("table table-striped");
//            Elements es = doc.getElementsByClass("table");
            Elements es = doc.getElementsByClass("manual");
//            Parser myParser;
//            NodeList nodeList = null;
            if (es != null) {
                String context = es.toString();
                context = context.replace("</a>", "").replaceAll("<a[^>]*>", "")
                        .replaceAll("<i[^>]*>([^<]*)</i>", "").replaceAll("<div[^>]*>", "")
                        .replace("</div>", "").replaceAll("<script[^>]*>([^<]*)</script>", "")
                        .replace("<span>", "").replace("<p>", "").replace("<b>", "")
                        .replace("</b>", "").replace("</p>", "").replace("</span>", "：")
                        .replace(" \n" + "  \n" + "  \n", "").replaceAll("<style[^>]*>([^<]*)</style>", "")
                        .replace("<strong>", "").replace("</strong>", "")
                        .replace("<br>", "").replace("</br>", "")
                        .replaceAll("<p[^>]*>", "").replace("&nbsp;", "").replace("&gt;", "")
                        .replaceAll("<span[^>]*>", "").replace("查看全文：", "").replace("\n" + "  查看", "");
                if (context.replace(" ", "").length() > 0) {
                    //            myParser = Parser.createParser(context, "gbk");
//            NodeFilter tableFilter = new NodeClassFilter(TableTag.class);
//            OrFilter lastFilter = new OrFilter();
//            lastFilter.setPredicates(new NodeFilter[]{tableFilter});
//            try {
//                // 获取标签为table的节点列表
//                nodeList = myParser.parse(lastFilter);
//                // 循环读取每个table
//                String sheetNameId = visitUrl.substring(31).replace(".html", "");
//                HSSFSheet sheet = wb.createSheet("药材标准" + "第" + sheetNameId + "页");
//                for (int i = 0; i < nodeList.size(); i++) {
//                    log.info(sheetNameId);
//                    if (nodeList.elementAt(i) instanceof TableTag) {
//                        TableTag tag = (TableTag) nodeList.elementAt(i);
//                        TableRow[] rows = tag.getRows();
//                        // 循环读取每一行
//                        for (int j = 0; j < rows.length; j++) {
//                            TableRow row = (TableRow) rows[j];
////                            Row r = sheet.createRow(j);
//                            Row r = rowGetter(sheet, j);
//                            // the reason to get headers is to parse <th> tag
//                            TableHeader[] headers = row.getHeaders();
//                            for (int k = 0; k < headers.length; ++k) {
//                                String head = headers[k].getStringText();
//                                r.createCell(k).setCellValue(head);
////                                System.out.println("第" + j + "行，第" + k + "列的标签的内容为：" + head);
//                            }
//
//                            TableColumn[] columns = row.getColumns();
//                            for (int k = 0; k < columns.length; ++k) {
//                                String info = columns[k].toPlainTextString().trim();
//                                r.createCell(k + 1).setCellValue(info);
////                                System.out.println("第" + j + "行，第" + k + "列的标签的内容为：" + info);
//                            }
//                        }
//                    }
//                }
//            } catch (ParserException e) {
//                e.printStackTrace();
//            }
                    int i = 1;
                    HSSFSheet sheet = wb.getSheet("药材标准");
                    if (sheet == null) {
                        sheet = wb.createSheet("药材标准");
                    }

                    if (sheet.getRow(0) == null) {
                        Row r0 = rowGetter(sheet, 0);
                        r0.createCell(0).setCellValue("药材");
                    }
                    Row r = rowGetter(sheet, i);
                    r.createCell(0).setCellValue(context);
                    ExcelMaker.makeExcel(wb, filename);

                }
            }
//            FileTool.saveToLocal("药物分子靶点数据库",page);

            //将已经访问过的链接放入已访问的链接中；
            Links.addVisitedUrlSet(visitUrl);
            log.info("已完成"+Links.getVisitedUrlNum()+"还剩"+Links.getUnVisitedUrlQueue().size());
        }

    }

    public static Row rowGetter(HSSFSheet sheet, int index) {
        Row r = null;
        if (sheet.getRow(index) == null) {
            r = sheet.createRow(index);
        } else r = rowGetter(sheet, index + 1);
        return r;
    }

    public static Set<String> getLinks(Elements es, String cssSelector) {
        Set<String> links = new HashSet<String>();
        Iterator iterator = es.iterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            if (element.hasAttr("href")) {
                links.add(element.attr("abs:href"));
            } else if (element.hasAttr("src")) {
                links.add(element.attr("abs:src"));
            }
        }
        return links;
    }

    public static void main(String[] args) {
//        String urlpre = "https://db.yaozh.com/cpg?p=";
        File file = new File("/Users/jachael/Documents/药材标准7.xls");
        HSSFWorkbook wb = new HSSFWorkbook();
        try {
            wb = new HSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String urlpre = "https://db.yaozh.com/zhuce?p=";
//        String urlpre = "https://db.yaozh.com/inn/";
//        String urlpre = "https://db.yaozh.com/targets/";
//        String urlpre = "https://db.yaozh.com/interaction/";
        String urlpre = "https://db.yaozh.com/yaocai_bz/";
//        String urlsuf = "&pageSize=30";
        String urlsuf = ".html";
        int y = 117+102+93+8+85+484+1000+207;
        int x = 5000+371+340+11+223+70+y;
        int q = 12880-x;
        int k = 1+x;
        String url[] = new String[q];
        for (int i = 0; i < q; i++) {
            url[i] = urlpre + (i + k) + urlsuf;
        }
        NewCrawler NewCrawler = new NewCrawler();
        try {
            NewCrawler.crawling(url, wb, "药材标准7");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
