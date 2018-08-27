package util;


import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import page.Page;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*  本类主要是 下载那些已经访问过的文件*/
public class FileTool {
    private static Logger log = Logger.getLogger(FileTool.class);

    private static String dirPath;


    /**
     * getMethod.getResponseHeader("Content-Type").getValue()
     * 根据 URL 和网页类型生成需要保存的网页的文件名，去除 URL 中的非文件名字符
     */
    private static String getFileNameByUrl(String url, String contentType) {
        //去除 http://
        url = url.substring(7);
        //text/html 类型
        if (contentType.indexOf("html") != -1) {
            url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
            return url;
        }
        //如 application/pdf 类型
        else {
            return url.replaceAll("[\\?/:*|<>\"]", "_") + "." +
                    contentType.substring(contentType.lastIndexOf("/") + 1);
        }
    }

    /*
     *  生成目录
     * */
    private static void mkdir() {
        if (dirPath == null) {
            dirPath = Class.class.getClass().getResource("/").getPath() + "storage4med39/";
        }
        File fileDir = new File(dirPath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }

    /*
     *  生成目录
     * */
    private static void mkdir(String folder) {
        if (dirPath == null) {
            dirPath = Class.class.getClass().getResource("/").getPath() + "storage4med39/" + folder + "/";
        }
        File fileDir = new File(dirPath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }

    /*
     *  生成目录
     * */
    private static void mkyaozhidir(String folder) {
        String path = "/Users/jachael/IdeaProjects/crawlers/target/classes/storage4yaozhi/" + folder + "/";
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }

    /*
     *  生成目录
     * */
    private static void newDir(String folder) {
        String filePath = "/Users/jachael/IdeaProjects/crawlers/target/classes/storage4med39/" + folder;
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }

    /*
     *  生成目录
     * */
    private static void newDiryz(String folder) {
        String filePath = "/Users/jachael/IdeaProjects/crawlers/target/classes/storage4yaozhi/" + folder;
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }

    /**
     * 保存网页字节数组到本地文件，filePath 为要保存的文件的相对地址
     */

    public static void saveToLocal(Page page) {
        mkdir();
        String fileName = getFileNameByUrl(page.getUrl(), page.getContentType());
        String filePath = URLDecoder.decode(dirPath + fileName);
        byte[] data = page.getContent();
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            for (int i = 0; i < data.length; i++) {
                out.write(data[i]);
            }
            out.flush();
            out.close();
            log.info("文件：" + fileName + "已经被存储在" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新的保存方法
     */
    public static void saveToLocal(Page page, String folder) {
        mkdir(folder);
        String fileName = getFileNameByUrl(page.getUrl(), page.getContentType());
        String filePath = URLDecoder.decode("/Users/jachael/IdeaProjects/crawlers/target/classes/storage4med39/" + fileName);
        byte[] data = page.getContent();
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            for (int i = 0; i < data.length; i++) {
                out.write(data[i]);
            }
            out.flush();
            out.close();
            log.info("文件：" + fileName + "已经被存储在" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新的保存方法 yaozhi
     */
    public static void saveToLocal(String folder, Page page) {
        mkyaozhidir(folder);
        String fileName = page.getUrl().replace("http://", "").replace("/", "_");
        String filePath = URLDecoder.decode("/Users/jachael/IdeaProjects/crawlers/target/classes/storage4yaozhi/" + folder + "/" + fileName);
        byte[] data = page.getContent();
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            for (int i = 0; i < data.length; i++) {
                out.write(data[i]);
            }
            out.flush();
            out.close();
            log.info("文件：" + fileName + "已经被存储在" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 合并byte数组
     */
    public static byte[] unitByteArray(byte[] byte1, byte[] byte2) {
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }

    /**
     * 新方法 yaozhi
     */
    public static void saveToLocal(Elements es, String folder) {
        newDiryz(folder);
        String fileName = es.get(2).getElementsByClass("toFindImg").toString().replace("</span>", "").replace("<span class=\"toFindImg\">", "");
        String filePath = URLDecoder.decode("/Users/jachael/IdeaProjects/crawlers/target/classes/storage4yaozhi/" + folder + "/" + fileName);
        String text = es.toString();
        text = text.replace("<table class=\"table\">", "").replace("<tbody>", "").replace("</table>", "")
                .replace("</tbody>", "").replace("</span>", "").replace("<span>", "")
                .replace("</font>", "").replace("<font>", "").replace("</th>", "：")
                .replace("<tr>", "").replace("</tr>", "").replace("<span class=\"toFindImg\">", "")
                .replace("</td>", "").replace("<td>", "").replace("<th class=\"detail-table-th\" style=\"min-width:6em;\">", "")
                .replace("&nbsp;", "").replace("<ol>", "").replace("</p>", "")
                .replace("<p>", "").replace("/ol", "").replace("li", "").replace("/li", "");
        while (text.contains("\n\n")) {
            text = text.replace("\n\n", "\n");
        }
        byte[] data = text.getBytes();
        try {
            if (new File(filePath).exists()) {
                filePath = filePath + "_1";
            }
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            for (int i = 0; i < data.length; i++) {
                out.write(data[i]);
            }
            out.flush();
            out.close();
            log.info("文件：" + fileName + "已经被存储在" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新的保存方法39
     */
    public static void saveToLocal(Elements es, Elements es2, String folder) throws UnsupportedEncodingException {
        newDir(folder);
        List<String> esText2 = new ArrayList<String>();
        for (int i = 0; i < es2.size(); i++) {
            Element e = es2.get(i);
            String text = es2.get(i).toString();
            if (!(text.contains("39医学")) && !(text.contains("med.39.net"))) {
                esText2.add(text);
            }
        }
        String fileName = "";
        if (esText2.size() > 0) {
            fileName = esText2.get(0).replace("<strong>", "").replace("</strong>", "").replace("/", "或");
        } else {
            fileName = "有误";
            log.info("文件名出错");
        }
        String filePath = URLDecoder.decode("/Users/jachael/IdeaProjects/crawlers/target/classes/storage4med39/" + folder + "/" + fileName);
//        byte[] data = page.getContent();
        List<String> esText = new ArrayList<String>();
        for (int i = 0; i < es.size(); i++) {
            String text = es.get(i).toString();
            if (!(text.contains("class=")) && !(text.contains("this.value")) && !(text.contains("39医学")) && !(text.contains("med.39.net")) && !(text.contains("请输入关键")) && !(text.contains("分科导航")) && !(text.contains("返回上一级")) && !(text.contains("声明：第三方公司可能在39健康网宣传他们的产品或服务。不过您跟第三方公司的任何交易与39健康网无关，39健康网将不会对可能引起的任何损失负责"))) {
                esText.add(text);
            }
        }
        String content = "";
        if (esText.size() > 0) {
            content = esText.get(0).replace("<p>", "").replace("</p>", "").replace("<br>", "").replace("</br>", "\n").replaceAll("<script[^>]*>([^<]*)</script>", "");
        } else {
            content = "有误";
            log.info("文件内容出错");
        }

        byte[] data = content.toString().getBytes();
//        byte[] data = es.toString().getBytes();

        try {
            //Files.lines(Paths.get("D:\\jd.txt"), StandardCharsets.UTF_8).forEach(System.out::println);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            for (int i = 0; i < data.length; i++) {
                out.write(data[i]);
            }
            out.flush();
            out.close();
            log.info("文件：" + fileName + "已经被存储在" + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 写 excel
     *
     * @param index
     * @param filename
     * @throws IOException
     */
    public void writeExcel(String index, String filename) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
//        String[] text = index.split("\n");

//        for (int row = 0; row < text.length; row++) {
//            HSSFRow rows = sheet.createRow(row);
//            rows.createCell(0).setCellValue(text[row]);
//        }
        HSSFRow rows = sheet.createRow(0);
        rows.createCell(0).setCellValue(index);
        File xlsFile = new File("target/classes/storage/" + filename + ".xls");
        FileOutputStream xlsStream = new FileOutputStream(xlsFile);
        workbook.write(xlsStream);
    }


}
