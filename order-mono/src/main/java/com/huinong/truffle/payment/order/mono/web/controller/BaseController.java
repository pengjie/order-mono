package com.huinong.truffle.payment.order.mono.web.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class BaseController {

    public Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    
}
