package utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;

/**
 * PDFBox是Java实现的PDF文档协作类库，提供PDF文档的创建、处理以及文档内容提取功能，也包含了一些命令行实用工具。其主要特性包括：
 * 1、提取PDF文件的Unicode文本
 * 2、将PDF切分成多个PDF文件或合并多个PDF文件
 * 3、从PDF表格中提取数据或填写PDF表格
 * 4、验证PDF文件是否符合PDF/A-1b标准
 * 5、使用标准的java API打印PDF文件
 * 6、将PDF文件保存为图像文件，如PNG、JPEG
 * 7、创建一个PDF文件，包含嵌入的字体和图像
 * 8、PDF文件进行数字签名，即对PDF 文档进行加密与解密
 * @author Angela
 */
public class PDFReader {

    private static Logger logger = Logger.getLogger(PDFReader.class);
    /**
     * 获取格式化后的时间信息 
     * @param calendar   时间信息 
     * @return
     */
    public static String dateFormat( Calendar calendar ){
        if( null == calendar )
            return null;
        String date = null;
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat( pattern );
        date = format.format( calendar.getTime() );
        return date == null ? "" : date;
    }

    /**打印纲要**/
    public static void getPDFOutline(String file){
        try {
            //打开pdf文件流
            FileInputStream fis = new FileInputStream(file);
            //加载 pdf 文档,获取PDDocument文档对象
            PDDocument document=PDDocument.load(fis);
            //获取PDDocumentCatalog文档目录对象
            PDDocumentCatalog catalog=document.getDocumentCatalog();
            //获取PDDocumentOutline文档纲要对象
            PDDocumentOutline outline=catalog.getDocumentOutline();
            //获取第一个纲要条目（标题1）
            PDOutlineItem item = outline.getFirstChild();
            if(outline!=null){
                //遍历每一个标题1
                while(item!=null){
                    //打印标题1的文本
                    System.out.println("Item:"+item.getTitle());
                    //获取标题1下的第一个子标题（标题2）
                    PDOutlineItem child=item.getFirstChild();
                    //遍历每一个标题2
                    while(child!=null){
                        //打印标题2的文本
                        System.out.println("    Child:"+child.getTitle());
                        //指向下一个标题2
                        child=child.getNextSibling();
                    }
                    //指向下一个标题1
                    item=item.getNextSibling();
                }
            }
            //关闭输入流
            document.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            logger.info(ex.getMessage());
        } catch (IOException ex) {
            logger.info(ex.getMessage());
        }
    }

    /**打印一级目录**/
    public static void getPDFCatalog(String file){
        try {
            //打开pdf文件流
            FileInputStream fis = new   FileInputStream(file);
            //加载 pdf 文档,获取PDDocument文档对象
            PDDocument document=PDDocument.load(fis);
            //获取PDDocumentCatalog文档目录对象
            PDDocumentCatalog catalog=document.getDocumentCatalog();
            //获取PDDocumentOutline文档纲要对象
            PDDocumentOutline outline=catalog.getDocumentOutline();
            //获取第一个纲要条目（标题1）
            if(outline!=null){
                PDOutlineItem item=outline.getFirstChild();
                //遍历每一个标题1
                while(item!=null){
                    //打印标题1的文本
                    System.out.println("Item:"+item.getTitle());
                    //指向下一个标题1
                    item=item.getNextSibling();
                }
            }
            //关闭输入流
            document.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            logger.info(ex.getMessage());
        } catch (IOException ex) {
            logger.info(ex.getMessage());
        }
    }

    /**获取PDF文档元数据**/
    public static void getPDFInformation(String file){
        try {
            //打开pdf文件流
            FileInputStream fis = new   FileInputStream(file);
            //加载 pdf 文档,获取PDDocument文档对象
            PDDocument document=PDDocument.load(fis);
            /** 文档属性信息 **/
            PDDocumentInformation info = document.getDocumentInformation();

            System.out.println("页数:"+document.getNumberOfPages());

            System.out.println( "标题:" + info.getTitle() );
            System.out.println( "主题:" + info.getSubject() );
            System.out.println( "作者:" + info.getAuthor() );
            System.out.println( "关键字:" + info.getKeywords() );

            System.out.println( "应用程序:" + info.getCreator() );
            System.out.println( "pdf 制作程序:" + info.getProducer() );

            System.out.println( "Trapped:" + info.getTrapped() );

            System.out.println( "创建时间:" + dateFormat( info.getCreationDate() ));
            System.out.println( "修改时间:" + dateFormat( info.getModificationDate()));

            //关闭输入流
            document.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            logger.info(ex.getMessage());
        } catch (IOException ex) {
            logger.info(ex.getMessage());
        }
    }

    /**提取pdf文本**/
    public static String extractTXT(String file){
        String passage = null;
        try{
            //打开pdf文件流
            FileInputStream fis = new FileInputStream(file);
            PDDocument document = PDDocument.load(fis);
            //获取一个PDFTextStripper文本剥离对象           
            PDFTextStripper stripper = new PDFTextStripper();
            //获取文本内容
            passage = stripper.getText(document);
            //打印内容
//            System.out.println( "内容:" + passage );
            document.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return passage;
    }

    /**
     * 提取部分页面文本
     * @param file pdf文档路径
     * @param startPage 开始页数
     * @param endPage 结束页数
     */
    public static String extractTXT(String file,int startPage,int endPage){
        String content = null;
        try{
            //打开pdf文件流
            FileInputStream fis = new FileInputStream(file);

            PDDocument document=PDDocument.load(fis);
            //获取一个PDFTextStripper文本剥离对象           
            PDFTextStripper stripper = new PDFTextStripper();
            // 设置起始页
            stripper.setStartPage(startPage);
            // 设置结束页
            stripper.setEndPage(endPage);
            //获取文本内容
            content = stripper.getText(document);
            //打印内容
            document.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            logger.info(ex.getMessage());
        } catch (IOException ex) {
            logger.info(ex.getMessage());
        }
        return content.trim();
    }

    /**
     * 提取图片并保存
     * @param file PDF文档路径
     * @param imgSavePath 图片保存路径
     */
    public static void extractImage(String file,String imgSavePath){
        try{
            //打开pdf文件流
            FileInputStream fis = new FileInputStream(file);
            //加载 pdf 文档,获取PDDocument文档对象
            PDDocument document=PDDocument.load(fis);
//            * 文档页面信息 *
            //获取PDDocumentCatalog文档目录对象
            PDDocumentCatalog catalog = document.getDocumentCatalog();
            //获取文档页面PDPage列表
            int pages = document.getNumberOfPages();
            int count = 1;

            //遍历每一页
            for( int i = 0; i < pages; i++ ){
                //取得第i页
                PDPage page = document.getPage(i);;
                if( null != page ){
                    PDResources resources = page.getResources();
                    //获取页面图片信息 
                    /*Map<String,PDXObjectImage> imgs = resource.getImages();
                    for(Map.Entry<String,PDXObjectImage> me: imgs.entrySet()){
                        //System.out.println(me.getKey());
                        PDXObjectImage img = me.getValue();
                        //保存图片，会自动添加图片后缀类型
                        img.write2file( imgSavePath + count );
                        count++;
                    }*/
//                    resources.
                    Iterable xobjects = resources.getXObjectNames();
                    if (xobjects != null) {
                        Iterator imageIter = xobjects.iterator();
                        while (imageIter.hasNext()) {
                            COSName key = (COSName) imageIter.next();
                            logger.info(key);
                            if (resources.isImageXObject(key)) {
                                try {
                                    PDImageXObject image = (PDImageXObject) resources.getXObject(key);
                                    BufferedImage bimage = image.getImage();
                                    ImageIO.write(bimage, "jpg", new File(imgSavePath + count + ".jpg"));
                                    count++;
                                    System.out.println(count);
                                } catch (Exception e) {

                                }
                            }

                        }
                    }


                }

            }

            logger.info(count);
            document.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            logger.info(ex.getMessage());
        } catch (IOException ex) {
            logger.info(ex.getMessage());
        }

    }

    /**
     * 提取文本并保存
     * @param file PDF文档路径
     * @param savePath 文本保存路径
     */
    public static void extractTXT(String file,String savePath){
        try{
            //打开pdf文件流
            FileInputStream fis = new FileInputStream(file);

            //获取PDDocument文档对象
            PDDocument pdoc = PDDocument.load(fis);

            //获取一个PDFTextStripper文本剥离对象           
            PDFTextStripper stripper = new PDFTextStripper();
            //创建一个输出流
            Writer writer= new OutputStreamWriter(new FileOutputStream(savePath));
            //保存文本内容
            stripper.writeText(pdoc, writer);
            //关闭输出流
            writer.close();
            //关闭输入流
            pdoc.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            logger.info(ex.getMessage());
        } catch (IOException ex) {
            logger.info(ex.getMessage());
        }
    }

    /**
     * 提取部分页面文本并保存
     * @param file PDF文档路径
     * @param startPage 开始页数
     * @param endPage 结束页数
     * @param savePath 文本保存路径
     */
    public static void extractTXT(String file,int startPage,
                                  int endPage,String savePath){
        try{
            //打开pdf文件流
            FileInputStream fis = new   FileInputStream(file);
            //实例化一个PDF解析器
//            PDFParser parser = new PDFParser((RandomAccessRead) fis);
            //解析pdf文档
//            parser.parse();
            //获取PDDocument文档对象
//            PDDocument document=parser.getPDDocument();
            PDDocument document = PDDocument.load(fis);
            //获取一个PDFTextStripper文本剥离对象           
            PDFTextStripper stripper = new PDFTextStripper();
            //创建一个输出流
            Writer writer=new OutputStreamWriter(new FileOutputStream(savePath));
            // 设置起始页
            stripper.setStartPage(startPage);
            // 设置结束页
            stripper.setEndPage(endPage);
            //保存文本内容
            stripper.writeText(document, writer);
            //关闭输出流
            writer.close();
            //关闭输入流
            document.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            logger.info(ex.getMessage());
        } catch (IOException ex) {
            logger.info(ex.getMessage());
        }
    }

    public static void main(String args[]){
        String file="C:\\Users\\yip\\Desktop\\test\\pdf\\pic\\light.pdf";
        String savePath="C:\\Users\\yip\\Desktop\\light.txt";
        String imagesPath = "C:\\Users\\yip\\Desktop\\test\\images\\";
        long startTime=System.currentTimeMillis();
//        extractTXT(file,savePath);
        String s = extractTXT(file);
//        getPDFOutline(file); 并没有做出outline来
//        getPDFCatalog(file);
//        getPDFInformation(file);
//        extractImage(file, imagesPath);
        long endTime=System.currentTimeMillis();
        System.out.println("读写所用时间为："+(endTime-startTime)+"ms");

    }

}