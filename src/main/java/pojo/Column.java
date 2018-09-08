package pojo;

import utils.PdfHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 条目类
 */
public class Column {

    private int id;
    /**
     * 条目名称
     */
    private String name;
    /**
     * 条目内容
     */
    private String content;
    /**
     * 条目信息的起始位置
     */
    private int start;
    /**
     * 条目信息的终止位置
     */
    private int end;
    /**
     * 条目是否还有细则
     */
    private boolean hasDatails;

    public boolean isHasDatails() {
        return hasDatails;
    }

    public void setHasDatails(boolean hasDatails) {
        this.hasDatails = hasDatails;
    }

    public List<Detail> details = new ArrayList<Detail>();

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
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

    public String getContent() {
        return content;
    }

    public void setContent(String tempColumn) {
        help(tempColumn, details);
        if (details.size() > 0) {
            hasDatails = true;
            content = tempColumn.substring(0, details.get(0).getStart());
        }
        else content = tempColumn;
        content = PdfHelper.cutPageNum(content);
    }

    public static void help(String tempColumn, List<Detail> temp) {
        Pattern pattern = Pattern.compile("\\n[ ]*(\\d+)[ ][ ]");
        Matcher matcher = pattern.matcher(tempColumn);
        while (matcher.find()) {
            int cl = matcher.start();
            int cr = matcher.end();
            Detail detail = new Detail(matcher.group().trim());
            detail.setStart(cl);
            detail.setEnd(cr);
            temp.add(detail);
        }

        for (int i=0; i<temp.size(); i++) {
            Detail detail = temp.get(i);
            if (i < temp.size()-1) {
                detail.setContent(tempColumn.substring(detail.getEnd(), temp.get(i+1).getStart()).trim());
            } else detail.setContent(tempColumn.substring(detail.getEnd()).trim());
        }
    }

    public Column(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getContent();
    }
}
