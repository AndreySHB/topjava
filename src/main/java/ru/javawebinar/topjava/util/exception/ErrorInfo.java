package ru.javawebinar.topjava.util.exception;

public class ErrorInfo {
    private final String url;
    private final String type;
    private final String detail;

    public ErrorInfo(CharSequence url, String type, String detail) {
        this.url = url.toString();
        this.type = type;
        this.detail = detail;
    }
}