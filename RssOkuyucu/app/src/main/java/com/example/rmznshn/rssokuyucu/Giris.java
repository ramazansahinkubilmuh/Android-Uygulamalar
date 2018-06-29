package com.example.rmznshn.rssokuyucu;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class Giris extends AppCompatActivity {

    TextView isim;
    TextView link;
    EditText name;
    EditText url;
    Button kaydet;
    Button giris;
    Button verikayit;
    Button iptal;
    private Veritabani haberler;

    boolean internetBaglantisiVarMi() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo=conMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        haberler = new Veritabani(this);

        if(!internetBaglantisiVarMi())
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();

            alertDialog.setMessage("Uygulamayı kullanabilmek için internet bağlantınızın aktif olması gerekmektedir...");
            alertDialog.setButton("Tamam", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });

            alertDialog.show();
        }
        else{
            isim=(TextView)findViewById(R.id.isim);
            link=(TextView)findViewById(R.id.link);
            name=(EditText)findViewById(R.id.name);
            url=(EditText)findViewById(R.id.url);
            kaydet=(Button)findViewById(R.id.kaydet);
            giris=(Button)findViewById(R.id.giris);
            verikayit=(Button)findViewById(R.id.verikaydetme);
            iptal=(Button)findViewById(R.id.iptal);


            isim.setVisibility(View.INVISIBLE);
            link.setVisibility(View.INVISIBLE);
            name.setVisibility(View.INVISIBLE);
            url.setVisibility(View.INVISIBLE);
            kaydet.setVisibility(View.INVISIBLE);
            iptal.setVisibility(View.INVISIBLE);

            verikayit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isim.setVisibility(View.VISIBLE);
                    link.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    url.setVisibility(View.VISIBLE);
                    kaydet.setVisibility(View.VISIBLE);
                    iptal.setVisibility(View.VISIBLE);
                    giris.setVisibility(View.INVISIBLE);
                    verikayit.setVisibility(View.INVISIBLE);

                    name.setText("");
                    url.setText("");
                }
            });

            iptal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    isim.setVisibility(View.INVISIBLE);
                    link.setVisibility(View.INVISIBLE);
                    name.setVisibility(View.INVISIBLE);
                    url.setVisibility(View.INVISIBLE);
                    kaydet.setVisibility(View.INVISIBLE);
                    iptal.setVisibility(View.INVISIBLE);
                    giris.setVisibility(View.VISIBLE);
                    verikayit.setVisibility(View.VISIBLE);

                }
            });

            giris.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Giris.this,MainActivity.class);
                    startActivity(intent);
                }
            });

            kaydet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try{
                        if(name.getText().toString().trim().equals("") || url.getText().toString().trim().equals("")) {
                            Toast.makeText(getApplicationContext(), "Lütfen Boş Yerleri Doldurunuz...", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            KayitEkle(name.getText().toString(), url.getText().toString());
                            isim.setVisibility(View.INVISIBLE);
                            link.setVisibility(View.INVISIBLE);
                            name.setVisibility(View.INVISIBLE);
                            url.setVisibility(View.INVISIBLE);
                            kaydet.setVisibility(View.INVISIBLE);
                            iptal.setVisibility(View.INVISIBLE);
                            giris.setVisibility(View.VISIBLE);
                            verikayit.setVisibility(View.VISIBLE);
                        }
                    }
                    finally{
                        haberler.close();
                    }
                }
            });
        }


    }

    private void KayitEkle(String name, String url){
        SQLiteDatabase db = haberler.getWritableDatabase();
        ContentValues veriler = new ContentValues();
        veriler.put("name", name);
        veriler.put("url", url);
        db.insertOrThrow("haberler", null, veriler);
    }

}
