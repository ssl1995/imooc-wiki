package com.jiawa.wiki.resp;

public class FileUploadResp {

    private boolean success;
    private String filename;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "FileUploadResp{" +
                "success=" + success +
                ", filename='" + filename + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
