package com.example.rmznshn.ceviri_ispanyolca;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GirisActivity extends AppCompatActivity {

    String DB_PATH;
    final Context context = this;
    private SQLiteDatabase mDataBase;
    private static String DB_NAME = "veritabanim.db";

    ListView listeleme;
    private Veritabani db;
    String idtut="";

    EditText kelimeTr;
    EditText kelimeIs;

    private Veritabani veritabanim;
    Button mkaydet;
    Button mcevirigec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);


        listeleme=(ListView)findViewById(R.id.listelemelist);
        registerForContextMenu(listeleme);

        mkaydet=(Button)findViewById(R.id.butonkaydet);
        mcevirigec=(Button)findViewById(R.id.butoncevirigec);
        kelimeTr=(EditText)findViewById(R.id.kelimeTr);
        kelimeIs=(EditText)findViewById(R.id.kelimeIs);

        veritabanim = new Veritabani(this);

        mkaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(kelimeTr.getText().toString().trim().equals("") || kelimeIs.getText().toString().trim().equals("")) {
                        Toast.makeText(getApplicationContext(), "Lütfen Boş Yerleri Doldurunuz...", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        KayitEkle(kelimeTr.getText().toString().toLowerCase().trim(),kelimeIs.getText().toString().toLowerCase().trim());
                        listelemeyap();
                    }
                }
                finally{
                    veritabanim.close();
                }
            }
        });

        mcevirigec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GirisActivity.this,CeviriActivity.class);
                startActivity(intent);
            }
        });

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

        listelemeyap();

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

                db2.delete("veritabanim", "kelimeId = ?",
                        new String[]{searchStr});
                listelemeyap();
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void listelemeyap()
    {
        final List<String> listelemelist = new ArrayList<String>();
        final List<String> id_list=new ArrayList<String>();

        String kelime="";

        String []sutunlar=new String[]{"kelimeId","kelimeTr","kelimeIs"};
        SQLiteDatabase db1;
        db1=openOrCreateDatabase("veritabanim",SQLiteDatabase.CREATE_IF_NECESSARY,null);
        Cursor c= db1.rawQuery("SELECT * FROM veritabanim ", null);
        //db1.rawQuery("SELECT * FROM sorular",null);
        //rawQuery("SELECT * FROM sorular WHERE zorluk = ?", new String[] {"1"});



        for (int i=0;i<c.getCount();i++) {
            c.moveToNext();
            String kelimebirlestir="";
            kelimebirlestir+=c.getString(c.getColumnIndex("kelimeTr")).toString()+" - "+c.getString(c.getColumnIndex("kelimeIs"));
            listelemelist.add(kelimebirlestir);
            id_list.add(c.getString(c.getColumnIndex("kelimeId")));
        }



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listelemelist);
        listeleme.setAdapter(adapter);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, id_list);

        listeleme.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                idtut = id_list.get(position);
                return false;
            }
        });
    }


    private void KayitEkle(String Tr, String Is){
        SQLiteDatabase db = veritabanim.getWritableDatabase();
        ContentValues veriler = new ContentValues();
        veriler.put("kelimeTr", Tr);
        veriler.put("kelimeIs", Is);
        db.insertOrThrow("veritabanim", null, veriler);

        kelimeTr.setText("");
        kelimeIs.setText("");

        listelemeyap();
    }
}
