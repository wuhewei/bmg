package com.bmg.mall.service.impl;

import com.bmg.mall.common.Constants;
import com.bmg.mall.common.ServiceResultEnum;
import com.bmg.mall.common.bmgCategoryLevelEnum;
import com.bmg.mall.controller.vo.SearchPageCategoryVO;
import com.bmg.mall.controller.vo.SecondLevelCategoryVO;
import com.bmg.mall.controller.vo.ThirdLevelCategoryVO;
import com.bmg.mall.controller.vo.bmgIndexCategoryVO;
import com.bmg.mall.dao.GoodsCategoryMapper;
import com.bmg.mall.entity.GoodsCategory;
import com.bmg.mall.service.bmgCategoryService;
import com.bmg.mall.util.BeanUtil;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class bmgCategoryServiceImpl implements bmgCategoryService {

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getCategorisPage(PageQueryUtil pageUtil) {
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.findGoodsCategoryList(pageUtil);
        int total = goodsCategoryMapper.getTotalGoodsCategories(pageUtil);
        PageResult pageResult = new PageResult(goodsCategories, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp != null) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        if (goodsCategoryMapper.insertSelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateGoodsCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsCategory temp2 = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp2 != null && !temp2.getCategoryId().equals(goodsCategory.getCategoryId())) {
            //同名且不同id 不能继续修改
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        goodsCategory.setUpdateTime(new Date());
        if (goodsCategoryMapper.updateByPrimaryKeySelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsCategory getGoodsCategoryById(Long id) {
        return goodsCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除分类数据
        return goodsCategoryMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<bmgIndexCategoryVO> getCategoriesForIndex() {
        List<bmgIndexCategoryVO> bmgIndexCategoryVOS = new ArrayList<>();
        //获取一级分类的固定数量的数据
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), bmgCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            //获取二级分类的数据
            List<GoodsCategory> secondLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(firstLevelCategoryIds, bmgCategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                List<Long> secondLevelCategoryIds = secondLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                //获取三级分类的数据
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(secondLevelCategoryIds, bmgCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    //根据 parentId 将 thirdLevelCategories 分组
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream().collect(groupingBy(GoodsCategory::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    //处理二级分类
                    for (GoodsCategory secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        //如果该二级分类下有数据则放入 secondLevelCategoryVOS 对象中
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())) {
                            //根据二级分类的id取出thirdLevelCategoryMap分组中的三级分类list
                            List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategory.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS((BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class)));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    //处理一级分类
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        //根据 parentId 将 thirdLevelCategories 分组
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for (GoodsCategory firstCategory : firstLevelCategories) {
                            bmgIndexCategoryVO bmgIndexCategoryVO = new bmgIndexCategoryVO();
                            BeanUtil.copyProperties(firstCategory, bmgIndexCategoryVO);
                            //如果该一级分类下有数据则放入 bmgIndexCategoryVOS 对象中
                            if (secondLevelCategoryVOMap.containsKey(firstCategory.getCategoryId())) {
                                //根据一级分类的id取出secondLevelCategoryVOMap分组中的二级级分类list
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(firstCategory.getCategoryId());
                                bmgIndexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                bmgIndexCategoryVOS.add(bmgIndexCategoryVO);
                            }
                        }
                    }
                }
            }
            return bmgIndexCategoryVOS;
        } else {
            return null;
        }
    }

    @Override
    public SearchPageCategoryVO getCategoriesForSearch(Long categoryId) {
        SearchPageCategoryVO searchPageCategoryVO = new SearchPageCategoryVO();
        GoodsCategory thirdLevelGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(categoryId);
        if (thirdLevelGoodsCategory != null && thirdLevelGoodsCategory.getCategoryLevel() == bmgCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            //获取当前三级分类的二级分类
            GoodsCategory secondLevelGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(thirdLevelGoodsCategory.getParentId());
            if (secondLevelGoodsCategory != null && secondLevelGoodsCategory.getCategoryLevel() == bmgCategoryLevelEnum.LEVEL_TWO.getLevel()) {
                //获取当前二级分类下的三级分类List
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelGoodsCategory.getCategoryId()), bmgCategoryLevelEnum.LEVEL_THREE.getLevel(), Constants.SEARCH_CATEGORY_NUMBER);
                searchPageCategoryVO.setCurrentCategoryName(thirdLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setSecondLevelCategoryName(secondLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setThirdLevelCategoryList(thirdLevelCategories);
                return searchPageCategoryVO;
            }
        }
        return null;
    }

    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0);//0代表查询所有
    }
}
