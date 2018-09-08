import org.junit.Test;
import pojo.PDF;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfTest {

    @Test
    public void testLogger() {

        Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
        String s = "5.1.1  混凝土基础的型式构造应根据塔机制造商提供的“塔式起重机使用说明书”及现\n" +
                "场工程地质等要求，选用板式基础或十字型式基础。 \n" +
                "5.1.2  确定基础底面尺寸和计算基础强度时，基底压力应符合本规程第 4章地基计算的\n" +
                "规定；基础配筋应按受弯构件计算确定。 \n" +
                "5.1.3  基础埋置深度的5.1.3  基础埋置深度的5.1.3  基础埋置深度的5.1.3  基础埋置深度的5.1.3  基础埋置深度的";
        Matcher m = pattern.matcher(s);
        System.out.println(m.groupCount());
        while(m.find()) {
            System.out.println(m.group());
            System.out.print("start:"+m.start());
            System.out.println(" end:"+m.end());
        }

    }

    @Test
    public void testList() {
//        PDF pdf = new PDF("sd");
//        pdf.readListFromLocalFiles();
    }


}
