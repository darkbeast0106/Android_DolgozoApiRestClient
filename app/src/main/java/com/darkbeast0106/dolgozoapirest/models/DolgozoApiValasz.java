package com.darkbeast0106.dolgozoapirest.models;

import com.darkbeast0106.dolgozoapirest.core.ApiValasz;

import java.util.List;

public class DolgozoApiValasz extends ApiValasz<Dolgozo> {
    public DolgozoApiValasz(boolean error, String message, List<Dolgozo> adatok) {
        super(error, message, adatok);
    }
}
