package com.zenkun.fundatest.logic.impl;

import com.zenkun.fundatest.logic.ActionSaleObjects;
import com.zenkun.fundatest.model.ModelHouse;
import com.zenkun.fundatest.utilities.NetworkUtils;
import com.zenkun.fundatest.utilities.SortUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Zen zenyagami@gmail.com on 25/03/2017.
 */

public class ActionSaleObjectsImpl implements ActionSaleObjects{
    //I use RX action to create the background task, is better than asynctask for now.

    private boolean isTuin;

    public ActionSaleObjectsImpl withParams(boolean isTuin) {
        this.isTuin = isTuin;
        return this;
    }


    @Override
    public Observable<ArrayList<ModelHouse>> observable() {
        return Observable.create(subscriber -> {
            try {

                Map<String,ModelHouse> unsorted =NetworkUtils.getSaleTop10(isTuin);

                if(unsorted!=null && unsorted.size()>0)
                {
                    ArrayList<ModelHouse> sorted = (ArrayList<ModelHouse>) SortUtil.sortMapDescending(unsorted);
                    //Sort the array based on highest repeating integers (top 1)
                    subscriber.onNext(sorted);
                    subscriber.onCompleted();
                }else
                {
                    //error
                    subscriber.onError(new Exception());
                }
            }catch (Exception ex)
            {
                ex.printStackTrace();
                subscriber.onError(ex);
            }

        });
    }



}
