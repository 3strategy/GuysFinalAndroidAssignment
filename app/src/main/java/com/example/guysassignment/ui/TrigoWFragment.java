/// Prompting: Guy S, on 4o: https://chatgpt.com/share/680345f8-2730-800e-93dc-eaed2a4150c3
// Puspose: host a local webView that was created in March/April 2025 that interacts with firebase
/// , continued on 4o-mini-high: https://chatgpt.com/share/68034575-9bcc-800e-abcb-86be4ecae071
/// personal link: https://chatgpt.com/c/6802507d-21c4-800e-9102-12ad1933807a

package com.example.guysassignment.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.guysassignment.R;
import com.example.guysassignment.SharedViewModel;
import android.os.Handler;
import android.os.Looper;

// To expose Java methods to your WebView’s JavaScript
import android.webkit.JavascriptInterface;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrigoWFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrigoWFragment extends Fragment {

    private SharedViewModel sharedVM;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrigoWFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrigoWFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrigoWFragment newInstance(String param1, String param2) {
        TrigoWFragment fragment = new TrigoWFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Scoped to the Activity so it's shared across all fragments
        sharedVM = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 1. inflate your layout
        View view = inflater.inflate(R.layout.fragment_trigo_w, container, false);

        // 2. find your WebView
        WebView web = view.findViewById(R.id.webview);

        // 3. enable JavaScript if your HTML app needs it
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        // 4. ensure links load in the WebView, not in the browser
        web.setWebViewClient(new WebViewClient());

        //4b connect with JSBridge to allowe writing to local storage database:
        web.addJavascriptInterface(
                new JSBridge(sharedVM),   // your SharedViewModel instance
                "Android"                 // the name JS will see
        );

        // 4c enable debugging web:
        web.setWebContentsDebuggingEnabled(true);


        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage msg) {
                Log.d("WebViewJS", msg.message() + " -- From line "
                        + msg.lineNumber() + " of " + msg.sourceId());
                return true;
            }
        });


        // 5. load your HTML app
        //    – if it lives in app/src/main/assets/myapp/index.html:
        web.loadUrl("file:///android_asset/myapp/ttrainer.html");
        //    – or load a remote URL:
        //web.loadUrl("https://מתמטיקה.com/ttrainer");

        return view;
    }


    private class JSBridge {
        private final SharedViewModel vm;

        JSBridge(SharedViewModel vm) {
            this.vm = vm;
        }

        @JavascriptInterface
        public void updateLocalScore(int score) {
            // this runs on a background thread, so post to main:
            new Handler(Looper.getMainLooper()).post(() -> {
                vm.setBestScore(score);
            });
        }
    }


}