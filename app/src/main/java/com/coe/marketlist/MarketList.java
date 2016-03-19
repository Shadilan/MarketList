package com.coe.marketlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

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
    ImageButton refresh;
    ImageButton open;
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
        refresh= (ImageButton) findViewById(R.id.refresh);
        open= (ImageButton) findViewById(R.id.open);
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
                    } else
                    {
                        final SharedPreferences sp = getApplicationContext().getSharedPreferences("MarketList", MODE_PRIVATE);
                        final SharedPreferences.Editor spe = sp.edit();
                        spe.putString("LastItem", guid);
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
                        String itemGroup = obj.getString("Group");
                        final MyTextView t = new MyTextView(getApplicationContext(), itemGUID, itemName, itemState,itemGroup);
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
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share)));

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
        String Name;
        String Group;
        public MyTextView(Context context,String GUID,String Name,String State,String Group) {
            super(context);
            this.GUID=GUID;
            this.setText(Name);
            this.Name=Name;
            this.Group=Group;
            this.State=State;
            if (State.equalsIgnoreCase("O")) this.setBackgroundColor(Color.GRAY);
            else this.setTextColor(Color.GREEN);
            this.setTextSize(20);

        }
        @Override
        public boolean onTouchEvent(final MotionEvent event) {
            if (event.getActionMasked()==MotionEvent.ACTION_UP) {
                switch (action) {
                    case 3:
                        serverConnect.getInstance().DeleteItem(listGuid,GUID);
                        break;
                    case 2:
                        editItem(GUID,Name, Group);
                        break;
                    default:
                    if (State.equals("S")) {
                        serverConnect.getInstance().StateItem(listGuid, GUID, "O");
                        this.setTextColor(Color.GRAY);
                        State = "O";
                    } else {
                        serverConnect.getInstance().StateItem(listGuid, GUID, "S");
                        this.setTextColor(Color.GREEN);
                        State = "S";
                    }
                }
            }
            return true;
        }


    }
    String selectectedGUID="";
    private void editItem(String GUID,String name, String group) {
        selectectedGUID=GUID;
        ((EditText) findViewById(R.id.itemName)).setText(name);
        ((EditText) findViewById(R.id.itemGroup)).setText(group);
        findViewById(R.id.editItem).setVisibility(View.VISIBLE);
    }
    public void saveItem(View v){
        switch (action)
        {
            case 1:
                serverConnect.getInstance().addItem(listGuid,
                        ((EditText) findViewById(R.id.itemName)).getText().toString(),
                        ((EditText) findViewById(R.id.itemGroup)).getText().toString());
                break;
            case 2:
                serverConnect.getInstance().changeItem(listGuid,selectectedGUID,
                        ((EditText) findViewById(R.id.itemName)).getText().toString(),
                        ((EditText) findViewById(R.id.itemGroup)).getText().toString());
                break;
            case 4:
                serverConnect.getInstance().createList(((EditText) findViewById(R.id.listName)).getText().toString());
                break;
        }
        cancelItem(v);
    }
    public void cancelItem(View v){
        selectectedGUID="";
        ((EditText) findViewById(R.id.itemName)).setText("");
        ((EditText) findViewById(R.id.itemGroup)).setText("");
        ((EditText) findViewById(R.id.listName)).setText("");
        findViewById(R.id.editItem).setVisibility(View.GONE);
        findViewById(R.id.createList).setVisibility(View.GONE);
    }


    private void showList(){
        energyBreakPointStart(795154489, "com.coe.marketlist.MarketList.showList.239");
        final LinearLayout  linearLayout= (LinearLayout) findViewById(R.id.listOfList);
        linearLayout.removeAllViews();
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
            button.setOnTouchListener(new View.OnTouchListener() {
                Point oldTouch;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        oldTouch = new Point((int) event.getRawX(), (int) event.getRawY());
                        Log.d("tttt","Touch Down:"+event.getRawX());
                    } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                        Log.d("tttt","Touch Up:"+event.getRawX());
                        if (oldTouch.x - event.getRawX() > 50) {
                            list.remove(v.getTag());
                            final SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MarketList", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            try {
                                list.saveToPreferences(editor,"List");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            editor.apply();
                            showList();

                        } else if (oldTouch.x - event.getRawX() < 10 && oldTouch.y - event.getRawY() < 10)
                        {
                            listGuid = (String) v.getTag();
                            refresh.callOnClick();
                            hideList();
                        }
                    }
                    return true;
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
    int action=0;
    public void showPanel(View v){
        if (!(v instanceof ToggleButton)) return;
        ToggleButton b=(ToggleButton)v;
        if (!b.isChecked()){
            findViewById(R.id.instrumentPanel).setVisibility(View.GONE);
            setAction(0);

        } else
        {
            findViewById(R.id.instrumentPanel).setVisibility(View.VISIBLE);
        }
        //b.setChecked(!b.isChecked());
    }
    //TODO:additional operations on change action
    private void setAction(int action){
        this.action=action;
        ImageView actImg=(ImageView) findViewById(R.id.imageView);
        switch (action)
        {
            case 2:
                actImg.setImageResource(R.mipmap.edit);
                break;
            case 3:
                actImg.setImageResource(R.mipmap.delete);
                break;
            default:actImg.setImageBitmap(null);
                break;
        }
    }

    int CREATE=1;
    int EDIT=2;
    int DELETE=3;
    int LIST=4;
    public void createAction(View v){
        setAction(CREATE);
        editItem("","","");
    }
    public void deleteAction(View v){
        if (action!=DELETE) setAction(DELETE);
        else setAction(0);
    }
    public void editAction(View v){

        if (action!=EDIT) setAction(EDIT);
        else setAction(0);
    }
    public void listAction(View v){
        setAction(LIST);
        findViewById(R.id.createList).setVisibility(View.VISIBLE);

    }

}
