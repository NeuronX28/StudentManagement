package com.example.studentmanagement;

public class Classes {
    private String classId;        // Add document ID field
    private String className_class;
    private String section;

    // Constructor
    public Classes(String classId, String className_class, String section) {
        this.classId = classId;    // Initialize document ID
        this.className_class = className_class;
        this.section = section;
    }

    public String getClassId() {
        return classId;             // Getter for document ID
    }

    public String getClassName_class() {
        return className_class;
    }

    public String getSection() {
        return section;
    }
}
