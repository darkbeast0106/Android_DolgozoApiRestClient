package com.darkbeast0106.dolgozoapirest.core;

import java.util.List;

public abstract class ApiValasz<T> {
    private boolean error;
    private String message;
    private List<T> adatok;

    public ApiValasz(boolean error, String message, List<T> adatok) {
        this.error = error;
        this.message = message;
        this.adatok = adatok;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getAdatok() {
        return adatok;
    }

    public void setAdatok(List<T> adatok) {
        this.adatok = adatok;
    }
}
