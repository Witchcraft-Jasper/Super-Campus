package com.example.campusapp.util;

public class ResponseCode {
    public static final int LOAD_SUCCESS = 1001;
    public static final int LOAD_FAILED = 1002;

    public static final int SIGN_IN_SUCCESS = 2000;
    public static final int SIGN_UP_SUCCESS = 2001;
    public static final int UPDATE_SUCCESS = 2002;
    public static final int DELETE_SUCCESS = 2003;
    public static final int QUERY_SUCCESS = 2004;
    public static final int CODE_SUCCESS = 2005;

    public static final int SIGN_IN_FAILED = 3000;
    public static final int SIGN_UP_FAILED = 3001;
    public static final int UPDATE_FAILED = 3002;
    public static final int DELETE_FAILED = 3003;
    public static final int QUERY_FAILED = 3004;
    public static final int SEND_FAILED = 3005;
    public static final int WRONG_CODE = 3006;

    public static final int EMPTY_RESPONSE = 4000;
    public static final int SERVER_ERROR = 4001;
    public static final int REQUEST_FAILED = 4002;
    public static final int JSON_SERIALIZATION = 4003;
    public static final int EXIT_SUCCESS = 4004;
    public static final int UNCHANGED_INFORMATION = 4005;
    public static final int EMPTY_INFO = 4006;
    public static final int WRONG_PASS = 4007;
}
