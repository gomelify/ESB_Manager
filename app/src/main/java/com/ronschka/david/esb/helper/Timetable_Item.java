package com.ronschka.david.esb.helper;

import android.content.Context;

import com.ronschka.david.esb.R;

public class Timetable_Item {
    private String lesson;
    private String room;
    private int hours;
    private String color;
    private String teacher;
    private String lessonFull;
    private Context context;

    public Timetable_Item(String lesson, String room, String teacher, int hours, Context context) {
        this.hours = hours;
        this.lesson = lesson;
        this.room = room;
        this.teacher = teacher;
        this.context = context;
        lessonFull = lessonList(lesson);
    }

    public String getLesson() {
        return lesson;
    }

    public String getLessonFull() { return lessonFull;}

    public String getRoom() {
        return room;
    }

    public int getHours() {
        return hours;
    }

    public String getColor() {
        return color;
    }

    public String getTeacher(){return teacher;}

    private String lessonList(String entry){
        switch (entry){
            case "M":
                color = context.getResources().getString(0 + R.color.MaterialIndigo2);
                return "Mathe";
            case "D":
                color = context.getResources().getString(0 + R.color.MaterialRed1);
                return "Deutsch";
            case "E":
                color = context.getResources().getString(0 + R.color.MaterialGreen1);
                return "Englisch";
            case "SP":
                color = context.getResources().getString(0 + R.color.MaterialCyan1);
                return "Sport";
            case "PHY":
                color = context.getResources().getString(0 + R.color.MaterialTeal1);
                return "Physik";
            case "INF":
                color = context.getResources().getString(0 + R.color.MaterialAmber);
                return "Informatik";
            case "TI":
                color = context.getResources().getString(0 + R.color.MaterialBlue);
                return "Technische Informatik";
            case "S":
                color = context.getResources().getString(0 + R.color.MaterialPink2);
                return "Spanisch";
            case "Veranstaltung":
                color = context.getResources().getString(0 + R.color.standardEvent);
                return "Veranstaltung";
            default:
                color = context.getResources().getString(0 + R.color.Grey);
                return entry;
        }
    }
}
