package com.example.covid;

public class NonSuspectUsers {
    public String userName, contact, address, city, state, country, aadhaar;
    public String latitude, longitude;

    public NonSuspectUsers(){

    }

    public NonSuspectUsers(String userName, String contact, String address, String city, String state, String country, String aadhaar, String latitude, String longitude) {
        this.userName = userName;
        this.contact = contact;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.aadhaar = aadhaar;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude(){
        return latitude;
    }
    public void setLatitude(String latitude){this.latitude = latitude;}

    public String getLongitude(){
        return longitude;
    }
    public void setLongitude(String longitude){this.longitude = longitude;}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }
}
