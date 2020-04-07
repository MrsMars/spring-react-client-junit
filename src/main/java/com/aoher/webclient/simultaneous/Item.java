package com.aoher.webclient.simultaneous;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class Item {

    private int id;

    @JsonbCreator

    public Item(@JsonbProperty("id") int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
