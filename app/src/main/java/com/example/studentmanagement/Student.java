package com.example.studentmanagement;

public class Student {
    private String roll;
    private String name;
    private String className;
    private String program;

    public Student(String roll, String name, String className, String program) {
        this.roll = roll;
        this.name = name;
        this.className = className;
        this.program = program;
    }

    public String getRoll() {
        return roll;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public String getProgram() {
        return program;
    }
}
