package com.coe.marketlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Shadilan
 */
public class serverConnect {
    private static serverConnect instance;

    public static serverConnect getInstance(){
        if (instance ==null){
            instance=new serverConnect();
        }
        return instance;
    }

    private String ServerAddres="http://tolps.pe.hu/MainService.php";
    private Context context;
    private RequestQueue reqq;
    private String Token;

    /**
     * Constructor
     */
    private serverConnect(){

    }

    /**
     * Set Parameters of connection
     * @param ctx Application context
     */
    public void Connect(Context ctx){
        context =ctx;
        reqq=Volley.newRequestQueue(context);
    }

    /**
     * Check internet connection
     * @return Return true if connection exists
     */
    private boolean checkConnection(){
        if (context==null) return false;
        ConnectivityManager connMgr = (ConnectivityManager)
                instance.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //Listeners
    private ArrayList<Response.Listener<JSONObject>> responseListener;
    private ArrayList<Response.Listener<JSONObject>> remResponseListener;
    /**
     * Add Login Listener to object
     * @param listener Listener to add
     */
    public void addListener(Response.Listener<JSONObject> listener){
        if (responseListener==null){
            responseListener=new ArrayList<>();
        }
        responseListener.add(listener);
    }

    /**
     * Remove LoginListener from object
     * @param listener Listener to remove
     */
    public void removeListener(Response.Listener<JSONObject> listener){
        if (remResponseListener==null){
            remResponseListener=new ArrayList<>();
        }
        remResponseListener.add(listener);
    }

    /**
     * Exec Listeners on event
     * @param response Response from server
     */
    private void doListener(JSONObject response){
        if (remResponseListener!=null && remResponseListener.size()>0) responseListener.removeAll(remResponseListener);
        if (responseListener !=null)
            for (Response.Listener<JSONObject> listener:responseListener){
                listener.onResponse(response);
            }
    }

    //CreateList
    public boolean createList(String Name){
        if (!checkConnection()) return false;
        Log.d("ServeConnect","Connection start");
        String url=ServerAddres+"?OPER=CREATE&NAME="+Name;
        Log.d("ServeConnect",url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        doListener(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("LoginTest",error.toString());
                    }
                });
        Log.d("ServeConnect",jsObjRequest.toString());
        reqq.add(jsObjRequest);
        return true;
    }
    //AddItem
    public boolean addItem(String GUID,String Name){
        if (!checkConnection()) return false;
        Log.d("ServeConnect","Connection start");
        String url=ServerAddres+"?OPER=ADD&GUID="+GUID+"&NAME="+Name;
        Log.d("ServeConnect",url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        doListener(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("LoginTest",error.toString());
                    }
                });
        Log.d("ServeConnect",jsObjRequest.toString());
        reqq.add(jsObjRequest);
        return true;
    }
    //DeleteItem
    public boolean DeleteItem(String GUID,String ItemGUID){
        if (!checkConnection()) return false;
        Log.d("ServeConnect","Connection start");
        String url=ServerAddres+"?OPER=DELETE&GUID="+GUID+"&ITEMGUID="+ItemGUID;
        Log.d("ServeConnect",url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        doListener(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("LoginTest",error.toString());
                    }
                });
        Log.d("ServeConnect",jsObjRequest.toString());
        reqq.add(jsObjRequest);
        return true;
    }
    //ChangeItem
    public boolean ChangeItem(String GUID, String ItemGUID,String Name){
        if (!checkConnection()) return false;
        Log.d("ServeConnect","Connection start");
        String url=ServerAddres+"?OPER=CHANGE&GUID="+GUID+"&ITEMGUID="+ItemGUID+"NAME="+Name;
        Log.d("ServeConnect",url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        doListener(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("LoginTest",error.toString());
                    }
                });
        Log.d("ServeConnect",jsObjRequest.toString());
        reqq.add(jsObjRequest);
        return true;
    }
    //StateItem
    public boolean StateItem(String GUID, String ItemGUID,String State){
        if (!checkConnection()) return false;
        Log.d("ServeConnect","Connection start");
        String url=ServerAddres+"?OPER=STATUS&GUID="+GUID+"&ITEMGUID="+ItemGUID+"&STATE="+State;
        Log.d("ServeConnect",url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        doListener(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("LoginTest",error.toString());
                    }
                });
        Log.d("ServeConnect",jsObjRequest.toString());
        reqq.add(jsObjRequest);
        return true;
    }
    //GetData
    public boolean GetData(String GUID){
        if (!checkConnection()) return false;
        Log.d("ServeConnect","Connection start");
        String url=ServerAddres+"?OPER=GET&GUID="+GUID;
        Log.d("ServeConnect",url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        doListener(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("LoginTest",error.toString());
                    }
                });
        Log.d("ServeConnect",jsObjRequest.toString());
        reqq.add(jsObjRequest);
        return true;
    }

}
