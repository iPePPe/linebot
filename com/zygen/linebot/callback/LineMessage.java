package com.zygen.linebot.callback;

public class LineMessage {
    private String id;
    private String header;
    private String body;
    public String getId() {
        return id;
    }

    public void setId(String newId) {
        this.id = newId;
    }

    public String getHeader() {
        return this.header;
    }

    public void setHeader(String newHeader) {
        this.header = newHeader;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String newBody) {
        this.body = newBody;
    }
}
