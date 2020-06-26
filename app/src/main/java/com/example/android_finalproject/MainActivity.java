package com.example.android_finalproject;

import android.content.ContentValues;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_finalproject.data.dbOpenHelper;
import com.example.android_finalproject.data.ContractLocation;


public class MainActivity extends AppCompatActivity {
    private LocationListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private EditText mNewlongitudeEditText;
    private EditText mNewlatitudeEditText;
    private EditText mNewNameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView locationRecyclerView;
        locationRecyclerView=(RecyclerView)findViewById(R.id.rv_location);
        mNewlongitudeEditText=(EditText)findViewById(R.id.edit_Longitude);
        mNewlatitudeEditText=(EditText)findViewById(R.id.edit_Latitude);
        mNewNameEditText=(EditText)findViewById(R.id.edit_Name);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Cursor cursor=getAllLocation();
        mAdapter=new LocationListAdapter(this,cursor);
        mAdapter.setOnItemClickListener(new LocationListAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Uri uri=Uri.parse("content://"+ContractLocation.AUTHORITY+ContractLocation.NEARBY+"/"+v.getTag().toString());
                Cursor answer=getContentResolver().query(uri,null,null,null,null,null);
                if(answer!=null){
                    Toast.makeText(getApplicationContext(),"NearByPoint"+String.valueOf(answer.getString(0)),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(int position, View v) {
                TextView tvLong=(TextView)v.findViewById(R.id.tv_Lon);
                TextView tvLat=(TextView)v.findViewById(R.id.tv_Lat);
                findInMap(Double.parseDouble(tvLong.getText().toString()),Double.parseDouble(tvLat.getText().toString()));
            }
        });

        locationRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                                long id = (long) viewHolder.itemView.getTag();
                                int count=removeLocation(id);
                                if(count>0){
                                    Toast.makeText(getApplicationContext(),"Delete count:"+String.valueOf(count),Toast.LENGTH_SHORT).show();
                                    mAdapter.swapCursor(getAllLocation());
                                }

            }
        }).attachToRecyclerView(locationRecyclerView);
    }

    public void addToLocationList(View view) {
        if (mNewNameEditText.getText().length() == 0 ||
                mNewlatitudeEditText.getText().length() == 0 ||
                mNewlongitudeEditText.getText().length() ==0) {
            return;
        }
        Double longitude=0.0, latitude=0.0;
        longitude=Double.parseDouble(mNewlongitudeEditText.getText().toString());
        latitude=Double.parseDouble(mNewlatitudeEditText.getText().toString());
        System.out.println(longitude);
        System.out.println(latitude);
        addNewLocation(longitude, latitude,mNewNameEditText.getText().toString());

        mAdapter.swapCursor(getAllLocation());

        mNewNameEditText.clearFocus();
        mNewlongitudeEditText.getText().clear();
        mNewlatitudeEditText.getText().clear();
        mNewNameEditText.getText().clear();
    }

    private boolean addNewLocation(double longitude, double latitude,String name) {
        ContentValues cv = new ContentValues();
        cv.put(ContractLocation.Location.LONG, longitude);
        cv.put(ContractLocation.Location.LAT,latitude);
        cv.put(ContractLocation.Location.NAME,name);
        System.out.println(cv.toString());
        Uri uri=Uri.parse("content://"+ContractLocation.AUTHORITY+ContractLocation.LOCATION);
        Uri mUri=getContentResolver().insert(uri,cv);
        if(mUri!=null){
            Toast.makeText(this,"Successfully insert uri:"+mUri.toString(),Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }


    private Cursor getAllLocation() {
        Uri CONTENT_URI=ContractLocation.CONTENT_URI;
        Cursor cursor=getApplicationContext().getContentResolver().query(CONTENT_URI,null,null,null,null);
        if(cursor!=null){
            Log.d("test","cursor!");
            return cursor;
        }
        return null;
    }

    private int removeLocation(long id) {
        Uri uri=Uri.parse("content://"+ContractLocation.AUTHORITY+ContractLocation.LOCATION+"/"+String.valueOf(id));
        int delete=getContentResolver().delete(uri,null,null);
        return delete;
    }

    public void findInMap(Double longitude,Double latitude){
        Uri intentURI=Uri.parse("https://www.google.com/maps/search/?api=1&query="+String.valueOf(longitude)+","+String.valueOf(latitude));
        Intent mapIntent=new Intent(Intent.ACTION_VIEW,intentURI);
        startActivity(mapIntent);
    }
}
