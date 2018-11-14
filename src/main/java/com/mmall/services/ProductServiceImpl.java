package com.mmall.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.model.Category;
import com.mmall.model.Product;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IproductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse saveorUpdateProduct(Product product){
        if(product != null){
            if(StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
                if(product.getId()!=null){
                 int count=   productMapper.updateByPrimaryKey(product);
                 if(count>0){
                     return ServerResponse.createBySuccess("更新产品成功");
                 }else {
                     return ServerResponse.createByErrorMessage("更新产品失败");
                 }
                }else{
                  int count=  productMapper.insert(product);
                    if(count>0){
                        return ServerResponse.createBySuccess("新增产品成功");
                    }else {
                        return ServerResponse.createByErrorMessage("新增产品失败");
                    }
                }
            }

        return  ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId == null || status == null){
             return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product =new Product();
        product.setId(productId);
        product.setStatus(status);
        int result=    productMapper.updateByPrimaryKeySelective(product);
        if(result >0){
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product =productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("商品已下架或删除");
        }
        ProductDetailVo productDetailVo =assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    /**
     * 分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);//初始化参数
        List<Product> productList =productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVo productListVo =assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);//重更新数据
             return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http:47.104.142.150/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);//初始化参数
        if(StringUtils.isNotBlank(productName)){
            productName =new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList =productMapper.selectByNameorProductId(productName,productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVo productListVo =assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);//重更新数据
        return ServerResponse.createBySuccess(pageResult);
    }

    //前台
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product =productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("商品已下架或删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("商品已下架或删除");
        }
        ProductDetailVo productDetailVo =assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public  ServerResponse<PageInfo> getProductByKeyWordCategory(String orderBy,String keyWord,Integer categoryId,Integer pageNum,Integer pageSize){
        if(StringUtils.isBlank(keyWord) && categoryId != null){
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList=new ArrayList<Integer>();
        if(categoryId !=null){
            Category category =categoryMapper.selectByPrimaryKey(categoryId);
            if(category ==null && StringUtils.isBlank(keyWord)){
                //返回一個空結果集
                PageHelper.startPage(pageNum,pageSize);//初始化参数
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageResult=new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageResult);
            }
            categoryIdList=iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyWord)){
            keyWord =new StringBuilder().append("%").append(keyWord).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);//初始化参数
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String [] orderbyArr=orderBy.split("_");
                PageHelper.orderBy(orderbyArr[0]+" "+orderbyArr[1]);
            }
        }
        List<Product> productList =productMapper.selectByNameAndCategoryId(StringUtils.isBlank(keyWord)?null:keyWord,categoryIdList.size()==0?null:categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product:productList){
            ProductListVo productListVo =assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);//重更新数据
        return ServerResponse.createBySuccess(pageInfo);
    }

}
