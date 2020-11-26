package com.example.awesomehabit.database.running;

import android.content.Context;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.awesomehabit.database.Habit;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfMeasurement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "running_table")
public class Run extends Habit {

    // distance theo met
    @ColumnInfo(name = "Distance")
    public int distance = 0;

    // timeStampStart ở đây là lấy luôn ngày tháng năm, giờ phút giây tại thời điểm chạy luôn
    // Một ngày chạy nhiều lần được
    // tinh theo miliseconds
//    @ColumnInfo(name = "timeStampStart")
//    public long timeStampStart = 0L;
    @ColumnInfo(name = "timeStart")
    public String timeStart ;

    // dung de ve ra quang duong
    @ColumnInfo(name = "Route")
    public String routeID;

    // thoi gian chay
    // tinh theo giay
    @ColumnInfo(name = "runningTime")
    public long runningTime;

//    public Run(int distance, long timeStampStart, long runningTime) {
//        this.distance = distance;
//        this.timeStampStart = timeStampStart;
//        this.routeID = String.valueOf(timeStampStart);
//        this.runningTime = runningTime;
//    }
    public Run(int distance, String timeStart,Calendar time, long runningTime, String routeID) {
        super(Habit.TYPE_RUN);
        this.distance = distance;
        this.timeStart = timeStart;
        this.routeID = routeID;
        this.runningTime = runningTime;
        this.time = time;
    }

//    public Run(int distance, String timeStart, long runningTime, String routeID) {
//        super(Habit.TYPE_RUN);
//        this.distance = distance;
//        this.timeStart = timeStart;
//        this.routeID = routeID;
//        this.runningTime = runningTime;
//    }


    //Tra ve speed voi toc do la km/h
    public List<Double> getSpeeds(Context context){
        String routeString = readFromFile(context, routeID);
        List<Point> points = getRouteFromString(routeString);
        List<Double> speeds = new ArrayList<>();
        speeds.add(0.);
        int n = points.size();

        for (int i=0 ; i < n-1 ; i++  ){
            speeds.add(TurfMeasurement.distance(points.get(i), points.get(i+1))*1800);
        }
        return speeds;
    }


    private List<Point> getRouteFromString(String route){
        List<Point> points = new ArrayList<>();
        String[] arrOfStr = route.split(" ");
        for (String str : arrOfStr ){
            String[] corr = str.split("@");
            points.add(Point.fromLngLat(Double.valueOf(corr[0]), Double.valueOf(corr[1])));
        }
        return points;
    }

    public List<Point> getRoute(Context context){
        String routeString = readFromFile(context, routeID);
        List<Point> points = getRouteFromString(routeString);
        return points;
    }

     private  String fromDate(Date date){
        return date == null ? null : new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(date);
    }

    private Date toDate(String daystring) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        return daystring == null ? null: simpleDateFormat.parse(daystring);
    }

    public String getDayString(){
        return timeStart;
    }

    private String readFromFile(Context context,String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("database", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("database", "Can not read file: " + e.toString());
        }

        return ret;
    }

}

