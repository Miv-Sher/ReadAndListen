package com.miv_sher.readlistening;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.w3c.dom.Attr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import company.WordsInfo;

/**
 * Created by Anna on 7/9/2015.
 */

    public class BookView extends WebView {
    private Context context;

    public ReaderActivity readerActivity;


    private String plainText;
    /**
     * Construct a new WebView with layout parameters.
     * @param context A Context object used to access application assets.
     * @param attrs An AttributeSet passed to our parent.
     */
    public BookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        WebSettings webviewSettings = getSettings();
        webviewSettings.setJavaScriptEnabled(true);
        // add JavaScript interface for copy
        addJavascriptInterface(new WebAppInterface(context), "JSInterface");
    }

    /**
     * Construct a new WebView with layout parameters and a default style.
     * @param context A Context object used to access application assets.
     * @param attrs An AttributeSet passed to our parent.
     * @param defStyle The default style resource ID.
     */
    public BookView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        WebSettings webviewSettings = getSettings();
        webviewSettings.setJavaScriptEnabled(true);
        // add JavaScript interface for copy
        addJavascriptInterface(new WebAppInterface(context), "JSInterface");

    }
    @TargetApi(22)
    public BookView(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle, defStyleRes);
        this.context = context;
        WebSettings webviewSettings = getSettings();
        webviewSettings.setJavaScriptEnabled(true);
        // add JavaScript interface for copy
        addJavascriptInterface(new WebAppInterface(context), "JSInterface");

    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);

    }

    String htmlData;
    public void loadBook(String Path) {
        htmlData = readFile(Path);
        htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" + htmlData;
        loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null);
    }
    public  void parsePlainText() {
        plainText = Jsoup.parse(htmlData).text();
    }

    public String getPlainText() {
        if (plainText != null) {
            parsePlainText();
        }
        return plainText;
    }

    public String readFile(String FilePath) {
        BufferedReader br = null;
        String result = null;
        try {
            InputStream is = getContext().getAssets().open(FilePath);
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            result = sb.toString();
            br.close();
        } catch (Exception ex) {
            int stop = 4;
        }
        return result;
    }

    // override all other constructor to avoid crash
    public BookView(Context context) {
        super(context);
        this.context = context;
        WebSettings webviewSettings = getSettings();
        webviewSettings.setJavaScriptEnabled(true);
        // add JavaScript interface for copy
        addJavascriptInterface(new WebAppInterface(context), "JSInterface");
    }


    // setting custom action bar
    private ActionMode mActionMode;
    private ActionMode.Callback mSelectActionModeCallback;
    private GestureDetector mDetector;

    // this will over ride the default action bar on long press
    @Override
    public ActionMode startActionMode(android.view.ActionMode.Callback callback) {
        ViewParent parent = getParent();
        if (parent == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String name = callback.getClass().toString();
            if (name.contains("SelectActionModeCallback")) {
                mSelectActionModeCallback = callback;
               // mDetector = new GestureDetector(context,
                //        new CustomGestureListener());
            }
        }
        CustomActionModeCallback mActionModeCallback = new CustomActionModeCallback();
        return parent.startActionModeForChild(this, mActionModeCallback);
    }

    private class CustomActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mActionMode = mode;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_select_text, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.copy:
                    getSelectedData();
                    mode.finish();
                    return true;
                case R.id.play:
                    if (readerActivity.isReady) {
                        getSelectedData();
                        ClipboardManager clipboard = (ClipboardManager)
                                getContext().getSystemService(Context.CLIPBOARD_SERVICE);


                        String selected = (String)clipboard.getText();
                        notifyPlay(selected);
                        mode.finish();

                        //because i cant do it normally. wtf?
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            getSelectedData();
                            ClipboardManager clipboard1 = (ClipboardManager)
                                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            String selected1 = (String)clipboard1.getText();
                            notifyPlay(selected1);
                            mode.finish();
                        }
                    }
                    else {
                        Toast.makeText(getContext(), "Wait for loading to finish", Toast.LENGTH_LONG).show();
                    }
                    return true;
                default:
                    mode.finish();
                    return false;
            }
        }

        private void notifyPlay(String selected) {
            readerActivity.findInAudio(selected);
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                clearFocus();
            } else {
                if (mSelectActionModeCallback != null) {
                    mSelectActionModeCallback.onDestroyActionMode(mode);
                }
                mActionMode = null;
            }
        }
    }


    private void getSelectedData() {
        String js= "(function getSelectedText() {"+
                "var txt;"+
                "if (window.getSelection) {"+
                "txt = window.getSelection().toString();"+
                "} else if (window.document.getSelection) {"+
                "txt = window.document.getSelection().toString();"+
                "} else if (window.document.selection) {"+
                "txt = window.document.selection.createRange().text;"+
                "}"+
                "JSInterface.getText(txt);"+
                "})()";
        // calling the js function
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:"+js, null);
        }else{
            loadUrl("javascript:"+js);
        }
    }

    private class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mActionMode != null) {
                mActionMode.finish();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Send the event to our gesture detector
        // If it is implemented, there will be a return value
        if (mDetector != null)
            mDetector.onTouchEvent(event);
        // If the detected gesture is unimplemented, send it to the superclass
        return super.onTouchEvent(event);
    }
}

class WebAppInterface {
    Context mContext;

    WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void getText(String text) {
        // put selected text into clipdata
        ClipboardManager clipboard = (ClipboardManager)
                mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        //ClipData clip = ClipData.newPlainText("simple text",text);
        //clipboard.setPrimaryClip(clip);
        clipboard.setText(text);
        // gives the toast for selected text
        //Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}

