import org.junit.Test;
import utils.PdfHelper;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {
    @Test
    public void testPattern() {
        String str = ":jds389:sd";
        Pattern pattern = Pattern.compile(":s");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void testName() {
        String s = "\\";
        System.out.println(PdfHelper.getPicInfo(s));
    }

    @Test
    public void testList() {
        File file = new File("C:\\Users\\yip\\Desktop\\light.txt");
        String s= null;
        try(FileInputStream fileReader = new FileInputStream(file)) {
            Long len = file.length();
            byte[] bytes = new byte[len.intValue()];
            fileReader.read(bytes);
            s = new String(bytes);
            System.out.println(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Pattern pattern = Pattern.compile("目\\s*" + "[次录]");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            System.out.println(matcher.group());
            System.out.println(matcher.start() + "-" + matcher.end());
        }
        pattern = Pattern.compile("附\\s*" + "[录]");
        matcher = pattern.matcher(s);
        while (matcher.find()) {
            System.out.println(matcher.group());
            System.out.println(matcher.start() + "-" + matcher.end());
        }
    }

}
