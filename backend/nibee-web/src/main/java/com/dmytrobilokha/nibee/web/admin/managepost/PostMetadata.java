package com.dmytrobilokha.nibee.web.admin.managepost;

public class PostMetadata {

    private String fileName;
    private String contentDirectoryName;
    private String title;
    private String webPath;

    public PostMetadata() {
    }

    public PostMetadata(
            String fileName
            , String contentDirectoryName
            , String title
            , String webPath
    ) {
        this.fileName = fileName;
        this.contentDirectoryName = contentDirectoryName;
        this.title = title;
        this.webPath = webPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentDirectoryName() {
        return contentDirectoryName;
    }

    public void setContentDirectoryName(String contentDirectoryName) {
        this.contentDirectoryName = contentDirectoryName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebPath() {
        return webPath;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    @Override
    public String toString() {
        return "PostMetadata{"
                + "fileName='" + fileName + '\''
                + ", contentDirectoryName='" + contentDirectoryName + '\''
                + ", title='" + title + '\''
                + ", webPath='" + webPath + '\''
                + '}';
    }

}
