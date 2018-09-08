package pojo;

import utils.PdfHelper;

public class Detail {
    /**
     * 细则的名称
     */
    private String name;
    /**
     * 细则的内容
     */
    private String content;
    /**
     * 细则起始位置
     */
    private int start;
    /**
     * 细则的终止位置
     */
    private int end;

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

    public Detail(String name) {
        this.name = name;
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

    public void setContent(String content) {
        this.content = PdfHelper.cutPageNum(content);
    }
}
