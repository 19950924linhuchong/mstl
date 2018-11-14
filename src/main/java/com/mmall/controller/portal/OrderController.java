package com.mmall.controller.portal;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.model.User;
import com.mmall.services.IOrderService;
import com.sun.xml.internal.ws.resources.HttpserverMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {

    private  static  final Logger logger =LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private IOrderService iOrderService;

    /**
     * 支付接口
     */
    @RequestMapping("pay.do")
    @ResponseBody
   public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
       User user = (User)session.getAttribute(Const.CURRENT_USER);
       if(user ==null){
           return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
       }
       String path =request.getServletContext().getRealPath("upload");
       return iOrderService.pay(orderNo,user.getId(),path);
   }

   @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallBack(HttpServletRequest request){
       Map<String,String> params= Maps.newHashMap();

       Map requestParams=request.getParameterMap();
       for(Iterator iter =requestParams.keySet().iterator();iter.hasNext();){
           String name=(String) iter.next();
           String [] value=(String[])requestParams.get(name);
           String valueStr="";
           for(int i=0;i<value.length;i++){
               valueStr =(i==value.length-1)?valueStr +value[i]:valueStr +value[i]+",";
           }
       params.put(name,valueStr);
       }
       logger.info("支付宝回调,sign:{},trade_status:{},参数:{}");

       return null;
   }
}
