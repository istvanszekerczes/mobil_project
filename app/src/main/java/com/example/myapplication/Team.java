package com.example.myapplication;

import java.util.ArrayList;

public class Team {
    private String teamName;
    private ArrayList<String> playerNames;

    public Team(String teamName, ArrayList<String> playerNames) {
        this.teamName = teamName;
        this.playerNames = playerNames;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public ArrayList<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(ArrayList<String> playerNames) {
        this.playerNames = playerNames;
    }
}
