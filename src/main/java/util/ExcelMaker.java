package util;

import jdk.internal.util.xml.impl.Input;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelMaker {

    private static String[] columns = new String[]{"药物大类：", "药物小类：", "药物名称：", "英文名：",
            "适应症：", "用法用量：", "药理学：", "药动学：", "相互作用：", "不良反应：", "注意事项：", "疗效评价："};
    private static HSSFWorkbook workBook = new HSSFWorkbook();

    private static int depth = 1;

    public static void make(String pathName, int depth) throws IOException {
        Map<String, String> mContent = new HashMap<String, String>();
        String sheetName = "";
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
//                System.out.println(dirFile.getCanonicalFile());
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
//        List<String> jsons = new ArrayList<String>();
        for (int i = 0; i < fileList.length; i++) {
            //遍历文件目录
            String string = fileList[i];
            //File("documentName","fileName")是File的另一个构造器
            File file = new File(dirFile.getPath(), string);
            String name = file.getName();
            //如果是一个目录，搜索深度depth++，输出目录名后，进行递归
            if (file.isDirectory()) {
                //递归
                make(file.getCanonicalPath(), currentDepth);
            } else {
                String[] sheetNames = file.getParent().split("/");
                sheetName = sheetNames[sheetNames.length - 1];
                InputStream inputStream = new FileInputStream(file);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                String str = new String(bytes);
                mContent.put(file.getName(), str);
                //如果是文件，则直接输出文件名
                for (int j = 0; j < currentDepth; j++) {
                    System.out.print("   ");
                }
                System.out.print("|--");
                System.out.println(name);
            }
        }
        writeExcel(sheetName, mContent);
    }

    public static void writeExcel(String sheetName, Map<String, String> mContent) {
        try {
            getHSSFWorkbook(sheetName, mContent, workBook);
        } catch (FileNotFoundException e) {
            System.out.println("找不到文件");
        } catch (IOException e) {
            System.out.println("存储错误");
        }

    }

    /**
     * 导出Excel
     *
     * @param sheetName sheet名称
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, Map<String, String> levels, HSSFWorkbook wb) throws IOException {

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

        //创建内容
        int i = 0;
        row = sheet.createRow(0);
        row.createCell(0).setCellValue("药物大类");
        row.createCell(1).setCellValue("药物小类");
        row.createCell(2).setCellValue("药物名称");
        row.createCell(3).setCellValue("英文名");
        row.createCell(4).setCellValue("适应症");
        row.createCell(5).setCellValue("用法用量");
        row.createCell(6).setCellValue("药理学");
        row.createCell(7).setCellValue("药动学：");
        row.createCell(8).setCellValue("相互作用：");
        row.createCell(9).setCellValue("不良反应");
        row.createCell(10).setCellValue("注意事项");
        row.createCell(11).setCellValue("疗效评价");

        for (String key : levels.keySet()) {
            row = sheet.createRow(i + 1);
            //将内容按顺序赋给对应的列对象
//            row.createCell(0).setCellValue(key);
            String context = levels.get(key).replace("<o:p>", "").replace("&lt;", "").replace("&gt;", "")
                    .replace("</o:p>", "").replaceAll("</?[p|P][^>]*>", "").replaceAll("<xml[^>]*>([^<]*)</xml>", "")
                    .replaceAll("<m[^>]*>([^<]*)</m>", "").replaceAll("<w:LsdException[^>]*>([^<]*)</w>", "")
                    .replaceAll("<style[^>]*>([^<]*)</style>", "")
                    .replaceAll("</?[!][^>]*>", "").replaceAll("</?[xml][^>]*>", "")
                    .replaceAll("</?[w:][^>]*>", "").replaceAll("</?[style][^>]*>", "")
                    .replace("<o:OfficeDocumentSettings>    <o:RelyOnVML/>    <o:AllowPNG/>   </o:OfficeDocumentSettings>   \n" +
                            "          Normal    0                10 pt    0    2        false    false    false        EN-US    ZH-CN    X-NONE                                                                                            $([{￡￥·‘“〈《「『【〔〖????＄（．［｛￡￥    !%),.:;>?]}￠¨°·ˇˉ?‖’”…‰′″&#8250;℃∶、。〃〉》」』】〕〗?︶︺︾﹀﹄???！＂％＇），．：；？］｀｜｝～￠", "");
            while (context.contains("\n\n")) {
                context = context.replace("\n\n", "\n");
            }
            for (int j = 0; j < columns.length; j++) {
                if ((j < columns.length - 1)) {
                    if (context.indexOf(columns[j]) != -1) {
                        List<String> now = new ArrayList<String>();
                        for (int k = 0; k < columns.length; k++) {
                            if (k > j) {
                                try {
                                    now.add(context.substring(context.indexOf(columns[j]) + columns[j].length(), context.indexOf(columns[k])));
                                } catch (Exception e) {

                                }
                            }
                        }
                        if (now.size() == 0) {
                            row.createCell(j).setCellValue(context.substring(context.indexOf(columns[j]) + columns[j].length()));
                        } else {
                            row.createCell(j).setCellValue(now.get(0));
                        }
                    }
                } else {
                    if (context.indexOf(columns[j]) != -1) {
                        row.createCell(j).setCellValue(context.substring(context.indexOf(columns[j]) + columns[j].length()));
                    }
                }
            }
            i += 1;
        }

        makeExcel(wb, "疾病诊断标准");
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
        File xlsFile = new File("/Users/jachael/Documents/" + filename + ".xls");
        try {
            FileOutputStream xlsStream = new FileOutputStream(xlsFile);
            if (!xlsFile.exists()) {
                xlsFile.createNewFile();
            }
            wb.write(xlsStream);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) throws IOException {
        make("/Users/jachael/IdeaProjects/crawlers/target/classes/storage4yaozhi/", depth);
    }
}
