package com.coe.marketlist;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Shadilan on 13.03.2016.
 */
public class ListOfList extends HashMap<String,String> {
    public void loadFromPreferences(SharedPreferences sp,String field) throws JSONException {
        String jsonString=sp.getString(field,"");
        if ("".equals(jsonString)) return;
        JSONArray jsonArray=new JSONArray(jsonString);
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            this.put(jsonObject.getString("key"),jsonObject.getString("value"));
        }
    }
    public void saveToPreferences(SharedPreferences.Editor ed,String field) throws JSONException {
        if (this.size()==0) return;
        JSONArray jsonArray=new JSONArray();
        for (String key:this.keySet()){
           jsonArray.put(new JSONObject().put("key",key).put("value", this.get(key)));
        }
        ed.putString(field,jsonArray.toString());
    }

}
