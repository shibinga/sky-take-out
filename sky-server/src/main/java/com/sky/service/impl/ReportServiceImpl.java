package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = getDateList(begin,end);
        String dateList = StringUtils.join(localDateList,",");
        List<Double> bigDecimalList = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Map map = new  HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover = orderMapper.sumByMap(map);
            if (turnover == null){
                turnover = 0.0;
            }
            bigDecimalList.add(turnover);
        }
        String turnoverList = StringUtils.join(bigDecimalList,",");

        return new TurnoverReportVO(dateList,turnoverList);
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = getDateList(begin,end);
        String dateList = StringUtils.join(localDateList,",");

        List<Integer> totalUsers = new ArrayList<>();
        List<Integer> newUsers = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Map map =new HashMap<>();
            map.put("end",endTime);
            Integer totalUser = userMapper.sumUserByMap(map);
            totalUsers.add(totalUser);
            map.put("begin",beginTime);
            Integer newUser = userMapper.sumUserByMap(map);
            newUsers.add(newUser);
        }
        String newUserList = StringUtils.join(newUsers, ",");
        String totalUserList = StringUtils.join(totalUsers, ",");

        return UserReportVO.builder()
                .dateList(dateList)
                .newUserList(newUserList)
                .totalUserList(totalUserList)
                .build();
    }
    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO orderReportVO(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = getDateList(begin,end);
        String dateList = StringUtils.join(localDateList,",");

        List<Integer> orderCounts = new ArrayList<>();
        List<Integer> validOrderCounts = new ArrayList<>();


        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Integer orderCount = getNum(beginTime,endTime,null);
            orderCounts.add(orderCount);
            Integer validOrder = getNum(beginTime,endTime,Orders.COMPLETED);
            validOrderCounts.add(validOrder);
        }
        Integer totalOrderCount = orderCounts.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCounts.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(dateList)
                .orderCompletionRate(orderCompletionRate)
                .validOrderCount(validOrderCount)
                .orderCountList(StringUtils.join(orderCounts,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCountList(StringUtils.join(validOrderCounts,","))
                .build();
    }

    /**
     * 查询销量排名top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO salesTop10ReportVO(LocalDate begin, LocalDate end) {
        if (begin.isAfter(end)){
            throw new OrderBusinessException(MessageConstant.UNKNOWN_ERROR);
        }
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesList = orderDetailMapper.SalesTop10(beginTime,endTime);
        if (goodsSalesList == null || goodsSalesList.size() == 0){throw new OrderBusinessException(MessageConstant.UNKNOWN_ERROR);}
            List<String> names = goodsSalesList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
            List<Integer> numbers = goodsSalesList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
            String nameList = StringUtils.join(names, ",");
            String numberList = StringUtils.join(numbers, ",");
        return new SalesTop10ReportVO(nameList,numberList);
    }

    /**
     * 导出Excel报表接口
     */
    @Override
    public void outReport(HttpServletResponse response) {
        //获得数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //读取模板
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        //创建excel
        try {
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);

            //获得表
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //填入数据
            sheet.getRow(1).getCell(1).setCellValue("时间："+ begin + "——" +end);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            for (int i = 0;i < 30; i++){
                LocalDate days = begin.plusDays(i);
                BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(days, LocalTime.MIN), LocalDateTime.of(days, LocalTime.MAX));

                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(String.valueOf(days));
                row.getCell(2).setCellValue(businessDataVO.getTurnover());
                row.getCell(3).setCellValue(businessDataVO.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataVO.getUnitPrice());
                row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            }




            //导出excel
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            //关闭流
            outputStream.close();
            excel.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取时间
     * @param begin
     * @param end
     * @return
     */
    public List<LocalDate> getDateList(LocalDate begin, LocalDate end){
        if (begin.isAfter(end)){
            throw new OrderBusinessException(MessageConstant.UNKNOWN_ERROR);
        }
        List<LocalDate> localDateList = new ArrayList<>();
        for (LocalDate i=begin; !i.isAfter(end); i = i.plusDays(1)){
            localDateList.add(i);
        }
        return localDateList;
    }

    /**
     * 获取订单数
     * @param begin
     * @param end
     * @param status
     * @return
     */
    public Integer getNum(LocalDateTime begin,LocalDateTime end, Integer status){
        Map map = new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
        Integer num = orderMapper.sumOrderByMap(map);
        return num;
    }
}
