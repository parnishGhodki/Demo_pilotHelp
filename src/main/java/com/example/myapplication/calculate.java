package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.StrictMath.abs;

public class calculate<Public> extends AppCompatActivity {

    ArrayList<Location> gps = new ArrayList<Location>();
    ArrayList<Long> speed = new ArrayList<Long>();
    ArrayList<Double> Distance = new ArrayList<Double>();
    ArrayList<Long> Time = new ArrayList<Long>();
    JSONArray runway ;

    ArrayList<Double> CalculatedTakeoffDistance = new ArrayList<Double>();
    ArrayList<Double> CalculatedLandingDistance = new ArrayList<Double>();

    double avgGroundLevel = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("Bundle");
        gps = (ArrayList<Location>)args.getSerializable("gps");
        speed = (ArrayList<Long>)args.getSerializable("speed");
        Distance = (ArrayList<Double>)args.getSerializable("distance");
        Time = (ArrayList<Long>)args.getSerializable("time");



        Toast.makeText(calculate.this,String.valueOf(gps.size()),Toast.LENGTH_SHORT).show();

        if(gps.size()> 10){
            avgGroundLevel = (float) ((gps.get(10).getAltitude() + gps.get(1).getAltitude() + gps.get(2).getAltitude() + gps.get(3).getAltitude() + gps.get(4).getAltitude() + gps.get(5).getAltitude() + gps.get(6).getAltitude() + gps.get(7).getAltitude() + gps.get(8).getAltitude() + gps.get(9).getAltitude())/10.0);

        }



    }

    public  void calculate(View view){

        calculateTakeoffLandingDistance();

        createFile();
        close();




    }

    public double calculateDistance(Location initialLocation,Location finalLocation){

        final int R = 6371;

        double latDistance = Math.toRadians(finalLocation.getLatitude() - initialLocation.getLatitude());
        double lonDistance = Math.toRadians(finalLocation.getLongitude() - initialLocation.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(initialLocation.getLatitude())) * Math.cos(Math.toRadians(finalLocation.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float distance = (float) (R * c * 1000);


        return distance;
    }




    public void close(){

        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        gps.clear();
        speed.clear();
        Distance.clear();
        Time.clear();



    }

    public void createFile(){

        Random random = new Random();
        int random1 = random.nextInt(1000);
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"Route" + random1 + ".kml");
        File textFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"Route" + random1 + ".txt");
        File speedFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"Speed" + random1 + ".txt");
        File runwayFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"Runway"+random1+".txt");
        File timeDistanceFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"TimeDistance"+random1+".txt");
        XmlSerializer xmlSerializer;
        String s;
        String v;
        String t;

        try {

            FileOutputStream textFos = new FileOutputStream(textFile);
            String distance = "TakeOff Distance = " + String.valueOf(CalculatedTakeoffDistance) + "Landing Distance = "+ String.valueOf(CalculatedLandingDistance);
            textFos.write(distance.getBytes());
            for(int i =0;i<gps.size()-1;i++){
                s = gps.get(i).getLongitude() + "," + gps.get(i).getLatitude() + "," + gps.get(i).getAltitude() + "\n";
                textFos.write(s.getBytes());
            }
            textFos.close();
            Log.i("1","File created");

            FileOutputStream speedFos = new FileOutputStream(speedFile);
            for (int j = 0;j<speed.size()-1;j++ ){
                v = speed.get(j) + "\n";
                speedFos.write(v.getBytes());

            }
            speedFos.close();
            Log.i("2","File created");

            FileOutputStream fos = new FileOutputStream(file);
            xmlSerializer = XmlPullParserFactory.newInstance().newSerializer();
            xmlSerializer.setOutput(fos, "UTF-8");
            xmlSerializer.startDocument(null, null);
            xmlSerializer.startTag(null, "kml");
            xmlSerializer.attribute(null, "xmlns","http://www.opengis.net/kml/2.2");
            xmlSerializer.startTag(null, "Document");
            xmlSerializer.startTag(null, "name");
            xmlSerializer.text("kmlFile");
            xmlSerializer.endTag(null, "name");
            xmlSerializer.startTag(null, "Style");
            xmlSerializer.attribute(null, "id", "M00230");
            xmlSerializer.startTag(null, "LineStyle");
            xmlSerializer.startTag(null, "width");
            xmlSerializer.text("10");
            xmlSerializer.endTag(null, "width");
            xmlSerializer.startTag(null, "color");
            xmlSerializer.text("7dff0000");
            xmlSerializer.endTag(null, "color");
            xmlSerializer.startTag(null, "colorMode");
            xmlSerializer.text("normal");
            xmlSerializer.endTag(null, "colorMode");
            xmlSerializer.endTag(null, "LineStyle");
            xmlSerializer.startTag(null, "PolyStyle");
            xmlSerializer.startTag(null, "colorMode");
            xmlSerializer.text("normal");
            xmlSerializer.endTag(null, "colorMode");
            xmlSerializer.startTag(null, "color");
            xmlSerializer.text("0000AA00");
            xmlSerializer.endTag(null, "color");
            xmlSerializer.endTag(null, "PolyStyle");
            xmlSerializer.endTag(null, "Style");
            xmlSerializer.startTag(null, "Style");
            xmlSerializer.attribute(null,"id","M00230");
            xmlSerializer.startTag(null,"LineStyle");
            xmlSerializer.startTag(null,"colorMode");
            xmlSerializer.text("normal");
            xmlSerializer.endTag(null,"colorMode");
            xmlSerializer.startTag(null,"color");
            xmlSerializer.text("B000AA00");
            xmlSerializer.endTag(null,"color");
            xmlSerializer.startTag(null,"width");
            xmlSerializer.text("10");
            xmlSerializer.endTag(null,"width");
            xmlSerializer.endTag(null,"LineStyle");
            xmlSerializer.startTag(null,"PolyStyle");
            xmlSerializer.startTag(null,"colorMode");
            xmlSerializer.text("normal");
            xmlSerializer.endTag(null,"colorMode");
            xmlSerializer.startTag(null,"color");
            xmlSerializer.text("4000AA00");
            xmlSerializer.endTag(null,"color");
            xmlSerializer.endTag(null,"PolyStyle");
            xmlSerializer.endTag(null,"Style");
            xmlSerializer.startTag(null,"Placemark");
            xmlSerializer.startTag(null,"visibility");
            xmlSerializer.text("1");
            xmlSerializer.endTag(null,"visibility");
            xmlSerializer.startTag(null,"styleUrl");
            xmlSerializer.text("#M10230");
            xmlSerializer.endTag(null,"styleUrl");
            xmlSerializer.startTag(null,"LineString");
            xmlSerializer.startTag(null,"extrude");
            xmlSerializer.text("1");
            xmlSerializer.endTag(null,"extrude");
            xmlSerializer.startTag(null,"tessellate");
            xmlSerializer.text("1");
            xmlSerializer.endTag(null,"tessellate");
            xmlSerializer.startTag(null,"altitudeMode");
            xmlSerializer.text("relativeToGround");
            xmlSerializer.endTag(null,"altitudeMode");
            xmlSerializer.startTag(null,"coordinates");
            for (int i=0;i<gps.size();i++){

                xmlSerializer.text(gps.get(i).getLongitude() + "," + gps.get(i).getLatitude() + "," + (gps.get(i).getAltitude() - avgGroundLevel) + "\n"  );
            }
            xmlSerializer.endTag(null,"coordinates");
            xmlSerializer.endTag(null,"LineString");
            xmlSerializer.endTag(null,"Placemark");
            xmlSerializer.endTag(null, "Document");
            xmlSerializer.endTag(null, "kml");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            fos.close();
            Log.i("3","File created");

            FileOutputStream timeDistanceFos = new FileOutputStream(timeDistanceFile);
            for(int p=0;p<Distance.size()-1;p++){
                t = Distance.get(p).toString() +","+ Time.get(p).toString()+"\n";
                timeDistanceFos.write(t.getBytes());
            }
            Log.i("4","File created");


            if(runway != null) {
                FileOutputStream runwayFos = new FileOutputStream(runwayFile);
                runwayFos.write(runway.toString().getBytes());
                runwayFos.close();
                Log.i("5", "File created");
            }
            Toast.makeText(calculate.this,"File is created in " + file.getPath(),Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(calculate.this,"File is not created",Toast.LENGTH_SHORT).show();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }


    }

    public void airportData(View view){
        if(gps.size() > 1) {
            String url = "https://www.overpass-api.de/api/interpreter?data=[out:json];(way[aeroway=runway](around:5000," + gps.get(0).getLatitude() + "," + gps.get(0).getLongitude() + ");>;);out;";
            RequestQueue requestQueue;

            requestQueue = Volley.newRequestQueue(this);


            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        Log.i("test", "The response is " + response.getString("elements"));
                        runway = response.getJSONArray("elements");
                        Toast.makeText(calculate.this, "API data recorded", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("parsing error", "Parsing Error");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("error ", "Error Occured");
                }
            });

            requestQueue.add(jsonArrayRequest);
        }

    }

    public void calculateTakeoffLandingDistance(){




        int j =0,i=0 ;
        ArrayList<Integer> point = new ArrayList<Integer>();
        for( i = 0;i<gps.size();i++){

            if(abs(avgGroundLevel-gps.get(i).getAltitude()) <= 5){
                for( j = i;j<gps.size();j++){

                    if( abs(gps.get(j).getAltitude() - avgGroundLevel) >= 5){

                        break;
                    }


                }
                point.add(i);
                point.add(j);

            }
            i = j+1;

        }

        //for 1st takeoff
        for(i=point.get(1);i<=point.get(0);i--){

            if(Distance.get(i) - Distance.get(i-1) < 0){

                CalculatedTakeoffDistance.add(calculateDistance(gps.get(i),gps.get(point.get(1)))) ;
            }

        }

        for(i=2;i<=point.size()-2;i+=2){

            for(j=point.get(i);j<point.get(i+1);j++){

                if(Distance.get(j) == 0){

                    CalculatedLandingDistance.add(calculateDistance(gps.get(point.get(i)),gps.get(j)));
                    CalculatedTakeoffDistance.add(calculateDistance(gps.get(j),gps.get(point.get(i+1))));
                    break;
                }
            }

        }

        //last landing distance
        j = point.size();
        CalculatedLandingDistance.add(calculateDistance(gps.get(point.get(j-2)),gps.get(point.get(j-1))));

    }




}