package pojo;

import utils.PdfHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Section {
    private int id;
    /**
     * 该小节的整体信息：小节序号+小节标题
     */
    private String wholeInfo;
    /**
     * 小节在所属章的位置
     */
    private int secIndex;
    /**
     * 小节内容
     */
    private String contents;
    /**
     * 该小节标题在整章的起始位置
     */
    private int start;
    /**
     * 该小节是否还有细则，如果有，则为 true
     */
    private boolean hasDatails;

    public List<Detail> details = new ArrayList<Detail>();

    /**
     * 小节的序号
     */
    private String info;
    /**
     * 小节的标题
     */
    private String name;
    /**
     * 小节包含的条目链表
     */
    public List<Column> columns = new ArrayList<Column>();

    public boolean isHasDatails() {
        return hasDatails;
    }

    public void setHasDatails(boolean hasDatails) {
        this.hasDatails = hasDatails;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    private int end;

    public String getContents() {
        return contents;
    }

    public void setContents(String tempSection) {
        contents = tempSection;
        Pattern pattern = Pattern.compile("(\\n)(\\d+)\\.(\\d+)\\.(\\d+)\\s");
        Matcher matcher = pattern.matcher(tempSection);
        while (matcher.find()) {
            int cl = matcher.start();
            int cr = matcher.end();
            Column column = new Column(matcher.group().trim());
            column.setStart(cl);
            column.setEnd(cr);
            columns.add(column);
        }

        for (int i=0; i<columns.size(); i++) {
            Column column = columns.get(i);
            if (i < columns.size()-1) {
                column.setContent(tempSection.substring(column.getEnd(), columns.get(i+1).getStart()).trim());
            } else column.setContent(tempSection.substring(column.getEnd()).trim());
        }
    }

    public int getSecIndex() {
        return secIndex;
    }

    public void setSecIndex(int secIndex) {
        this.secIndex = secIndex;
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



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Section(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Section(String info, String name) {
        this.info = info;
        this.name = name;
    }

    public Section(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", info='" + info + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public void setContents(String tempSection, boolean b) {
        Column.help(tempSection, details);
        if (details.size() > 0) {
            hasDatails = true;
            contents = tempSection.substring(0, details.get(0).getStart());
        }
        else contents = tempSection;
        contents = PdfHelper.cutPageNum(contents);
    }
}
