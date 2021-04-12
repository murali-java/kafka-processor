package com.kafkaprocessor.kafkaprocessor;

import java.util.stream.*;
import java.util.*;

public class TestResults
{
    public static class Student implements Comparable<Student>{
        private String name;
        private int score;

        public Student(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public int getScore() {
            return score;
        }

        public String getName() {
            return name;
        }

        @Override
        public int compareTo(Student student) {
            if(this.score > student.getScore()){
                return -1;
            }else if(this.score < student.getScore()){
                return 1;
            }
            return 0;
        }
    }

    public static List<String> studentsThatPass(Stream<Student> students, int passingScore) {
        //throw new UnsupportedOperationException("Waiting to be implemented.");
        return students.filter(s -> s.getScore() >= passingScore).sorted().map(Student::getName).collect(Collectors.toList());
    }

    public static void main(String[] args) {

        List<Student> students = new ArrayList<Student>();

        students.add(new Student("Mike", 80));
        students.add(new Student("James", 57));
        students.add(new Student("Alan", 21));

        studentsThatPass(students.stream(), 50).forEach(System.out::println);
    }
}
