package com.example.remindme;

public class TaskDataModel {
    private String name;
    private String category;
    private String desc;

    public TaskDataModel(String name, String category, String desc) {
        this.name = name;
        this.category = category;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
