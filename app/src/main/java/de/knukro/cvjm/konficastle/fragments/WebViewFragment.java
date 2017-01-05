package de.knukro.cvjm.konficastle.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.knukro.cvjm.konficastle.R;


public class WebViewFragment extends Fragment {

    public WebView webview;
    private NestedScrollView scrollView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        Bundle bundle = this.getArguments();
        String curURL = bundle.getString("curl");

        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        scrollView = (NestedScrollView) view.findViewById(R.id.web_scrollview);
        webview = (WebView) view.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new webClient());

        updateUrl(curURL);

        return view;
    }

    private void updateUrl(String url) {
        webview.loadUrl(url);
    }


    private class webClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        public void onPageFinished(WebView view, String url) {
            scrollView.scrollTo(0, 0);
        }
    }

}
