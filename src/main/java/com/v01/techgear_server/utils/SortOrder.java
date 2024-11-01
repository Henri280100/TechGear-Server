package com.v01.techgear_server.utils;

public class SortOrder {
    private String field;
    private boolean ascending;

    public SortOrder() {
    }

    public SortOrder(String field, boolean ascending) {
        this.field = field;
        this.ascending = ascending;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

}