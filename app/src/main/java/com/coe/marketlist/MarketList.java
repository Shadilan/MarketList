package com.coe.marketlist;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    EditText listGuid;
    EditText listName;
    Button open;
    Button create;
    LinearLayout itemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        serverConnect.getInstance().Connect(getApplicationContext());
        listGuid= (EditText) findViewById(R.id.editText);
        listName= (EditText) findViewById(R.id.editText2);
        open= (Button) findViewById(R.id.Open);
        create= (Button) findViewById(R.id.Create);
        itemList= (LinearLayout) findViewById(R.id.ItemList);
        serverConnect.getInstance().addListener(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    listGuid.setText(response.getString("GUID"));
                    listName.setText(response.getString("Name"));
                    JSONArray arr = response.getJSONArray("Items");
                    int s=arr.length();
                    itemList.removeAllViews();
                    for (int i=0;i<s;i++){
                        JSONObject obj=arr.getJSONObject(i);
                        String itemGUID=obj.getString("GUID");
                        String itemName=obj.getString("Name");
                        String itemState=obj.getString("State");
                        MyTextView t=new MyTextView(getApplicationContext(),itemGUID,itemName,itemState);
                        itemList.addView(t);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverConnect.getInstance().GetData(listGuid.getText().toString());
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverConnect.getInstance().GetData(listName.getText().toString());
            }
        });


    }
    private class MyTextView extends Button{
        String GUID;
        public MyTextView(Context context,String GUID,String Name,String State) {
            super(context);
            this.GUID=GUID;
            this.setText(Name);
            if (State.equalsIgnoreCase("O")) this.setBackgroundColor(Color.GRAY);
            else this.setTextColor(Color.GREEN);
            this.setTextSize(20);

        }
        int x=0;
        int y=0;
        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if (event.getActionMasked()==MotionEvent.ACTION_DOWN){
                x= (int) event.getX();
                y= (int) event.getY();
                Log.d("TestTouch", "Down");
            } else
            if (event.getActionMasked()==MotionEvent.ACTION_UP){
                int dx= (int) (this.x-event.getX());
                int dy= (int) (this.y-event.getY());
                Log.d("TestTouch", "Up");
                if (dx>Math.abs(dy)+100){
                    Log.d("TestTouch", "SToch");
                    serverConnect.getInstance().StateItem(listGuid.getText().toString(), GUID, "S");
                    this.setTextColor(Color.GREEN);
                } else if (dx<0 && Math.abs(dx)>Math.abs(dy)+100){
                    Log.d("TestTouch", "OToch");
                    serverConnect.getInstance().StateItem(listGuid.getText().toString(), GUID, "O");
                    this.setTextColor(Color.GRAY);
                }
            }

            return true;
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_market_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
