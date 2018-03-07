package com.giaothuy.ebookone.model;

/**
 * Created by 1 on 3/7/2018.
 */

public class ServerResponse {

    private String message;
    private boolean status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
