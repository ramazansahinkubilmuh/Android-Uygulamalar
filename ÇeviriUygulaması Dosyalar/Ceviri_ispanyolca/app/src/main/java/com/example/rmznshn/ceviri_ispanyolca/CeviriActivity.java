package com.example.rmznshn.ceviri_ispanyolca;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CeviriActivity extends AppCompatActivity {

    String DB_PATH;
    final Context context = this;
    private SQLiteDatabase mDataBase;
    private static String DB_NAME = "veritabanim.db";

    private Veritabani db;
    String idtut="";

    private Veritabani veritabanim;
    EditText cevrilecekkelime;

    RadioButton mRadio1, mRadio2;
    EditText mInputText;
    TextView mOutputText;
    Button mCevir;

    RadioButton rb,rb2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceviri);

        cevrilecekkelime=(EditText)findViewById(R.id.editText1);

        mRadio1 = (RadioButton)findViewById(R.id.ispanturk);
        mRadio2 = (RadioButton)findViewById(R.id.turkispan);
        mInputText=(EditText)findViewById(R.id.editText1);
        mOutputText=(TextView)findViewById(R.id.textView);
        mCevir=(Button)findViewById(R.id.butoncevir);

        mInputText.setHint("İspanyolca kelime girin...");
        mOutputText.setText("Türkçe karşılığı için çevire tıklayın...");

        rb = (RadioButton) findViewById(R.id.ispanturk);
        rb.setOnClickListener(first_radio_listener);
        rb2 = (RadioButton) findViewById(R.id.turkispan);
        rb2.setOnClickListener(first_radio_listener2);

        veritabanim = new Veritabani(this);

        mCevir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(rb.isChecked())
                        kayitcek(true);
                    else if(rb2.isChecked())
                        kayitcek(false);
                    //KayitEkle(kelimeTr.getText().toString(), kelimeIs.getText().toString());
                }
                finally{
                    veritabanim.close();
                }
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
    }


    public void kayitcek(boolean gelen)
    {
        String kelime="";
        String gelenkontrol="";
        String gelenkontrolters="";

        if (gelen) {
            gelenkontrol = "kelimeIs";
            gelenkontrolters="kelimeTr";
        }
        else {
            gelenkontrol="kelimeTr";
            gelenkontrolters = "kelimeIs";
        }


        String []sutunlar=new String[]{"kelimeId","kelimeTr","kelimeIs"};
        SQLiteDatabase db1;
        db1=openOrCreateDatabase("veritabanim",SQLiteDatabase.CREATE_IF_NECESSARY,null);
        Cursor c= db1.rawQuery("SELECT * FROM veritabanim where "+ gelenkontrol.toString()+"='" + cevrilecekkelime.getText().toString() + "'", null);
        //db1.rawQuery("SELECT * FROM sorular",null);
        //rawQuery("SELECT * FROM sorular WHERE zorluk = ?", new String[] {"1"});

        while (c.moveToNext()) {
            kelime=(String)c.getString(c.getColumnIndex(gelenkontrolters.toString()));
        }

        mOutputText.setText(kelime);

        if(mOutputText.getText()=="") {
            Toast.makeText(getApplicationContext(), "Çevrilecek değer bulunamadı...", Toast.LENGTH_SHORT).show();
        }
    }


    View.OnClickListener first_radio_listener = new View.OnClickListener(){
        public void onClick(View v) {
            mInputText.setHint("İspanyolca kelime girin...");
            mOutputText.setText("Türkçe karşılığı için çevire tıklayın...");

            mCevir.callOnClick();
        }
    };

    View.OnClickListener first_radio_listener2 = new View.OnClickListener(){
        public void onClick(View v) {
            mInputText.setHint("Türkçe kelime girin...");
            mOutputText.setText("İspanyolca karşılığı için çevire tıklayın...");

            mCevir.callOnClick();
        }
    };
}
