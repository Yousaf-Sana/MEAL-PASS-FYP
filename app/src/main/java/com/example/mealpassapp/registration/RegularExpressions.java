package com.example.mealpassapp.registration;

import java.util.regex.Pattern;

public class RegularExpressions {

    private static Pattern mobilenumber=Pattern.compile("[0][3]\\d{2}\\s?\\d{7}");
    private static Pattern userName=Pattern.compile("[a-zA-Z]{3}[a-zA-Z\\\\s0-9]{5,20}");
    private static Pattern userName2=Pattern.compile("^[a-zA-Z\\s]+");
    private static Pattern businessName2=Pattern.compile("^[a-zA-Z\\s]+");
    private static Pattern businessName=Pattern.compile("[a-zA-Z]{3}[a-zA-Z\\\\s0-9]{8,25}");
    private static Pattern password=Pattern.compile("[a-zA-Z0-9\\\\W]{8,15}");
    private static Pattern cnic=Pattern.compile("[0-9]{5}[\\\\s-]?[0-9]{7}[\\\\s-]?[0-9]");


    public static boolean validateMobileNumber(String s){
        if(mobilenumber.matcher(s).matches()==true){
            return true;
        }
        else
            return false;
    }
    public static boolean validateUserName(String s){
        if(userName2.matcher(s).matches()==true){
            return  true;
        }
        else{
            return false;
        }
    }
    public static boolean validateBusinessName(String s){
        if(businessName2.matcher(s).matches()==true){
            return  true;
        }
        else{
            return false;
        }
    }
    public static boolean validatePassword(String s){
        if(password.matcher(s).matches()==true){
            return true;
        }
        else return false;
    }
    public static boolean validateCNIC(String s){
        if (cnic.matcher(s).matches()==true){
            return true;
        }else return false;
    }

}
