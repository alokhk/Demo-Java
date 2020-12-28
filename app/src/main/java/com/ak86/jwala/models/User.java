package com.ak86.jwala.models;

public class User {

    private String username;
    private String emailId;
    private int userLevel;

    public User(){

    }

    public User(String username, String emailId, int userLevel){
        this.username = username;
        this.emailId = emailId;
        this.userLevel = userLevel;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getLevel(){
        if(this.userLevel==0){
            return "GUEST";
        } else if(this.userLevel==1){
            return "NCO/JCO";
        } else if(this.userLevel ==2){
            return "QUARANTINE_OC";
        } else if(this.userLevel ==3){
            return "ADJT";
        } else if(this.userLevel ==4){
            return "COMDT";
        }else if(this.userLevel ==5){
            return "ADMINISTRATOR";
        }
        return "";
    }

    public void setLevel(String level){
        if(level.equals("GUEST")){
            this.userLevel = 0;
        } else if(level.equals("NCO/JCO")){
            this.userLevel = 1;
        } else if(level.equals("QUARANTINE_OC")){
            this.userLevel = 2;
        } else if(level.equals("ADJT")){
            this.userLevel = 3;
        } else if(level.equals("COMDT")){
            this.userLevel = 4;
        } else if(level.equals("ADMINISTRATOR")){
            this.userLevel = 5;
        }
    }

}
