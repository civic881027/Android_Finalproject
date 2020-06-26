package com.example.android_finalproject.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class dbProvider extends ContentProvider {

    private dbOpenHelper dbOpenHelper;

    private static final UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    static{
        mUriMatcher.addURI(ContractLocation.AUTHORITY,ContractLocation.LOCATIONS,1);
        mUriMatcher.addURI(ContractLocation.AUTHORITY,ContractLocation.NEARBY+"/#",2);
        mUriMatcher.addURI(ContractLocation.AUTHORITY,ContractLocation.LOCATION,3);
        mUriMatcher.addURI(ContractLocation.AUTHORITY,ContractLocation.LOCATION+"/#",4);
    }



    @Override
    public boolean onCreate() {
        this.dbOpenHelper=new dbOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        //dbOpenHelper.onUpgrade(db,1,1);//resetDB
        Cursor mCursor=null;
        switch (mUriMatcher.match(uri)) {
            case 1:
                mCursor = db.query(ContractLocation.Location.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                mCursor.moveToFirst();
                break;
            case 2:
                mCursor = db.query(ContractLocation.Location.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                if (mCursor != null) {
                    Log.d("test","query case2");
                    selection = ContractLocation.Location.ID + " = " + uri.getLastPathSegment();
                    Cursor dataCursor = db.query(ContractLocation.Location.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                    dataCursor.moveToFirst();
                    double mainLong = dataCursor.getDouble(dataCursor.getColumnIndex(ContractLocation.Location.LONG));
                    double mainLat = dataCursor.getDouble(dataCursor.getColumnIndex(ContractLocation.Location.LAT));

                    double tLong = 0;
                    double tLat = 0;
                    double min = Double.MAX_VALUE;
                    Double distance=0.0;
                    int index = 0;
                    while (mCursor.moveToNext()) {
                        tLong = mCursor.getDouble(dataCursor.getColumnIndex(ContractLocation.Location.LONG));
                        tLat = mCursor.getDouble(dataCursor.getColumnIndex(ContractLocation.Location.LAT));
                        distance=getDistance(mainLong, mainLat, tLong, tLat);
                        if (distance < min && distance!=0) {
                            index=mCursor.getPosition();
                            min=getDistance(mainLong, mainLat, tLong, tLat);
                        }
                    }
                    mCursor.moveToPosition(index);
                    break;
                }
            default:
                throw new IllegalArgumentException("Unknown URI:"+uri);

        }
        mCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return  mCursor;
    }



    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        if(mUriMatcher.match(uri)!=3){
            throw new IllegalArgumentException("Unknown URI:"+uri);
        }
        long rowID=db.insert(ContractLocation.Location.TABLE_NAME,null,values);
        if(rowID>0){
            Uri uriLocation= ContentUris.withAppendedId(uri,rowID);
            getContext().getContentResolver().notifyChange(uriLocation,null);
            return  uriLocation;
        }
        throw new IllegalArgumentException("Unknown URI:"+uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        int count=0;

        if(mUriMatcher.match(uri)==4){
            String rowId = uri.getPathSegments().get(1);

            count=db.delete(ContractLocation.Location.TABLE_NAME,ContractLocation.Location.ID+"="+rowId+(!TextUtils.isEmpty(selection)?" AND ("+selection+")":""),selectionArgs);
            getContext().getContentResolver().notifyChange(uri,null);
            return count;
        }
        throw new IllegalArgumentException("UnknownURI:"+uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public double getDistance(double longitude1,double latitude1,double longitude2,double latitude2){
        double LAT1=rad(latitude1);
        double LAT2=rad(latitude2);
        double a=LAT1-LAT2;
        double b=rad(longitude1)-rad(longitude2);
        double s=2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(LAT1)*Math.cos(LAT2)*Math.pow(Math.sin(b/2),2)));
        s=s*6371000;
        s=Math.round(s*1000)/1000;
        return s;

    }


    private  static double rad(double d){
        return d*Math.PI/180.0;
    }

}
