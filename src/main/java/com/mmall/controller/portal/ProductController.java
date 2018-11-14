package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.services.IproductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sun.nio.cs.FastCharsetProvider;

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IproductService iproductService;

    @RequestMapping("/detail.do")
    public ServerResponse<ProductDetailVo> detail(Integer productId){
    return     iproductService.getProductDetail(productId);
    }

    public ServerResponse<PageInfo> list(@RequestParam(value = "orderBy",defaultValue = "") String orderBy,  @RequestParam(value ="keyword",required = false) String keyword, @RequestParam(value = "categoryId",required = false) Integer categoryId,@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue ="10" ) Integer pageSize){
    return iproductService.getProductByKeyWordCategory(orderBy,keyword,categoryId,pageNum,pageSize);

    }
}
