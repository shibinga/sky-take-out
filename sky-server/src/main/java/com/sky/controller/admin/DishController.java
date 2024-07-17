package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品: {}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询: {}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除: {}",ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("id查询")
    public Result<DishVO> selectById(@PathVariable Long id){
        log.info("查询id: {}",id);
        DishVO dishVO = dishService.selectById(id);
        return Result.success(dishVO);
    }

    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id){
        log.info("修改状态: {},{}",status,id);
        dishService.changeStatus(status,id);
        return Result.success();
    }
    /**
     * 修改
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改: {}",dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }
}
