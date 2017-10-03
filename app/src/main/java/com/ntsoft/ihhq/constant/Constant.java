package com.ntsoft.ihhq.constant;

import android.os.Environment;

/**
 * Created by Administrator on 2/9/2016.
 */
public class Constant {

    public static String MEDIA_PATH = Environment.getExternalStorageDirectory().toString() + "/HHQ";
    //////////string parameters

    public static String INDECATOR = "HHQ Touch";
    public static String ANDROID = "android";
    public static String DEVICE_TYPE = "device_type";
    public static String DEVICE_TOKEN = "device_token";
    public static String DEVICE_ID = "device_id";

    public static String USER_ID = "user_id";
    public static String EMAIL = "email";
    public static String PASSWORD = "password";
    public static String FULLNAME = "fullname";
    public static String AVATAR = "avatar";

    public static double cornerRadius5 = 5.0;

    public static String[] arrDocType = {"pdf", "docx", "xls"};
    public static String[] arrDocSortBy = {"Date", "Name"};
    public static String[] arrTicketSortBy = {"Date", "Subject"};
    public static String[] arrLogisticStatus = {"Delivered", "Received", "Return"};
//    public static String[] arrPaymentStatus = {"REQUEST", "RECEIVED", "BANK DEPOSIT", "DUE NOW"};
    public static String[] arrPaymentStatus = {"Outstanding", "Paid", "Processing", "Processing"};
    public static String[] arrUserRoles = {"admin", "staff", "lawyer", "billing", "logistic", "client", "spectator"};
    public static String[] arrBillplzMethod = {"bank", "billplz"};
    public static String[] arrPaymentMethod = {"Upload Receipt", "Online Banking"};
}
