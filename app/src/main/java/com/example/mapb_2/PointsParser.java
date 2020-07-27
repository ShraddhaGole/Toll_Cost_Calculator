package com.example.mapb_2;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.System.err;


public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    int sum = 0;
    TaskLoadedCallback taskCallback;
    String directionMode = "driving";

    public PointsParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("in_pars_jsonData[0]", jsonData[0].toString());

            calculate(jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("in_pars_parser", parser.toString());

            // Starts parsing data
            routes = parser.parse(jObject);
            Log.d("in_pars", "Executing routes");
            Log.d("in_pars_routes:", routes.toString());

        } catch (Exception e) {
            Log.d("in_pars_exception", e.toString());
            e.printStackTrace();
        }
        return routes;
    }


    private void calculate(String res) {
        Log.d("in_pars_i_got", res);
        try {
            JSONObject job = new JSONObject(res);
            Log.d("in_pars_yesss", String.valueOf(job));
            //int sum=0;

            JSONArray route = job.getJSONArray("routes");
            //List<String> posterPaths = new ArrayList<String>(json1arr.length());
            for (int i = 0; i < route.length(); i++) {
                JSONObject ithObj = route.getJSONObject(i);
                JSONArray legs = ithObj.getJSONArray("legs");
                for (int j = 0; j < legs.length(); j++) {
                    JSONObject jthObj = legs.getJSONObject(j);
                    JSONArray steps = jthObj.getJSONArray("steps");
                    for (int k = 0; k < steps.length(); k++) {
                        String ma = steps.get(k).toString();
                        //if(ma.matches("a"))sum++;
                        if (Pattern.compile("Toll road").matcher(ma).find()) sum++;
                    }
                }

            }
            //Toast.makeText((Context) taskCallback,"total number of tolls are:" +sum,Toast.LENGTH_LONG).show();
            Log.d("in_pars_my_answer", String.valueOf(sum));
            

        } catch (JSONException err) {
            Log.d("Error", err.toString());
        }
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        // Traversing through all the routes
        Log.d("in_pars_passed_result", String.valueOf(result));
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            Log.d("in_pars_result.get(i)", String.valueOf(result.get(i)));
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            Log.d("in_pars", "componenets of that resilt[i] are:");
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                Log.d("in_pars_point", String.valueOf(point));
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            if (directionMode.equalsIgnoreCase("walking")) {
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);
            } else {
                lineOptions.width(10);
                lineOptions.color(Color.MAGENTA);
            }
            Log.d("mylog", "onPostExecute lineoptions decoded");
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            //mMap.addPolyline(lineOptions);
            Log.d("mylog", "calling ontaskdone");
            taskCallback.onTaskDone(lineOptions);

        } else {
            Log.d("mylog", "without Polylines drawn");
        }

    }
}