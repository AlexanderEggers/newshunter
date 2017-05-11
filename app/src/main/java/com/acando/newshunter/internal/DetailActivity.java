package com.acando.newshunter.internal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toolbar;

import com.acando.newshunter.R;

public class DetailActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(false);
        webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(getIntent().getStringExtra("url"));

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

    setActionBar(toolbar);

        if(

    getActionBar() !=null)

    {
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
