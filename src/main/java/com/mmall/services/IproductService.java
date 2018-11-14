package com.mmall.services;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.model.Product;
import com.mmall.vo.ProductDetailVo;

public interface IproductService {

    ServerResponse saveorUpdateProduct(Product product);
    ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);
    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize);
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductByKeyWordCategory(String orderBy,String keyWord,Integer categoryId,Integer pageNum,Integer pageSize);
}
