package com.ntsoft.ihhq.utility;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 1/4/2016.
 */
public class StringUtility {

    public static CharSequence trimTrailingWhitespace(CharSequence source) {

        if(source == null)
            return "";

        int i = source.length();

        // loop back to the first non-whitespace character
        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        return source.subSequence(0, i+1);
    }

    public static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }
    public static String[] spliteStringByComma(String str) {
        String[] strings = str.split(", ");
        return strings;
    }
    public static ArrayList<String> spliteStringByCommaReturnArray (String str) {
        String[] strings = str.split(", ");

        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < strings.length; i ++) {
            arrayList.add(strings[i]);
        }
        return arrayList;
    }
    //convert string to emoji
    public static String getEmoji(String str) {
        String strKey = "\\ud83d";
        while(str.contains(strKey)) {

            int start = str.indexOf("\\ud83d");
            String str3 = str.substring(start , start + 12);
            ///get emoji from string
            String str4 = getUnicode(str3);
            //replace string with emoji
            String result = str.replace(str3, str4);
            str = result;

        }
        return str;
    }
    public static String getUnicode(String myString) {
        String str = myString.split(" ")[0];
        str = str.replace("\\","");
        String[] arr = str.split("u");
        String text = "";
        for(int i = 1; i < arr.length; i++){
            int hexVal = Integer.parseInt(arr[i], 16);
            text += (char)hexVal;
        }
        return text;
    }
    //convert emoji to string
    public static String escapeJavaString(String st){
        StringBuilder builder = new StringBuilder();
        try {
            for (int i = 0; i < st.length(); i++) {
                char c = st.charAt(i);
                if(!Character.isLetterOrDigit(c) && !Character.isSpaceChar(c)&& !Character.isWhitespace(c) ){
                    String unicode = String.valueOf(c);
                    int code = (int)c;
                    if(!(code >= 0 && code <= 255)){
                        unicode = "\\u"+Integer.toHexString(c);
                    }
                    builder.append(unicode);
                }
                else{
                    builder.append(c);
                }
            }
            Log.i("Unicode Block", builder.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return builder.toString();
    }
    ////seperate string by comma (",")
    private ArrayList<String> getAmenities(String strTag) {
        ArrayList<String> arrString = new ArrayList<String >();
        String strKey = ",";
        int first = 0;
        int cutNum = 0;
        do{
            int second = strTag.indexOf(strKey);
            int length = strTag.length();
            if(second == -1){

                second = length;
                cutNum = second;
            }else{
                cutNum = second + 1;
            }
            String str3 = strTag.substring(first, second);
            arrString.add(str3);
//            first = 1;
            strTag = strTag.substring(cutNum, length);
        }while (strTag.contains(strKey));
        if(!strTag.isEmpty()){
            arrString.add(strTag);
        }

        return arrString;
    }
    public final static boolean isValidCharacter (String txtInput){
        Pattern pattern;
        Matcher matcher;

        final String USERNAME_PATTERN = "^[a-z0-9A-Z]{2,25}$";
        pattern = Pattern.compile(USERNAME_PATTERN);

        matcher = pattern.matcher(txtInput);
        return matcher.matches();
    }

    public static String getFileSize(int byteSize) {
        if (byteSize < 1024) {
            return String.valueOf(byteSize) + "b";
        } else if (byteSize < (1024 * 1024)) {
            double kb = round(byteSize / 1024, 1);
            return  String.valueOf(kb) + "Kb";
        } else {
            double mb = round(byteSize / 1024 / 1024, 1);
            return  String.valueOf(mb) + "Mb";
        }
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
