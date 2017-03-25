package com.zenkun.fundatest.logic;


import rx.Observable;
/**
 * Created by Zen zenyagami@gmail.com on 25/03/2017.
 */
public interface Action<ReturnType> {

  Observable<ReturnType> observable();
}