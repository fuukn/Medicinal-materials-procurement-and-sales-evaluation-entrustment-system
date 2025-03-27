package io.renren.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;

import io.renren.common.utils.Query;
import io.renren.modules.sys.dao.PriceWeatherDataDao;
import io.renren.modules.sys.entity.PriceWeatherDataEntity;
import io.renren.modules.sys.service.PriceWeatherDataService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


@Service("priceWeatherDataService")
public class PriceWeatherDataServiceImpl extends ServiceImpl<PriceWeatherDataDao, PriceWeatherDataEntity> implements PriceWeatherDataService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PriceWeatherDataEntity> page = this.page(
                new Query<PriceWeatherDataEntity>().getPage(params),
                new QueryWrapper<PriceWeatherDataEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public void exportToExcel(HttpServletResponse response) {
        // 获取所有数据
        List<PriceWeatherDataEntity> dataList = getAllData();

        // 创建工作簿
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("PriceWeatherData");

            // 创建表头
            String[] headers = {"日期", "价格", "最低温度", "最高温度", "天气",
                    "风向风力", "空气质量", "天气(简)", "平均温度"};
            Row headerRow = sheet.createRow(0);

            // 设置表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int rowNum = 1;

            for (PriceWeatherDataEntity data : dataList) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(dateFormat.format(data.getCreateDate()));
                row.createCell(1).setCellValue(data.getPrice() != null ? data.getPrice().doubleValue() : 0);
                row.createCell(2).setCellValue(data.getLowestTemperature() != null ? data.getLowestTemperature() : 0);
                row.createCell(3).setCellValue(data.getHighestTemperature() != null ? data.getHighestTemperature() : 0);
                row.createCell(4).setCellValue(data.getWeather());
                row.createCell(5).setCellValue(data.getWind());
                row.createCell(6).setCellValue(data.getAirQuality());
                row.createCell(7).setCellValue(data.getSimplifiedWeather());
                row.createCell(8).setCellValue(data.getAverageTemperature() != null ? data.getAverageTemperature() : 0);
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=PriceWeatherData_" +
                    System.currentTimeMillis() + ".xlsx");

            // 写入输出流
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();

        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    @Override
    public List<PriceWeatherDataEntity> getAllData() {
        return baseMapper.selectList(null);
    }
}