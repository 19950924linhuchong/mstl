package com.mmall.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.model.Shipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int count=  shippingMapper.insert(shipping);
        if(count>0){
            Map map = Maps.newHashMap();
            map.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新增地址成功",map);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    public ServerResponse del(Integer userId, Integer shippingId){
        int count=  shippingMapper.deleteByuserIdandshipId(userId,shippingId);
        if(count>0){
            return  ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int count=  shippingMapper.updateByUserIdandshipping(shipping);
        if(count>0){
            return  ServerResponse.createByErrorMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping =shippingMapper.selectByshippingIdAndUserId(userId,shippingId);
        if(shipping == null){
            return ServerResponse.createByErrorMessage("无法查询该地址失败");
        }
        return  ServerResponse.createBySuccess("查询地址成功",shipping);
    }
    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList =shippingMapper.selectlistByuserid(userId);
        PageInfo pageInfo =new PageInfo(shippingList);
        return  ServerResponse.createBySuccess(pageInfo);

    }
}
