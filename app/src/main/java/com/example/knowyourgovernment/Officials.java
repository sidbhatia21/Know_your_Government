package com.example.knowyourgovernment;

import java.io.Serializable;

public class Officials implements Serializable {

    private String party;
    private String photoURL;
    private String office;
    private String name;
    private String facebookID;
    private String twitterID;
    private String youtubeID;
    private String address;
    private String emailID;
    private String phoneNum;
    private String webURL;

    public Officials(String office, String name) {
        this.office = office;
        this.name = name;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getParty() {
        return this.party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailID() {
        return this.emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getPhoneNum() {
        return this.phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getWebURL() {
        return this.webURL;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    public String getPhotoURL() {
        return this.photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getFacebookID() {
        return this.facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public String getTwitterID() {
        return this.twitterID;
    }

    public void setTwitterID(String twitterID) {
        this.twitterID = twitterID;
    }

    public String getYoutubeID() {
        return this.youtubeID;
    }

    public void setYoutubeID(String youtubeID) {
        this.youtubeID = youtubeID;
    }
}
