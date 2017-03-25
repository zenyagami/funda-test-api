package com.zenkun.fundatest;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zenkun.fundatest.logic.impl.ActionSaleObjectsImpl;
import com.zenkun.fundatest.adapter.AdapterTopTenMakelar;
import com.zenkun.fundatest.model.ModelHouse;
import com.zenkun.fundatest.utilities.UtilRx;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private TextView makelarLabel;
    private TextView makelarTopOne;
    private TextView makerlaTopTenCaption;
    private View root;
    private View loading;
    private Subscription subscriptionTask;
    //endpoint for Tuin filter.
    public boolean isTuin=true;

    private AdapterTopTenMakelar mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makelarTopOne= (TextView) findViewById(R.id.makelar_id);
        makerlaTopTenCaption=(TextView) findViewById(R.id.top_ten_caption);
        root=findViewById(R.id.root_views);
        loading=findViewById(R.id.pb_loading);
        makelarLabel= (TextView) findViewById(R.id.makelar_label);

        RecyclerView mList = (RecyclerView) findViewById(R.id.rv_top_ten);
        mList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mList.setHasFixedSize(true);
        mAdapter = new AdapterTopTenMakelar(null);
        mList.setAdapter(mAdapter);
        //TODO save the List of top ten in a "Cache or savedInstance" in case another request.
        if(savedInstanceState!=null && savedInstanceState.containsKey("isTuin"))
            isTuin= savedInstanceState.getBoolean("isTuin");

        executeMakelarTask(isTuin);

    }


    /**
     *  //I used Rx java to make the asynctask easier and modular.
     * @param isTuin  True when we hit the amsterdam/tuin query
     */
    private void executeMakelarTask(boolean isTuin)
    {

        //remove the subscriptions (running task, to start a new one and avoid concurrent task)
        UtilRx.unsubscribe(subscriptionTask);
        onPreExecute();
        subscriptionTask = new ActionSaleObjectsImpl()
                .withParams(isTuin)
                .observable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onSuccess,this::onError);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //TODO validate if the selected menu value is the same as the one thtat is displaying
        getMenuInflater().inflate(R.menu.menu_main,menu);
        if(isTuin)
            menu.findItem(R.id.action_tuin).setChecked(true);
        else
            menu.findItem(R.id.action_amsterdam).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId())
        {
            case R.id.action_amsterdam:
                isTuin=false;
                executeMakelarTask(false);
                return true;
            case R.id.action_tuin:
                isTuin=true;
                executeMakelarTask(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        //we save the current city we are using as query in case of screen orientation
        //TODO sava the list data using parceleable
        outState.putBoolean("isTuin",isTuin);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void onSuccess(ArrayList<ModelHouse> mapKeys) {
        onPostExecute(mapKeys);
    }

    private void onError(Throwable throwable) {
        Toast.makeText(getApplicationContext(),getString(R.string.error_generic)+" "+throwable.getMessage(),Toast.LENGTH_SHORT).show();
    }


    public void onPreExecute() {
        //instead a dialog of loading i will use a ProgressBar
        loading.setVisibility(View.VISIBLE);
        root.setVisibility(View.GONE);
    }

    public void onPostExecute(ArrayList<ModelHouse> mapKey) {
        loading.setVisibility(View.GONE);
        root.setVisibility(View.VISIBLE);
        if(mapKey==null)
        {
            showErrorMessage();
        }else
        {
            //adapter setup
            if(mapKey.size()>0)
            {
                //we remove the first top , to introduce on our top 1 view
                ModelHouse topOne=mapKey.remove(0);
                makelarTopOne.setText(topOne.makelarName);
            }
            //set adapter with data to display of top 10
            mAdapter.changeDataset(mapKey);

            makerlaTopTenCaption.setText(getString(isTuin?R.string.makelar_top_ten_tuin: R.string.makelar_top_ten_amsterdam));
            makelarLabel.setText(getString(isTuin?R.string.makelar_top_one_tuin: R.string.makelar_top_one_amsterdam));

        }
    }

    @Override
    protected void onDestroy() {
        UtilRx.unsubscribe(subscriptionTask);
        super.onDestroy();
    }

    private void showErrorMessage() {
        loading.setVisibility(View.GONE);
        //Error on the server
        //TODO show the error based on exception, (parse exception,no response and so on)
        Toast.makeText(getApplicationContext(),R.string.error_generic,Toast.LENGTH_SHORT).show();
    }
}
