package com.meeting.entitiy;

public class Topic {
    Long id;
    String name;

    public Topic(Long id, String topic) {
        this.id = id;
        this.name = topic;
    }

    public Topic() {
    }

    public Topic(String topic) {
        this.name = topic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
