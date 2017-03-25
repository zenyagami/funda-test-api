package com.zenkun.fundatest.utilities;

import android.net.Uri;
import android.util.Log;

import com.zenkun.fundatest.model.ModelHouse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG =NetworkUtils.class.getSimpleName();
    private static final String FUNDA_ENDPOINT ="http://partnerapi.funda.nl/feeds/Aanbod.svc/json/a001e6c3ee6e4853ab18fe44cc1494de/?type=koop&zo=%s&page=%s&pagesize=25";
    private static final String SEARCH_QUERY_AMSTERDAM="/amsterdam/";
    private static final String SEARCH_QUERY_AMSTERDAM_TUIN="/amsterdam/tuin/";


    /**
     * We are using this approach due api restrictions (for now)
     * Iteration between pages and objects to search the top makelar id.
     * this is O(n*n) time complexity,
     * maybe for faster result I could use just a regex, but it will not be the best code.
     * @param isTuinQuery if True ,SEARCH_QUERY_AMSTERDAM_TUIN will be used, otherwise SEARCH_QUERY_AMSTERDAM param
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static Map<String,ModelHouse> getSaleTop10(boolean isTuinQuery) throws IOException, JSONException {

        int currentPage=0;
        int totalPages;
        Map<String,ModelHouse> cache = new HashMap<>();

        do {
            //we can use Uri.Parse and builUpond for every key, but for now is easier for this test. Uri.parse("").buildUpon().appendQueryParameter()...
            //current page cannot be increased by 50 or more in the query so every response will bring 25 list object
            //Even if i use VolgendeUrl or pagesize=50 or higher only returns 25 in the list, that's why im using same URL instead VolgendeUrl
            final String Url = String.format(FUNDA_ENDPOINT,isTuinQuery? SEARCH_QUERY_AMSTERDAM_TUIN: SEARCH_QUERY_AMSTERDAM,currentPage+1);
            JSONObject response = new JSONObject(getResponseFromHttpUrl(new URL(Url)));

            currentPage = response.getJSONObject("Paging").getInt("HuidigePagina");
            totalPages = response.getJSONObject("Paging").getInt("AantalPaginas");
            //we can use  Regex and Match groups but it will not be the best API....
            JSONArray array = response.getJSONArray("Objects");
            Log.i(TAG,"Array size: "+array.length()+ " currentPage:"+currentPage+" of "+totalPages);
            for(int i=0;i<array.length();i++)
            {
                String id = array.getJSONObject(i).getString("MakelaarId");
                if(cache.containsKey(id))
                {
                    // we increment the counter of the makelar
                    ModelHouse house = cache.get(id);
                    house.amountOfSales++;
                    cache.put(id,house);
                }else {
                    ModelHouse modelHouse = new ModelHouse();
                    modelHouse.amountOfSales=1;
                    modelHouse.makelarId = id;
                    modelHouse.makelarName = array.getJSONObject(i).getString("MakelaarNaam");
                    cache.put(id, modelHouse);
                }
            }
            //we can use VolgendeUrl for next Url, but seems it can work changing only the page
        }while (currentPage<totalPages);


        return cache;
    }



    //HTTP request, we can use okHtttp with retrofit but for just one request i will stick with Java http connection.
    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.i(TAG,"using this url:"+ url.toString());
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
