package com.mmall.services;

import com.mmall.common.ServerResponse;

public interface IOrderService {

    ServerResponse pay(Long orderNo, Integer userId, String path);
}
