package com.example.rmznshn.rssokuyucu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Haberler extends AppCompatActivity {

    ListView liste;

    String site=" ";

    WebView tarayici;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haberler);

        liste = (ListView) findViewById(R.id.haberbasliklari);


        tarayici=(WebView)findViewById(R.id.haberlerweb);

        Bundle extras = getIntent().getExtras();
        String value=extras.getString("site");
        site=value;


        //tarayıcımızı tanıtalım.

        tarayici.getSettings().setJavaScriptEnabled(true);

//javascriptleri çalıştırmasını sağlayalım.

        tarayici.setWebViewClient(new WebViewClient());

//bu kodu yazmadığımız takdirde telefonun tarayıcısında açıyor...
        tarayici.setWebChromeClient(new WebChromeClient());
//bu kod youtube tarzı sitelerde videoyu oynatması için. Eski sürüm androidlerde çalışmayabilir...

        tarayici.loadUrl(site);
        final ProgressDialog progress = ProgressDialog.show(this, "Haber Başlıkları", "Başlıklar Yükleniyor...", true);//Site yükleme aşamasında iken progres bar gözükecek
        progress.show();

        tarayici.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                WebServisiIleListeyiDoldur();
                if(liste.getCount()==0) {
                    Toast.makeText(getApplicationContext(), "Çekilecek Veri Bulunamadı...", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), liste.getCount() + " Tane veri yüklendi", Toast.LENGTH_SHORT).show();//Site doğru şekilde yüklendi
                    progress.dismiss();
                }


            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {//Yükleme sırasında hata oluştu
                Toast.makeText(getApplicationContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });

//bir urlyi çağırmasını istiyoruz.

//loaddata metodu ile html kodlarını çalıştırmak da mümkün...




    }

    private void WebServisiIleListeyiDoldur() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String haber_url = site;

        final List<String> haber_list = new ArrayList<String>();
        final List<String> linkler=new ArrayList<String>();

        HttpURLConnection baglanti = null;

        try {
            URL url = new URL(haber_url);

            baglanti = (HttpURLConnection) url.openConnection();

            int baglanti_durumu = baglanti.getResponseCode();

            if (baglanti_durumu == HttpURLConnection.HTTP_OK) {

                BufferedInputStream stream = new BufferedInputStream(baglanti.getInputStream());
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                Document document = documentBuilder.parse(stream); //iki farklı paket var


                NodeList haberNodeList = document.getElementsByTagName("item");

                for (int i = 0; i < haberNodeList.getLength(); i++) {
                    Element element = (Element) haberNodeList.item(i); //üç farklı paket var

                    NodeList nodeListisim = element.getElementsByTagName("title");
                    NodeList nodeListlink = element.getElementsByTagName("link");

                    String isim = nodeListisim.item(0).getFirstChild().getNodeValue();
                    String link = nodeListlink.item(0).getFirstChild().getNodeValue();

                    linkler.add(link);
                    haber_list.add("Haber "+ (i+1)+" : "+isim);

                }



            }

        } catch (Exception e) {

            Log.e("Xml parse hatası", e.getMessage().toString());

        } finally {

            if (baglanti != null)
                baglanti.disconnect();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, haber_list);
        liste.setAdapter(adapter);




        liste.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String pen = linkler.get(position).toString();
                        Intent intent = new Intent(Haberler.this,WebGoruntule.class);
                        intent.putExtra("site",pen);
                        startActivity(intent);

                    }
                }
        );
    }
}
