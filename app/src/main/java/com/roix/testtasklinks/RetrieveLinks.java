package com.roix.testtasklinks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by u5 on 10/1/16.
 */
public class RetrieveLinks extends AsyncTask<String,Void,ArrayList<String>>{

    private RetrieveLinksResult result;

    public RetrieveLinks(RetrieveLinksResult result){
        this.result=result;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        String url=params[0];
        ArrayList<String> ret=new ArrayList<>();
        try {
            Document doc  = Jsoup.connect(url).get();
            Elements links = doc.getElementsByTag("a");
            for (Element link : links) {
                String linkHref = link.attr("href");
                String linkText = link.text();
                ret.add(linkHref);
            }

        } catch (IOException e) {//if there is not html page
            result.onError();
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        result.onSuccess(strings);
    }

    public interface RetrieveLinksResult{
        void onSuccess(ArrayList<String> strings);
        void onError();
    }
}
