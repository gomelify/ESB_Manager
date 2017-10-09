package com.ronschka.david.esb.helper;

public class Timetable_Item {
    private String lesson;
    private String room;
    private int hours;
    private String color;

    public Timetable_Item(String lesson, String room, int hours, String color) {
        this.hours = hours;
        this.lesson = lesson;
        this.room = room;
        this.color = color;
    }

    public String getLesson() {
        return lesson;
    }

    public String getRoom() {
        return room;
    }

    public int getHours() {
        return hours;
    }

    public String getColor() {
        return color;
    }
}
