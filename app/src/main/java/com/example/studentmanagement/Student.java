package com.example.studentmanagement;

public class Student {
    private String roll;
    private String name;
    private String className;
    private String program;

    // Constructor
    public Student(String roll, String name, String className, String program) {
        this.roll = roll;
        this.name = name;
        this.className = className;
        this.program = program;
    }

    // Getters
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
