package util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonExporter {


    private static int depth = 1;

    public static void find(String pathName, int depth) throws IOException {
        List<String> fileNames = new ArrayList<String>();
        int filecount = 0;
        //获取pathName的File对象
        File dirFile = new File(pathName);
        //判断该文件或目录是否存在，不存在时在控制台输出提醒
        if (!dirFile.exists()) {
            System.out.println("do not exit");
            return;
        }
        //判断如果不是一个目录，就判断是不是一个文件，时文件则输出文件路径
        if (!dirFile.isDirectory()) {
            if (dirFile.isFile()) {
                System.out.println(dirFile.getCanonicalFile());
            }
            return;
        }

        for (int j = 0; j < depth; j++) {
            System.out.print("  ");
        }
        System.out.print("|--");
        System.out.println(dirFile.getName());
        //获取此目录下的所有文件名与目录名
        String[] fileList = dirFile.list();
        int currentDepth = depth + 1;
        for (int i = 0; i < fileList.length; i++) {
            //遍历文件目录
            String string = fileList[i];
            //File("documentName","fileName")是File的另一个构造器
            File file = new File(dirFile.getPath(), string);
            String name = file.getName();
            //如果是一个目录，搜索深度depth++，输出目录名后，进行递归
            if (file.isDirectory()) {
                //递归
                find(file.getCanonicalPath(), currentDepth);
            } else {
                //如果是文件，则直接输出文件名
                for (int j = 0; j < currentDepth; j++) {
                    System.out.print("   ");
                }
                System.out.print("|--");
                System.out.println(name);
                fileNames.add(file.getAbsolutePath());
            }
        }
        System.out.println("共有" + fileNames.size() + "个文件");
    }

    public String readToString(String[] fileName) {
        String returnString = "";
        for (int i = 0; i < fileName.length; i++) {
            String jsonString = fileName[i];
            returnString += readToString(jsonString);
        }
        return returnString;
    }

    public String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
//        find("/Users/jachael/IdeaProjects/crawlers/target/classes/storage4omaha/", depth);
        JsonExporter jsonExporter = new JsonExporter();
        String a = jsonExporter.readToString("/Users/jachael/IdeaProjects/crawlers/target/classes/storage4omaha/meta.omaha.org.cn__atc_get_id=8901.json;charset=UTF-8");
        System.out.println(a);
    }


    public boolean export(String jsonString) {
        String path = "/Users/jachael/IdeaProjects/crawlers/target/classes/storage4omaha/";

        return false;
    }

    /**
     * 导出Excel
     *
     * @param sheetName sheet名称
     * @param title     标题
     * @param values    内容
     * @param wb        HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, String[][] values, HSSFWorkbook wb) throws IOException {

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if (wb == null) {
            wb = new HSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式 垂直：VerticalAlignment.CENTER

        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < values[i].length; j++) {
                //将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }

        System.out.println(makeExcel(wb, sheetName));
        return wb;

    }

    /**
     * 存excel
     *
     * @param wb
     * @param filename
     * @return
     * @throws IOException
     */
    public static boolean makeExcel(HSSFWorkbook wb, String filename) {
        SimpleDateFormat df = new SimpleDateFormat("dd HH:mm:ss");
        File xlsFile = new File("~/Documents/saveOMAHA/" + filename + df.format(new Date()) + ".xls");
        try {
            FileOutputStream xlsStream = new FileOutputStream(xlsFile);
            wb.write(xlsStream);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}
