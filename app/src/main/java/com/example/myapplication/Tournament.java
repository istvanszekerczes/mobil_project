package com.example.myapplication;

import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.ArrayList;

public class Tournament {
    private String documentId;
    private String name;
    private String description;
    private String organiser;
    private String startDate;
    private String endDate;
    private String location;
    private int numberOfTeams;
    private int maxNumberOfTeams;
    private ArrayList<String> registeredTeams;

    public Tournament(String name, String location, String startDate, String endDate,
                      String organiser, String info, int maxNumberOfTeams) {
        this.name = name;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organiser = organiser;
        this.description = info;
        this.maxNumberOfTeams = maxNumberOfTeams;
    }

    public Tournament(String documentId, String name, String location, String startDate, String endDate, String description) {
        this.documentId = documentId;
        this.name = name;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }
    public Tournament() {
    }

        public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxNumberOfTeams() {
        return maxNumberOfTeams;
    }

    public void setMaxNumberOfTeams(int maxNumberOfTeams) {
        this.maxNumberOfTeams = maxNumberOfTeams;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public void setNumberOfTeams(int numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getRegisteredTeams() {
        return registeredTeams;
    }

    public void setRegisteredTeams(ArrayList<String> registeredTeams) {
        this.registeredTeams = registeredTeams;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
