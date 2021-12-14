package com.darkbeast0106.dolgozoapirest;

public final class ApiUrls {
    private ApiUrls(){}

    public static final String BASE_URL = "http://darkbeast0106.ddns.net/";

    public static final String API_URL = BASE_URL+"/api";

    public final class DolgozoUrls {
        private DolgozoUrls(){}

        public static final String GET_ALL = API_URL+"/dolgozo";
        public static final String GET_ONE = API_URL+"/dolgozo/%d";
        public static final String POST = API_URL+"/dolgozo";
        public static final String PUT = API_URL+"/dolgozo/%d";
        public static final String DELETE = API_URL+"/dolgozo/%d";

    }
}
