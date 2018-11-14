package com.mmall.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.model.Category;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;


@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("消费品类参数错误");
        }
        Category category =new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int count =categoryMapper.insert(category);
        if(count>0){
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category =new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int count = categoryMapper.updateByPrimaryKeySelective(category);
        if(count>0){
        return  ServerResponse.createBySuccess("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category>  categoryList =categoryMapper.selectCategeoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("没找到当前分类的子分类");
        }

        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归算法返回id集合
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId !=null){
            for(Category categoryItem :categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    /**
     * 递归自己调用自己
     * @param categorySet
     * @param categoryId
     * @return
     */
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){
        Category category =categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        //查找子节点 一定要有一个退出条件
        List<Category> categoryList =categoryMapper.selectCategeoryChildrenByParentId(categoryId);
        for(Category categoryItem :categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;

    }
}
