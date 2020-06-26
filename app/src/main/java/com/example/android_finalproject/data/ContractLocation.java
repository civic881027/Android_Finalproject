package com.example.android_finalproject.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ContractLocation {
    public static final String AUTHORITY="com.example.android_finalproject";

    public static final String LOCATIONS="/locations";
    public static final String NEARBY="/nearby";
    public static final String LOCATION="/location";

    public static final Uri CONTENT_URI=Uri.parse("content://"+AUTHORITY+LOCATIONS);

    public static final String DATABASE_NAME="testDB";
    public static final int DATABASE_VERSION=1;

    public static class Location implements BaseColumns{

        private Location(){}
        public static final String TABLE_NAME="locations";

        public static final String ID="_ID";
        public static final String LONG="longitude";
        public static final String LAT="latitude";
        public static final String NAME="name";


    }





}
