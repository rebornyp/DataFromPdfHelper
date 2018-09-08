package pojo;

import exceptions.ListMissingException;
import exceptions.ListNotRegular;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.log4j.Logger;
import utils.PDFReader;
import utils.PdfHelper;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDF {

    private static Logger logger = Logger.getLogger(PDF.class);

    /**
     * 该pdf的文件所在路径
     */
    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 保存excel表格时的行号
     */
    private int row = 2;

    /**
     * pdf的名字
     */
    private String name;
    /**
     * pdf的章节
     */
    public List<Chapter> chapters = new ArrayList<Chapter>();
    /**
     * pdf的所有内容，一个长字符串
     */
    private String content;

    /**
     * 整个项目的初始根路径
     */
    private String base = null;

    public void setBase(String base) {
        this.base = base;
    }

    /**
     * 如果出现根据目录无法界定文档内容的情况，
     * 就得通过读取配置文件里人工界定的两部分内容的起始和终止页码来提取内容，
     * 并存储到以下数组中，
     */
    private List<Integer> pageNum = new ArrayList<>();

    /**
     * 构造函数
     * @param s pdf文档名字
     * @param content pdf所有的文本内容
     */
    public PDF(String s, String content) {
        name = s;
        this.content = content;
    }

    private void reset() {
        for (Chapter chapter : chapters)
            chapter.sections.clear();
    }


    /**
     * pdf的初始化函数
     *
     */
    public void initPdf() {
        String ch = null;
        try {
            ch = markChapters();
        } catch (ListMissingException e) {
            try {
                readListFromLocalFiles();
            } catch (FileNotFoundException e1) {
                logger.error("目录信息缺失，请从项目根目录下的/list/文件夹里添加完整的目录文本信息，并按照   " + name + "-目录.txt   的格式命名");
                e1.printStackTrace();
            } catch (ListNotRegular listNotRegular) {
                logger.error("您撰写的目录不合规范，请重新编辑目录！！！");
                listNotRegular.printStackTrace();
            }
        }

        if (null != ch)
            setChapters(ch);
        pageNum = PdfHelper.getPageMap(base).get(name);

//         printChapters(chapters);

    }

    /**
     * 将content里的内容再次解析都每一章，每一节中去；
     * 下标0时，代表此时是从第一个规程部分开始解析，当下标变成1时，就从条文说明开始解析
     */
    private void fillChapters(int i) {
        content = PDFReader.extractTXT(this.path, pageNum.get(2*i), pageNum.get(2*i+1)); //将内容替换为第一个目录所对应的内容
        helpStore();
    }

    /**
     * 从pdf内容中标记出目录所在的子字符串来
     * @return 子字符串
     */
    private String markChapters() throws ListMissingException {
        String chapterPart = null;
        Pattern pattern = Pattern.compile("目\\s*" + "[次录]");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            int left = matcher.start();
            int right = matcher.end();
            logger.debug(matcher.group() + ":" + left + "-" + matcher.end());

            Pattern pattern1 = Pattern.compile("附\\s*" + "[录]");
            Matcher matcher1 = pattern1.matcher(content);
            while (matcher1.find()) {
                right = matcher1.start();
                logger.debug(matcher1.group() + ":" + left + "-" + right);
                if (right > left)
                    break;
            }
            if (left > content.length() / 2) throw new ListMissingException(); //可能第一个目录有缺失，所以这才做下判断，看提出来的目录是不是第二个目录
            chapterPart = content.substring(left, right);
            logger.debug(chapterPart);
        }
        if (null == chapterPart) throw new ListMissingException(); //如果两个目录都有缺失，同样抛出该错误...
        return chapterPart;
    }

    /**
     * 将整个pdf文档写入excel模板里
     * @param file 模板excel文件
     */
    public void WriteIntoExcel(File file, int index) {
        fillChapters(index);
        try {
            Workbook workbook =  Workbook.getWorkbook(file);
            WritableWorkbook writableWorkbook = Workbook.createWorkbook(file, workbook);
            WritableSheet sheet = writableWorkbook.getSheet(0);
            logger.debug(row);
            for (Chapter chapter : chapters) {
                Label label = new Label(4, ++row, chapter.getWholeInfo());
                sheet.addCell(label);
                for (Section section : chapter.sections) {
                    Label label1 = null;
                    if (chapter.isNotNormal()) {
                        String temp = section.getContents();
                        label1 = new Label(6, ++row, section.getName() + ":" + temp);
                        List<String> list = PdfHelper.getPicInfo(temp);
                        if (list.size() > 0) {
                            Label label2 = new Label(8, row, list.toString());
                            sheet.addCell(label2);
                        }
                    } else {
                        label1 = new Label(5, ++row, section.getWholeInfo());
                        if (section.columns.size() == 0) {
                            Label label2 = new Label(6, ++row, section.getContents());
                            sheet.addCell(label2);
                        }
                    }
                    sheet.addCell(label1);
                    for (Detail detail : section.details) {
                        String temp = detail.getContent();
                        Label label3 = new Label(7, ++row, detail.getName() + ":" + temp);
                        sheet.addCell(label3);
                        List<String> list = PdfHelper.getPicInfo(temp);
                        if (list.size() > 0) {
                            Label label2 = new Label(8, row, list.toString());
                            sheet.addCell(label2);
                        }
                    }
                    for (Column column : section.columns) {
                        String temp = column.getContent();
                        Label label2 = new Label(6, ++row, column.getName() + ":" + temp);
                        sheet.addCell(label2);
                        List<String> list = PdfHelper.getPicInfo(temp);
                        if (list.size() > 0) {
                            Label label4 = new Label(8, row, list.toString());
                            sheet.addCell(label4);
                        }
                        if (column.isHasDatails()) {
                            for (Detail detail : column.details) {
                                temp = detail.getContent();
                                Label label3 = new Label(7, ++row, detail.getName() + ":" + temp);
                                list = PdfHelper.getPicInfo(temp);
                                if (list.size() > 0) {
                                    Label label4 = new Label(8, row, list.toString());
                                    sheet.addCell(label4);
                                }
                                sheet.addCell(label3);
                            }
                        }
                    }
                }
            }

            writableWorkbook.write();
            writableWorkbook.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将content里的内容，存到每个章节里去
     */
    private void helpStore() {
        int cr;
        String c = new String(content);
        logger.debug(c);
        for (int i=0; i<chapters.size(); i++) {
            if (i != chapters.size()-1) {
                logger.debug(chapters.get(i+1).getWholeInfo());
                cr = getChapterIndex(chapters.get(i+1), c);
            } else cr = c.length();
            logger.debug(chapters.get(i).getWholeInfo() + "~~~~~" + i + "    " + cr);
            String tempChapter = c.substring(0, cr);
            chapters.get(i).setContents(tempChapter);
            c = c.substring(cr);
        }
    }

    /**
     * 正常解析目录页的函数
     * @param ch 目录页的字符串
     */
    private void setChapters(String ch) {
        try {
            String[] str = ch.split("\n");
            for (String s : str) {
                s = s.trim();
                if (!(s == null || s.length() == 0)) {
                    if (Character.isDigit(s.charAt(0)) && s.length() > 5) {
                        String wholeInfo;
                        int l = s.indexOf(' ');
                        int j = l;
                        for (; j<s.length(); j++) {
                            if (s.charAt(j) == '.') break;
                        }
                        wholeInfo = s.substring(0, j).trim();
                        String tempInfo = s.substring(0, l).trim();
                        tempInfo.replaceAll(" ", "");
                        s = s.substring(l+1);
                        l = s.indexOf('.');
                        String tempName = s.substring(0, l).trim();
                        tempName = PdfHelper.deleteSpace(tempName); //以无空格内容保存在名字中
                        logger.debug("wholeInfo:"+wholeInfo + "-" + "tempInfo:" + tempInfo);
                        int i = tempInfo.indexOf('.');
                        extractCommonInfos(wholeInfo, tempInfo, tempName, i);
                    }
                }
            }
        } catch (Exception e) {
            try {
                readListFromLocalFiles();
            } catch (FileNotFoundException e1) {
                logger.error("目录信息缺失，请从项目根目录下的/list/文件夹里添加完整的目录文本信息，并按照   " + name + "-目录.txt   的格式命名");
            } catch (ListNotRegular listNotRegular) {
                logger.error("您撰写的目录不合规范，请重新编辑目录...");
            }
        }
    }

    /**
     * pdf目录缺失时，从本地读入txt文档，并将其设置成所需的目录
     * @return 返回改段
     * @throws FileNotFoundException
     * @throws ListNotRegular
     */
    public void readListFromLocalFiles() throws FileNotFoundException, ListNotRegular {
        String path = base + "list\\" + name + "-目录.txt";
        logger.debug(path);
        File file = new File(path);
        if (!file.exists()) throw new FileNotFoundException();

        // 以下这段代码将文件里内容全部读出来成一个字符串
        String encoding = "gbk";
        String passage = null;
        byte[] bytes = new byte[new Long(file.length()).intValue()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(bytes);
            passage = new String(bytes, encoding);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 这段代码是将字符串做正则匹配成目录信息
        try {
            String[] str = passage.split("\\n");
            for (String s : str) {
                s = s.trim();
                if (!(s == null || s.length() == 0)) {
                    if (s.charAt(0) <= '9' && s.charAt(0) >= '0' && s.length() > 2) {
                        String wholeInfo = new String(s);
                        int l = s.indexOf(' ');
                        String tempInfo = s.substring(0, l).trim();
                        s = s.substring(l+1);
                        String tempName = s.trim();
                        tempName = PdfHelper.deleteSpace(tempName); //以无空格内容保存在名字中
                        logger.debug("wholeInfo:"+wholeInfo + "-" + "tempInfo:" + tempInfo + "tempName:" + tempName);
                        int i = tempInfo.indexOf('.');
                        extractCommonInfos(wholeInfo, tempInfo, tempName, i);
                    }
                }
            }
            logger.debug(passage);
        } catch (Exception e) {
            logger.debug("目录有问题！");
            e.printStackTrace();
            throw new ListNotRegular();
        }
    }

    /**
     * 将目录信息提取出存放到chapters链表里的方法
     * @param wholeInfo 章节的全信息，注入“2.7 章节名”
     * @param tempInfo 序号信息如 “2.7”
     * @param tempName 名字信息 “名字”
     * @param i 插入到第几章节
     */
    private void extractCommonInfos(String wholeInfo, String tempInfo, String tempName, int i) {
        if (i == -1) {
            Chapter chapter = new Chapter(tempInfo, tempName);
            chapter.setIndex(Integer.parseInt(tempInfo));
            chapters.add(chapter);
            chapter.setWholeInfo(wholeInfo);
        } else {
            int i1 = Integer.parseInt(tempInfo.substring(0, i));
            Section section = new Section(tempInfo, tempName);
            section.setWholeInfo(wholeInfo);
            chapters.get(i1-1).sections.add(section);
        }
    }

    /**
     * 打印一个pdf文档的所有目录信息
     * 包括章，节整体信息
     * 以及各自正则表达式在内容中的位置
     * @param list chapters链表
     */
    private void printChapters(List<Chapter> list) {
        for (Chapter chapter : list) {
            logger.info(chapter.getWholeInfo());
            for (Section section : chapter.sections) {
                int secindex = chapter.getSectionIndex(section, chapter.getContents());
                logger.info(section.getWholeInfo() + ", matchIndex:" + secindex);
            }
            logger.info(getChapterIndex(chapter, content));
        }
    }

    /**
     * 由章节信息，在str中查找到其下标值
     * @param chapter
     * @param str
     * @return
     */
    private int getChapterIndex(Chapter chapter, String str) {
        int index = -1;
        Pattern pattern;
        String tempName;
        StringBuilder comp = new StringBuilder();
        if (chapter.sections.size() > 0) {
            tempName = chapter.sections.get(0).getName();
            for (char ch : chapter.sections.get(0).getInfo().toCharArray()) {
                comp.append("[ ]*" + ch);
            }
        } else {
            tempName= chapter.getName();
            comp.append(chapter.getInfo());
        }

        comp.append("[ ]*");

        for (char ch : tempName.toCharArray()) {
            comp.append("[ ]*" + ch);
        }
        pattern = Pattern.compile(comp.toString());
        Matcher matcher = pattern.matcher(str);
        matcher.find();
        index = matcher.start();
        return index;
    }

    /**
     * 将文档写入txt文件中，作为无法提取到excel中的备选方案
     * @param file
     */
    public void writeIntoTXT(File file) {
        try(FileChannel outputChannel = new FileOutputStream(file).getChannel()) {
            content = PDFReader.extractTXT(this.path);
//            content.replaceAll(" ", "");
            ByteBuffer byteBuffer = ByteBuffer.wrap(content.getBytes());
            outputChannel.write(byteBuffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将pdf文本内容写入txt文件中
     * @param targetExcel
     */
    public void WriteIntoExcel(File targetExcel) {
        WriteIntoExcel(targetExcel, 0);
        if (pageNum.size() == 4) { //如果页码参数有4个的话，意味着有两个目录需要记录
            reset();
            row ++;
            WriteIntoExcel(targetExcel, 1);
        }
    }
}
