package com.carmotors.sgt.service.observer;

import com.carmotors.sgt.model.StockRepuesto;


public interface StockObserver {


    void onStockBajo(StockRepuesto stock);
}
