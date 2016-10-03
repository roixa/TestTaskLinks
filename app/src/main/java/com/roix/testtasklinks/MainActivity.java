package com.roix.testtasklinks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
                                                        RetrieveLinks.RetrieveLinksResult,
                                                            AdapterView.OnItemClickListener,
                                                                TextWatcher{
    private ArrayList<String> urls=null;
    private Button button;
    private EditText editText;
    private ListView listView;
    private ProgressBar progressBar;
    private static final int PAGE_NOT_FOUND=0;
    private static final int NOT_CONNECTED=1;


    private String tempUrl="http://developer.alexanderklimov.ru/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        button=(Button)findViewById(R.id.button);
        button.setOnClickListener(this);
        progressBar=(ProgressBar)findViewById(R.id.toolbar_progress_bar);
        progressBar.setVisibility(View.GONE);
        editText=(EditText)findViewById(R.id.edit_url);
        editText.addTextChangedListener(this);
        listView=(ListView)findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);

        //prepare saved data
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> saveSet =prefs.getStringSet("urls", null);
        if(saveSet!=null){
            urls=new ArrayList<>(saveSet);
            setList();
        }
        String currUrl=prefs.getString("curr_url","");

        if(currUrl.isEmpty())button.setEnabled(false);

        editText.setText(currUrl);

    }

    //in onPause save data
    @Override
    protected void onPause() {
        super.onPause();
        Set<String> saveSet=new HashSet<>();
        saveSet.addAll(urls);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("curr_url",editText.getText().toString());
        editor.putStringSet("urls",saveSet).commit();
    }


    private void setList(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, urls);
        listView.setAdapter(adapter);
    }





    private void showErrorToast(final int errType){
        final Context c=this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text;
                if (errType == PAGE_NOT_FOUND) {
                    text = c.getString(R.string.pageNotFound);
                } else if (errType == NOT_CONNECTED) {
                    text = c.getString(R.string.notConnected);
                } else return;
                Toast.makeText(c, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    //handle button click
    @Override
    public void onClick(View v) {
        if(isOnline()) {
            String url = editText.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            new RetrieveLinks(this).execute(url);
        }
        else{
            showErrorToast(NOT_CONNECTED);
        }
    }

    //response from retrieving asynctask
    @Override
    public void onSuccess( ArrayList<String> strings) {
        urls = strings;
        progressBar.setVisibility(View.GONE);
        setList();
    }

    @Override
    public void onError() {
        showErrorToast(PAGE_NOT_FOUND);
    }

    //for listview
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String newUrl=urls.get(position);
        editText.setText(newUrl);
        boolean isUrl= URLUtil.isValidUrl(newUrl);
        button.setEnabled(isUrl);
    }


    //for listen text change in edittext
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean isUrl= URLUtil.isValidUrl((s)+"");
        button.setEnabled(isUrl);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
