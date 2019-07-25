package com.pinyougou.common.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportExcel {


    /**
     * @param dataList 数据集
     * @param dataType 数据对应的Pojo
     * @param fileName 文件名
     * @param response 设置响应输出格式与响应输出流
     * @param <T>
     * @throws IllegalAccessException
     * @throws IOException            该方法采用反射
     *                                每个POJO都对应一个数据库以及一个实体对象
     *                                将POJO设置为Excel对应的列名
     *                                用数据集进行填充
     */
    public <T> void importExcel(List<T> dataList, Class<T> dataType, String fileName, HttpServletResponse response) throws IllegalAccessException, IOException {
        Field[] fields = dataType.getDeclaredFields();
        Workbook workbook = new XSSFWorkbook();
        Sheet rows = workbook.createSheet();
        int rowCount = 0;

        Row indexRow = rows.createRow(rowCount++);
        for (int i = 0; i < fields.length; i++) {
            indexRow.createCell(i).setCellValue(fields[i].getName());
        }

        for (T t : dataList) {
            int cellCount = 0;
            Row row = rows.createRow(rowCount++);
            for (Field field : fields) {
                field.setAccessible(true);
                Object o = field.get(t);
                row.createCell(cellCount++).setCellValue(String.valueOf(o));
            }
        }

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + new String(fileName.getBytes("gb2312"), "ISO8859-1"));

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.close();
    }

    /**
     * @param file  Excle文件 注意 此处未做判断
     * @param type  数据库对应的POJO对象
     * @param <T>   约定的泛型
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * 通过正则表达式获取对应的setter方法以方便后续执行数据填充
     * 将数据填充到对应的POJO对象后加入List随后进行批量插入操作
     * 注意事项:
     *      应约束好用户的列名,防止无法填充数据导致的错误
     */
    public <T> List<T> importDataForExcel(File file, Class<T> type) throws IOException, InvalidFormatException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Method[] methods = type.getMethods();
        Map<String, Method> methodMap = new HashMap<>();
        for (Method method : methods) {
            if (method.getName().matches("set\\w+")) {
                methodMap.put(method.getName().replace("set", "").toLowerCase(), method);
            };
        }

        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<String> excelFiledList = new ArrayList<>();
        ArrayList<T> resultList = new ArrayList<>();
        for (Row cells : sheet) {
            int cellNum = cells.getLastCellNum();
            if (cells.getRowNum() == 0) {
                for (int i = 0; i < cellNum; i++) {
                    String value = cells.getCell(i).getStringCellValue();
                    excelFiledList.add(value);
                }
            } else {
                T t = type.newInstance();
                for (int i = 0; i < cellNum; i++) {
                    String value;
                    try{
                        value = cells.getCell(i).getStringCellValue();
                    }catch (Exception e){
                        value = String.valueOf((long)cells.getCell(i).getNumericCellValue());
                    }

                    Method method = methodMap.get(
                            excelFiledList.get(i).toLowerCase()
                    );
                    Class<?> parameterType = method.getParameterTypes()[0];
                    method.invoke(
                            t,
                            parameterType.getConstructor(String.class).newInstance(value)
                    );
                }
                resultList.add(t);
            }
        }
        return resultList;
    }

    /**
     *
     * @param fo  用户上传的excel对象
     * @return 返回复合对象
     * @throws IOException
     * @throws InvalidFormatException
     *  导入的API要求用户传入的数据必须合法
     *  即数据格式合法 与pojo对应的字段合法
     *
     */
    public List<Map<String,String>> importDataForExcel(File fo) throws IOException, InvalidFormatException {
        List<Map<String, String>> resultList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(fo);
        Sheet sheet = workbook.getSheetAt(0);
        List<String> excelFiledList = new ArrayList<>();
        for (Row cells : sheet) {
            int cellNum = cells.getLastCellNum();
            if (cells.getRowNum() == 0) {
                for (int i = 0; i < cellNum; i++) {
                    String value = cells.getCell(i).getStringCellValue();
                    excelFiledList.add(value);
                }
            } else {
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < cellNum; i++) {
                    String value;
                    try{
                        value = cells.getCell(i).getStringCellValue();
                    }catch (Exception e){
                        value = String.valueOf((long)cells.getCell(i).getNumericCellValue());
                    }
                    map.put(excelFiledList.get(i),value );
                }
                resultList.add(map);
            }
        }
        return resultList;
    }
}
