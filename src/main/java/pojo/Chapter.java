package pojo;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chapter {
    private int id;
    /**
     * 章序号，如 4
     */
    private String info;
    /**
     * 章标题
     */
    private String name;
    /**
     * 章序号+标题
     */
    private String wholeInfo;
    /**
     * 整章的内容，
     */
    private String contents;
    /**
     * 看这个章是否直接包含二级目录，比如第一章的直接子目录是 1.0.1小条目，那么该值为 true；
     */
    private boolean isNotNormal;

    private static Logger logger = Logger.getLogger(Chapter.class);

    public boolean isNotNormal() {
        return isNotNormal;
    }

    public void setNotNormal(boolean notNormal) {
        isNotNormal = notNormal;
    }

    public String getContents() {
        return contents;
    }

    /**
     * 对每章的内容进行解析，处理
     * @param tempChapter 当前章的全部内容
     */
    public void setContents(String tempChapter) {
        this.contents = tempChapter;
        logger.debug(contents);
        //先对小节标题在contents中定位下标
        for (int j=0; j<sections.size(); j++) {
            int index = getSectionIndex(sections.get(j), contents);
            logger.debug(sections.get(j).getWholeInfo() + ", " + index);
            sections.get(j).setSecIndex(index);
        }

        for (int j=0; j<sections.size(); j++) {
            int sl = sections.get(j).getSecIndex();
            String tempSection;
            if (j < sections.size()-1) {
                int sr = sections.get(j + 1).getSecIndex();
                tempSection = tempChapter.substring(sl, sr);
            } else {
                tempSection = tempChapter.substring(sl);
            }
            sections.get(j).setContents(tempSection);
        }

        // 如果该章的小节为0，需要判断是否直接含有细则；也即该章是不正常的章 isNotNormal
        if (sections.size() == 0) {
            isNotNormal = true;
            Pattern pattern = Pattern.compile("(\\n)(\\d+)\\.(\\d+)\\.(\\d+)\\s");
            Matcher matcher = pattern.matcher(tempChapter);
            while (matcher.find()) {
                int cl = matcher.start();
                int cr = matcher.end();
                Section section = new Section(matcher.group().trim());
                section.setStart(cl);
                section.setEnd(cr);
                sections.add(section);
            }
            for (int i=0; i<sections.size(); i++) {
                Section section = sections.get(i);
                if (i < sections.size()-1) {
                    section.setContents(tempChapter.substring(section.getEnd(), sections.get(i+1).getStart()).trim(), true);
                } else section.setContents(tempChapter.substring(section.getEnd()).trim(), true);
            }
        }


    }

    /**
     * 判断每一小节在整章的位置，
     * @param section 输入某一小节
     * @param string 整章字符串
     * @return 返回该小节的位置，index
     */
    public int getSectionIndex(Section section, String string) {
        int index;
        Pattern pattern;
        String tempName = section.getName();
        StringBuilder comp = new StringBuilder();
        logger.debug(section.getInfo() + ".........." + section.getWholeInfo() + "........." + section);
        for (char ch : section.getInfo().toCharArray()) {
            comp.append("[ ]*" + ch);
        }
        comp.append("[ ]*");
        for (char ch : tempName.toCharArray()) {
            comp.append("[ ]*" + ch);
        }
        pattern = Pattern.compile(comp.toString());
        Matcher matcher = pattern.matcher(string);
        matcher.find();
        index = matcher.start();
        logger.debug(section.getWholeInfo() +", " + section.getInfo() + ", tempName: " + tempName + ", matchIndex:" + index);
        return index;
    }


    public String getWholeInfo() {
        return wholeInfo;
    }

    public void setWholeInfo(String wholeInfo) {
        this.wholeInfo = wholeInfo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    public List<Section> sections = new ArrayList<Section>();

    public int getIndex() {
        return id;
    }

    public void setIndex(int index) {
        this.id = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Chapter(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public Chapter(String info, String name) {
        this.info = info;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "id=" + id +
                ", info='" + info + '\'' +
                ", name='" + name + '\'' +
                ", sections=" + sections +
                '}' + "\n";
    }
}
