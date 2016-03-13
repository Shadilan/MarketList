package com.coe.marketlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MarketList extends AppCompatActivity {
    /*
    По открытию загрузить данные из преференца если поле GUID не пустое загрузить данные
    По кнопке открыть сохранить данные в преференце загрузить данные

    По кнопке создать создать список поставить переменную создания списка

    По приходу данных Очистисть вью
    Для каждой записи строку

    По лонг клику на строке открыть режим редактирования

    По свайпу строки отметить строку или разотметить строку

    По нажатию кнопки добавить открыть режим редактирования

    В режиме редактирования при нажатии отправить
    Если открыто из режима редактирования вызвать изменение записи

    Если открыто из режима создания вызвать создание записи
     */
    //EditText listGuid;
    String listGuid;
    TextView listName;
    Button refresh;
    Button open;
    //Button create;
    LinearLayout itemList;
    ListOfList list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_list);

        final SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MarketList", MODE_PRIVATE);
        list=new ListOfList();
        try {
            list.loadFromPreferences(sharedPreferences,"List");
        } catch (final JSONException e) {
            e.printStackTrace();
        }
        final String lastItem=sharedPreferences.getString("LastItem","");

        final Intent intent = getIntent();
        final Uri data;
        String ID = "";
        if (Intent.ACTION_VIEW.equals(intent.getAction()))
        {
            data = intent.getData();

            if (data != null) ID = data.getQueryParameter("ID");
        }



        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        serverConnect.getInstance().Connect(getApplicationContext());
        //listGuid= (EditText) findViewById(R.id.editText);
        listName= (TextView) findViewById(R.id.editText2);
        refresh= (Button) findViewById(R.id.refresh);
        open= (Button) findViewById(R.id.open);
        itemList= (LinearLayout) findViewById(R.id.ItemList);


        serverConnect.getInstance().addListener(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                long st = new Date().getTime();
                try {
                    final String guid = response.getString("GUID");
                    final String name = response.getString("Name");
                    listGuid = guid;
                    if (!list.containsKey(guid)) {
                        list.put(guid, name);

                        final SharedPreferences sp = getApplicationContext().getSharedPreferences("MarketList", MODE_PRIVATE);
                        final SharedPreferences.Editor spe = sp.edit();
                        spe.putString("LastItem", guid);
                        list.saveToPreferences(spe, "List");
                        spe.apply();
                    }

                    listName.setText(name);

                    final JSONArray arr = response.getJSONArray("Items");
                    final int s = arr.length();
                    itemList.removeAllViews();
                    for (int i = 0; i < s; i++) {
                        final JSONObject obj = arr.getJSONObject(i);
                        final String itemGUID = obj.getString("GUID");
                        final String itemName = obj.getString("Name");
                        final String itemState = obj.getString("State");
                        //String itemGroup = obj.getString("Group");
                        final MyTextView t = new MyTextView(getApplicationContext(), itemGUID, itemName, itemState);
                        itemList.addView(t);
                    }


                } catch (final JSONException e) {
                    e.printStackTrace();
                }
                st = new Date().getTime() - st;
                Log.d("SpeedLog", "TimeTake:" + st + " ms");
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (listGuid.equals("")) {
                    if (listName.getText().toString().equals(""))
                        listName.setText("Список покупок");
                    serverConnect.getInstance().GetData(listName.getText().toString());
                }
                serverConnect.getInstance().GetData(listGuid);
            }
        });

        Log.d("Debug Market", "ID:" + ID);
        Log.d("Debug Market", "ID:" + lastItem);
        if (!"".equals(ID)) {
            listGuid=ID;
            refresh.callOnClick();
        } else if (!"".equals(lastItem)) {
            listGuid=lastItem;
            refresh.callOnClick();
        }
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showList();
            }
        });





    }
    public void SHARE(View view) {

        String shareBody = "http://tolps.pe.hu/default.php?ID="+listGuid;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "\n\n");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent,  getResources().getString(R.string.share)));

    }
    public void energyBreakPointStart(final int stateId, final String stateDescription) {
        final Intent stateUpdate = new Intent("com.quicinc.Trepn.UpdateAppState");
        stateUpdate.putExtra("com.quicinc.Trepn.UpdateAppState.Value", stateId);
        stateUpdate.putExtra("com.quicinc.Trepn.UpdateAppState.Value.Desc", stateDescription);
        sendBroadcast(stateUpdate);
    }// Generated  energyBreakPointStart method

    public void energyBreakPointEnd() {
        final Intent stateUpdate = new Intent("com.quicinc.Trepn.UpdateAppState");
        stateUpdate.putExtra("com.quicinc.Trepn.UpdateAppState.Value", 0);
        sendBroadcast(stateUpdate);
    }// Generated  energyBreakPointEnd method

    private class MyTextView extends Button{
        String GUID;
        String State;
        public MyTextView(Context context,String GUID,String Name,String State) {
            super(context);
            this.GUID=GUID;
            this.setText(Name);
            this.State=State;
            if (State.equalsIgnoreCase("O")) this.setBackgroundColor(Color.GRAY);
            else this.setTextColor(Color.GREEN);
            this.setTextSize(20);

        }
        @Override
        public boolean onTouchEvent(final MotionEvent event) {
            if (event.getActionMasked()==MotionEvent.ACTION_UP) {
                if (State.equals("S")) {
                    serverConnect.getInstance().StateItem(listGuid, GUID, "O");
                    this.setTextColor(Color.GRAY);
                    State="O";
                } else {
                    serverConnect.getInstance().StateItem(listGuid, GUID, "S");
                    this.setTextColor(Color.GREEN);
                    State="S";
                }
            }
            return true;
        }


    }




    private void showList(){
        energyBreakPointStart(795154489, "com.coe.marketlist.MarketList.showList.239");
        final LinearLayout  linearLayout= (LinearLayout) findViewById(R.id.listOfList);
        linearLayout.removeAllViews();
        Log.d("tttt","Size:"+list.size());
        for (final String key:list.keySet()){
            final Button button=new Button(getApplicationContext());
            button.setTag(key);
            button.setText(list.get(key));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    listGuid = (String) v.getTag();
                    refresh.callOnClick();
                    hideList();
                }

            });
            linearLayout.addView(button);
        }
        findViewById(R.id.scrollView).setVisibility(View.VISIBLE);

    }
    private void hideList(){
        energyBreakPointStart(619042121, "com.coe.marketlist.MarketList.hideList.258");
        findViewById(R.id.scrollView).setVisibility(View.GONE);
    }
}
