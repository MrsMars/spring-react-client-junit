package com.aoher.webclient.simultaneous;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class User {

    private int id;

    @JsonbCreator

    public User(@JsonbProperty("id") int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
