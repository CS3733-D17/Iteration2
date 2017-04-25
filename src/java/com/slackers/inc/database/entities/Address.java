/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

import java.io.Serializable;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 *
 *     Holds the address of any user. Contains information about the user's
 *     line 1, line 2, city, state, zip code, and country.
 */
public class Address implements Serializable{
    private String line1;
    private String line2;
    private String city;
    private String state;
    private int zipCode;
    private String country;

    public Address(String line1, String line2, String city, String state, int zipCode, String country) {
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }
    public Address()
    {
        this("","","","",-1,"");
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
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

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        if (this.zipCode!=-1)
            return line1 + "\n" + line2 + "\n" + city + " " + state + ", " + String.format("%05d", zipCode);
        else
            return "";
    }
    
    public static Address tryParse(String addressBox)
    {
        try
        {
            String[] lines = addressBox.split("\n");
            System.out.println(lines.length);
            if (lines.length!=3)
                return null;

            int zipBreak = lines[2].lastIndexOf(" ");

            String zipCode = lines[2].substring(zipBreak).trim();
            if (zipCode.contains("-"))
            {
                zipCode = zipCode.substring(0, zipCode.indexOf("-"));
            }
            zipCode = zipCode.trim();

            String cityAndState = lines[2].substring(0,zipBreak).trim();

            String state = cityAndState.substring(cityAndState.lastIndexOf(" ")).trim();
            state = state.replace(",", "").trim();

            String city = cityAndState.substring(0, cityAndState.lastIndexOf(" ")).trim();
            int zip=0;
            zip = Integer.parseInt(zipCode);
            String country = "";
            return new Address(lines[0].trim(), lines[1].trim(), city, state, zip, country);
        }
        catch (Exception e)
        {
            return null;
        }        
    }
}
