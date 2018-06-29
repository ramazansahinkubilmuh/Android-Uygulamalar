package com.example.rmznshn.rssokuyucu;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebGoruntule extends Activity{

    WebView tarayici;
    String acilacak_haber_sitesi="";

//tarayıcı referansını oluşturalım.

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_goruntule);


        tarayici=(WebView)findViewById(R.id.tarayici);

        Bundle extras = getIntent().getExtras();
        String value=extras.getString("site");
        acilacak_haber_sitesi=value;

//tarayıcımızı tanıtalım.

        tarayici.getSettings().setJavaScriptEnabled(true);

//javascriptleri çalıştırmasını sağlayalım.

        tarayici.setWebViewClient(new WebViewClient());

//bu kodu yazmadığımız takdirde telefonun tarayıcısında açıyor...
        tarayici.setWebChromeClient(new WebChromeClient());
//bu kod youtube tarzı sitelerde videoyu oynatması için. Eski sürüm androidlerde çalışmayabilir...

        tarayici.loadUrl(acilacak_haber_sitesi);
        final ProgressDialog progress = ProgressDialog.show(this, "Haber Kaynağı", "Site Yükleniyor...", true);//Site yükleme aşamasında iken progres bar gözükecek
        progress.show();

        tarayici.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Toast.makeText(getApplicationContext(), "Sayfa yüklendi", Toast.LENGTH_SHORT).show();//Site doğru şekilde yüklendi
                progress.dismiss();

            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {//Yükleme sırasında hata oluştu
                Toast.makeText(getApplicationContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });

//bir urlyi çağırmasını istiyoruz.

//loaddata metodu ile html kodlarını çalıştırmak da mümkün...

    }
}
