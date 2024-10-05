package com.example.studentmanagement;

public class Classes {
    private String classId;        // Add document ID field
    private String className_class;

    // Constructor
    public Classes(String classId, String className_class) {
        this.classId = classId;    // Initialize document ID
        this.className_class = className_class;
    }

    public String getClassId() {
        return classId;             // Getter for document ID
    }

    public String getClassName_class() {
        return className_class;
    }
}
