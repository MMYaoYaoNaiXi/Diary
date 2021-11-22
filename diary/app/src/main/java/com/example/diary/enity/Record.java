package com.example.diary.enity;

public class Record {
    private Integer id;  //记事本id
    private String titleName;  //题目名字
    private String textBody;  //日记内容
    private String author; //作者
    private String createTime;  //创建时间
    private String path;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }



    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", titleName='" + titleName + '\'' +
                ", textBody='" + textBody + '\'' +
                ", author='" + author + '\'' +
                ", createTime='" + createTime + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
