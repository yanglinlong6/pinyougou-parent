package com.pinyougou.common.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class ImportExcel {


    /**
     *
     * @param dataList  数据集
     * @param dataType  数据对应的Pojo
     * @param fileName  文件名
     * @param response  设置响应输出格式与响应输出流
     * @param <T>
     * @throws IllegalAccessException
     * @throws IOException
     * 该方法采用反射
     * 每个POJO都对应一个数据库以及一个实体对象
     * 将POJO设置为Excel对应的列名
     * 用数据集进行填充
     */
    public <T> void  importExcel(List<T> dataList, Class<T> dataType, String fileName, HttpServletResponse response) throws IllegalAccessException, IOException {
        Field[] fields = dataType.getDeclaredFields();
        Workbook workbook = new XSSFWorkbook();
        Sheet rows = workbook.createSheet();
        int rowCount = 0;

        for (T t : dataList) {
            int cellCount = 0;
            if (rowCount == 0){
                Row indexRow = rows.createRow(rowCount++);
                for (int i = 0; i < fields.length; i++) {
                    indexRow.createCell(cellCount).setCellValue(fields[cellCount++].getName());
                }
            }else{
                Row row = rows.createRow(rowCount++);
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object o = field.get(t);
                    row.createCell(cellCount++).setCellValue(String.valueOf(o));
                }
            }
        }

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=\""+new String(fileName.getBytes("gb2312"),"ISO8859-1"));

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.close();
    }



}
