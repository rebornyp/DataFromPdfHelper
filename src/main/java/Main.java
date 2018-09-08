import org.apache.log4j.Logger;
import pojo.PDF;
import utils.PDFReader;
import utils.PdfHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class);

    /**
     * 主程序入口，可由命令行输入参数；
     * 输入参数详见说明文档；
     * @param args
     */
    public static void main(String[] args) {

        //程序唯一需要用户指定的工作目录的系统路径，可通过args输入指派；
        String base = "C:\\Users\\yip\\Desktop\\PdfWorker\\";



        String path = base + "sources\\"; //存放初始pdf文件的目录
        String path2 = base + "output\\"; //保存最后excel文件的目录,可以用户自定义输入
        String template = base + "template\\模板文件.xls"; //最终提取出文件的excel模板

        String[] filesName = PdfHelper.getFilesName(path); //获取子目录下所有pdf文件名称
        logger.debug(Arrays.toString(filesName));
        PdfHelper.loadPageNums(base);
        for (String s : filesName) {
            s = s.substring(0, s.indexOf("."));
            String content = PDFReader.extractTXT(path + s + ".pdf"); //一次性提出pdf里所有文本信息
            PDF pdf = new PDF(s, content); //新建一个pdf对象
            pdf.setPath(path + s + ".pdf");
            pdf.setBase(base); //需要整个项目的根路径
            pdf.initPdf(); // pdf初始化函数
            File targetExcel = new File(path2 + s + ".xls"); //最终提取出信息存放的excel文件
            try {
                Files.copy(new File(template).toPath(), targetExcel.toPath()); //将模板文档复制到目标目录中
            } catch (IOException e) {
                logger.error("请检查下是不是已经存在输出文件: " + targetExcel.getName() + "了，谢谢....");
                e.printStackTrace();
            }
            try {
                pdf.WriteIntoExcel(targetExcel); //将pdf 所有信息都写入excel文档中
                logger.info("成功写入到目标文件中：" + path2 + s + ".xls");
            } catch (Exception e) {
                String txt = path2 + s + ".txt"; //对应路径下的txt文件
                pdf.writeIntoTXT(new File(txt));
                logger.error("pdf文档写入excel失败，请查看相应生成的TXT文档: "+ txt + "，并手动复制粘贴...");
                e.printStackTrace();
            }
        }
    }




}
