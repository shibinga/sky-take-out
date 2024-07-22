package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐管理")
@Slf4j
public class setmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmeal", key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐: {}",setmealDTO);
        setmealService.saveSetmealWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult>page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询: {}",setmealPageQueryDTO);
       PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除")
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        log.info("套餐删除：{}",ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("id查询")
    public Result<SetmealVO> selectById(@PathVariable Long id){
        log.info("id查询: {}",id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("套餐修改: {}",setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改状态")
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public Result changeStatus(@PathVariable Integer status, Long id){
        log.info("修改状态: {},{}",status,id);
        setmealService.changeStatus(status,id);
        return Result.success();
    }
}
