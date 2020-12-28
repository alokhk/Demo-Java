package com.ak86.jwala.models;

import java.util.Calendar;
import java.util.Date;

public class Person {

    private String rank;
    private String name;
    private String company;
    private Date fromDate, toDate, isolationStartDate, isolationEndDate,dateOfPositiveResult, dateOfNextTest, dateOfNegativeResult;
    private Boolean isolation;
    private String dbKey = null;
    private Boolean isNowPositive = false;
    private String currentLocation = null;
    private Boolean recovered = false;

    public Person(){

    }

    public Person(String rank, String name, String company, Date fromDate, Date toDate, Date isolationStartDate, Date isolationEndDate,
                  Date dateOfPositiveResult, Date dateOfNextTest, Date dateOfNegativeResult) {
        this.rank = rank;
        this.name = name;
        this.company = company;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.isolationStartDate = fromDate;
        this.isolationEndDate = toDate;
        this.dateOfPositiveResult = dateOfPositiveResult;
        this.dateOfNextTest = dateOfNextTest;
        this.dateOfNegativeResult = dateOfNegativeResult;
        this.isolation = false;
        this.isNowPositive=false;
        this.currentLocation="Please Update";
        this.dbKey="";
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Boolean getIsolation() {
        return isolation;
    }

    public void setIsolation(Boolean isolation) {
        this.isolation = isolation;
    }

    public Date getIsolationStartDate() {
        return isolationStartDate;
    }

    public void setIsolationStartDate(Date isolationStartDate) {
        this.isolationStartDate = isolationStartDate;
    }

    public Date getIsolationEndDate() {
        return isolationEndDate;
    }

    public void setIsolationEndDate(Date isolationEndDate) {
        this.isolationEndDate = isolationEndDate;
    }

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public Date getDateOfPositiveResult() {
        return dateOfPositiveResult;
    }

    public void setDateOfPositiveResult(Date dateOfPositiveResult) {
        this.dateOfPositiveResult = dateOfPositiveResult;
    }

    public Date getDateOfNextTest() {
        return dateOfNextTest;
    }

    public void setDateOfNextTest(Date dateOfNextTest) {
        this.dateOfNextTest = dateOfNextTest;
    }

    public Date getDateOfNegativeResult() {
        return dateOfNegativeResult;
    }

    public void setDateOfNegativeResult(Date dateOfNegativeResult) {
        this.dateOfNegativeResult = dateOfNegativeResult;
    }

    public Boolean getNowPositive() {
        return isNowPositive;
    }

    public void setNowPositive(Boolean nowPositive) {
        isNowPositive = nowPositive;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Boolean getRecovered() {
        return recovered;
    }

    public void setRecovered(Boolean recovered) {
        this.recovered = recovered;
    }

}
