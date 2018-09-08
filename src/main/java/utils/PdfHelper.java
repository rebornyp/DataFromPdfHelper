package utils;


import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfHelper {

    private static Logger logger = Logger.getLogger(PdfHelper.class);

    /**
     * 存放从 页码映射表.xls 里提取出来的页码信息
     * 以pdf文件名为键，对应页码链表为值存储
     */
    private static Map<String, List<Integer>> pageMap = new HashMap<>();

    public static Map<String, List<Integer>> getPageMap(String base) {
        return pageMap;
    }

    /**
     * 删除 pdf 提取出的文本里的页码，会对正则匹配造成干扰
     * @param str 输入字符串
     * @return 返回删除后的字符串
     */
    public static String cutPageNum(String str) {
        String s;
        Pattern pattern = Pattern.compile("\\n[ ]\\d+\\n");
        s = cutPageNum(str, pattern);
        s = deleteEnter(s);
        return s;
    }

    /**
     * 实际工作函数
     * @param str
     * @param pattern
     * @return
     */
    private static String cutPageNum(String str, Pattern pattern) {
        Matcher matcher = pattern.matcher(str);
        int before = 0;
        String s = "";
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            s += str.substring(before, start).trim();
            before = end;
        }
        s += str.substring(before, str.length()).trim();
        return s;
    }

    /**
     * 删除内部的换行符
     * @param s
     * @return
     */
    public static String deleteEnter(String s) {
        s = s.replaceAll("\n", "");
        return s;
    }

    /**
     * 提取字符串 str 内部的引用图片和公式信息
     * @param str
     * @return 返回引用的字符串信息
     */
    public static List<String> getPicInfo(String str) {
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile("图[ ](\\d+)\\.(\\d+)\\.(\\d+)");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        pattern = Pattern.compile("[(（](\\d+)\\.(\\d+)\\.(\\d+)[-](\\d+)[）)]");
        matcher = pattern.matcher(str);
        while (matcher.find()) {
            list.add("公式：" + matcher.group());
        }
        return list;
    }

    /**
     * 删除字符串的空格
     * @param str
     * @return
     */
    public static String deleteSpace(String str) {
        str = str.replaceAll(" ", "");
        return str;
    }

    /**
     * 返回文件的子目录的名称数组
     * @param path
     * @return
     */
    public static String[] getFilesName(String path) {
        File file = new File(path);
        return file.list();
    }

    /**
     * 加载并处理 页码映射表.xls
     * @param base
     */
    public static void loadPageNums(String base) {
        File file = new File(base + "\\pageNum\\页码映射表.xls");
        Workbook workbook = null;
        try{
            workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);
            int i = 2;
            while (i < sheet.getRows()) {
                List<Integer> list = new ArrayList<>();
                for (int j=1; j<=4; j++) {
                    if (sheet.getCell(j, i).getType() == CellType.EMPTY) break;
                    list.add(Integer.parseInt(sheet.getCell(j, i).getContents()));
                }
                pageMap.put(sheet.getCell(0, i).getContents(), list);
                i ++;
            }
        } catch (IOException e) {
            logger.error("请查看您项目/pageNum/下是否有  页码映射表.xls  文件！！或者是页码映射表文件有错误需要重新编辑");
            e.printStackTrace();
        } catch (BiffException e) {
            logger.error("页码映射表文件有错误，请重新编辑审核！！");
            e.printStackTrace();
        }
    }
}
