package com.ntsoft.ihhq.constant;

/**
 * Created by dell17 on 4/15/2016.
 */
public class API {

//    public static String BASE_URL = "http://13.228.25.27/";
    public static String BASE_URL = "http://hhqtouch.com.my/";
    //    public static String BASE_URL = "http://192.168.3.200/iHHQ/public/";
    public static String BASE_API_URL = BASE_URL + "api/v1/";
    public static String BASE_IMAGE_URL = BASE_URL + "upload/avatars/";
    public static String BASE_FILE_URL = BASE_URL + "api/v1/files/documents/";
    public static String EMPOWER_URL = "https://hhq.com.my/empower/";

    public static String REGISTER = BASE_URL + "register";
    public static String TERMS_AND_POLICY = "https://hhq.com.my/terms-of-use-for-hhq-touch/";
    public static String LOGIN = BASE_API_URL + "users/me";
    public static String FORGOT_PASSWORD = BASE_API_URL + "users/me/password";
    public static String LOGOUT = BASE_API_URL + "users/me/logout";
    public static String GET_FILES = BASE_API_URL + "files?per_page=20";
    public static String GET_LOGISTICS = BASE_API_URL + "dispatches";
    public static String GET_LOGISTICS_MILESTONES = BASE_API_URL + "files/%d/milestoneslogistics";
    public static String GET_TICKETS = BASE_API_URL + "tickets?per_page=20";
    public static String GET_TICKETS_OPEN = BASE_API_URL + "tickets/open?per_page=20";
    public static String GET_TICKETS_CLOSED = BASE_API_URL + "tickets/close?per_page=20";
    public static String GET_TICKETS_PENDING = BASE_API_URL + "tickets/pending?per_page=20";
    public static String CREAT_NEW_TICKET = BASE_API_URL + "tickets";
    public static String GET_TICKET_MESSAGE = BASE_API_URL + "tickets/";
    public static String POST_TICKET_MESSAGE = BASE_API_URL + "tickets/";
    public static String GET_TICKET_CATEGORY = BASE_API_URL + "tickets/categories";
    public static String GET_FILE_REFS = BASE_API_URL + "users/me/files/file-refs";
    public static String GET_NOTIFICATIONS = BASE_API_URL + "notifications";
    public static String SUBMIT_QR_CODE = BASE_API_URL + "dispatches/scan";
    public static String UPDATE_PROFILE = BASE_API_URL + "users/me/setting";
    public static String ENABLE_NOTIFICATION = BASE_API_URL + "enable_notification";
    public static String CHANGE_PASSWORD = BASE_API_URL + "users/me/change_password";
    public static String UPLOAD_NEW_DOCUMENT = BASE_API_URL + "files/%d/documents";
    public static String CREATE_BILL = BASE_API_URL + "billplzpayment";
    public static String CHECK_BILLING = BASE_API_URL + "requestbillplz";
    public static String UPLOAD_RECEIPT = BASE_API_URL + "bankpayment";

    public static String DOWNLOAD_INVOICE = BASE_API_URL + "files/invoice/%d/download";
    public static String DOWNLOAD_SLIP = BASE_API_URL + "files/slip/%d/download";
    public static String DOWNLOAD_RECEIPT = BASE_API_URL + "files/receipt/%d/download";
}
