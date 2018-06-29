package com.example.rmznshn.rssokuyucu;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "Otomatik internet Kontrol¸";
    private NetworkChangeReceiver receiver;//Network dinleyen receiver objemizin referans˝

    String DB_PATH;
    final Context context = this;
    private SQLiteDatabase mDataBase;
    private static String DB_NAME = "haberler.db";

    ListView haber_listwiev;
    private Veritabani db;
    String idtut="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Receiverımızı register ediyoruz
        //Yani Çalıştırıyoruz
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);

        haber_listwiev=(ListView)findViewById(R.id.haberlistesi);
        registerForContextMenu(haber_listwiev);


        db = new Veritabani(this);

        try {

            db.createDB();
        } catch (IOException ioe) {

            throw new Error("Database not created....");
        }

        try {
            db.openDB();

        }catch(SQLException sqle){

            throw sqle;
        }
        gazetelerilistele();

        if(haber_listwiev.getCount()==0){
            Toast.makeText(getApplicationContext(), "Çekilecek Veri Bulunamadı...", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(), haber_listwiev.getCount() + " Tane veri yüklendi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater b=getMenuInflater();
        b.inflate(R.menu.context_menu, menu);

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.verisilmepopup:
                SQLiteDatabase db2 = db.getReadableDatabase();
                String searchStr = idtut;

                db2.delete("haberler", "id = ?",
                        new String[]{searchStr});
                gazetelerilistele();
                if(haber_listwiev.getCount()==0)
                    finish();
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void gazetelerilistele()
    {
        final List<String> haber_list = new ArrayList<String>();
        final List<String> linkler_list=new ArrayList<String>();
        final List<String> id_list=new ArrayList<String>();

        String []sutunlar=new String[]{"id","name","url"};
        SQLiteDatabase db1;
        db1=openOrCreateDatabase("haberler",SQLiteDatabase.CREATE_IF_NECESSARY,null);
        Cursor c= db1.query("haberler",sutunlar,null,null,null,null,null);
        //db1.rawQuery("SELECT * FROM sorular",null);
        //rawQuery("SELECT * FROM sorular WHERE zorluk = ?", new String[] {"1"});

        for (int i=0;i<c.getCount();i++) {
            c.moveToNext();
            haber_list.add(c.getString(c.getColumnIndex("name")));
            linkler_list.add(c.getString(c.getColumnIndex("url")));
            id_list.add(c.getString(c.getColumnIndex("id")));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, haber_list);
        haber_listwiev.setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, linkler_list);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, id_list);

        haber_listwiev.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String pen = linkler_list.get(position).toString();
                        Intent intent = new Intent(MainActivity.this,Haberler.class);
                        intent.putExtra("site",pen);
                        startActivity(intent);

                    }
                }
        );

        haber_listwiev.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                idtut=id_list.get(position);
                return false;
            }
        });
    }
}