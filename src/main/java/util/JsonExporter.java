package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonExporter {


    private static int depth = 1;

    public static void find(String pathName, int depth) throws IOException {
        JsonExporter jsonExporter = new JsonExporter();
        List<String> jsons = new ArrayList<String>();
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
                jsons.add(jsonExporter.readToString(file.getAbsolutePath()));
            }
        }
        System.out.println("共有" + jsons.size() + "个文件");
        jsonExporter.translate(jsons);

    }

    public void translate(List<String> jsons) throws IOException {
        HashMap<String, String> level01 = new HashMap();
        HashMap<String, String> level12 = new HashMap();
        HashMap<String, String> level23 = new HashMap();
        HashMap<String, String> level34 = new HashMap();
        HashMap<String, String> level45 = new HashMap();

        for (int i = 0; i < jsons.size(); i++) {
            String jsonString = jsons.get(i);
            JSONObject jsonObject = JSON.parseObject(jsonString);
            String parentNameCn = jsonObject.getString("parentNameCn");
            String parentCode = jsonObject.getString("parentCode");
            int parentLevel = jsonObject.getInteger("parentLevel");
            JSONObject atc = jsonObject.getJSONObject("atc");
//            JSONArray subs = jsonObject.getJSONArray("subs");
            if (parentLevel == 0) {
                String cpNameCn = atc.getString("cpNameCn");
                String atcCode = atc.getString("atcCode");
                int level = atc.getInteger("level");
                level01.put("level:" + parentLevel + " code:" + parentCode + " name:" + parentNameCn + " soncode:" + atcCode, "level:" + level + " code:" + atcCode + " name:" + cpNameCn);

            } else if (parentLevel == 1) {
                String cpNameCn = atc.getString("cpNameCn");
                String atcCode = atc.getString("atcCode");
                int level = atc.getInteger("level");
                level12.put("level:" + parentLevel + " code:" + parentCode + " name:" + parentNameCn + " soncode:" + atcCode, "level:" + level + " code:" + atcCode + " name:" + cpNameCn);

            } else if (parentLevel == 2) {
                String cpNameCn = atc.getString("cpNameCn");
                String atcCode = atc.getString("atcCode");
                int level = atc.getInteger("level");
                level23.put("level:" + parentLevel + " code:" + parentCode + " name:" + parentNameCn + " soncode:" + atcCode, "level:" + level + " code:" + atcCode + " name:" + cpNameCn);

            } else if (parentLevel == 3) {
                String cpNameCn = atc.getString("cpNameCn");
                String atcCode = atc.getString("atcCode");
                int level = atc.getInteger("level");
                level34.put("level:" + parentLevel + " code:" + parentCode + " name:" + parentNameCn + " soncode:" + atcCode, "level:" + level + " code:" + atcCode + " name:" + cpNameCn);

            } else if (parentLevel == 4) {
                String cpNameCn = atc.getString("cpNameCn");
                String atcCode = atc.getString("atcCode");
                int level = atc.getInteger("level");
                level45.put("level:" + parentLevel + " code:" + parentCode + " name:" + parentNameCn + " soncode:" + atcCode, "level:" + level + " code:" + atcCode + " name:" + cpNameCn);

            }
        }
        HSSFWorkbook wb = ExcelMaker.getHSSFWorkbook("01", level01, null);
        ExcelMaker.getHSSFWorkbook("12", level01, wb);
        ExcelMaker.getHSSFWorkbook("23", level01, wb);
        ExcelMaker.getHSSFWorkbook("34", level01, wb);
        ExcelMaker.getHSSFWorkbook("45", level01, wb);

//        makeExcel(wb, "Atc");

        System.out.println("success");

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
        find("/Users/jachael/IdeaProjects/crawlers/target/classes/storage4omaha/", depth);
    }


}
