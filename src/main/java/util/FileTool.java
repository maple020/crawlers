package util;


import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import page.Page;

import java.io.*;
import java.net.URLDecoder;

/*  本类主要是 下载那些已经访问过的文件*/
public class FileTool {

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
            dirPath = Class.class.getClass().getResource("/").getPath() + "storage4omaha/";
        }
        File fileDir = new File(dirPath);
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
            //Files.lines(Paths.get("D:\\jd.txt"), StandardCharsets.UTF_8).forEach(System.out::println);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            for (int i = 0; i < data.length; i++) {
                out.write(data[i]);
            }
            out.flush();
            out.close();
            System.out.println("文件：" + fileName + "已经被存储在" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static void main(String[] args) {
        FileTool fileTool = new FileTool();
        try {
            fileTool.writeExcel("123\n456", "123");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
