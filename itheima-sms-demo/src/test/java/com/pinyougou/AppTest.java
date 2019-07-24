package com.pinyougou;

import static org.junit.Assert.assertTrue;

import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
@ContextConfiguration(locations = "classpath:spring/applicationContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class AppTest {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Test
    public void shouldAnswerWithTrue() throws IOException, IllegalAccessException {

        Class<?> tb = TbBrand.class;
        Field[] fields = tb.getDeclaredFields();

        List<TbBrand> tbBrands = tbBrandMapper.selectAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet rows = workbook.createSheet();
        int rowCount = 1;
        for (TbBrand tbBrand : tbBrands) {
            int cellCount = 0;
            if (rowCount == 1){
                Row indexRow = rows.createRow(rowCount++);
                for (int i = 0; i < fields.length; i++) {
                    indexRow.createCell(cellCount).setCellValue(fields[cellCount++].getName());
                }
            }else{
                Row row = rows.createRow(rowCount++);
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object o = field.get(tbBrand);
                    row.createCell(cellCount++).setCellValue(String.valueOf(o));
                }
            }

        }
        FileOutputStream os = new FileOutputStream(new File("F:\\partner_project\\itheima-sms-demo\\src\\excetl\\test.xls"));
        workbook.write(os);
    }

    @Test
    public void importData() throws IOException, InvalidFormatException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> type = TbBrand.class;
        Field[] fields = type.getDeclaredFields();
        Method[] methods = type.getMethods();
        Map<String, Method> methodMap = new HashMap<>();
        for (Method method : methods) {
            if (method.getName().matches("set\\w+")){
                methodMap.put(method.getName().replace("set","" ).toLowerCase(), method);
            };
        }

        Workbook workbook = new XSSFWorkbook(new File("C:\\Users\\L1455013965\\Desktop\\brands.xls"));
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<String> excelFiledList = new ArrayList<>();
        List<TbBrand> brands = new ArrayList<>();
        for (Row cells : sheet) {
            int cellNum = cells.getLastCellNum();
            if (cells.getRowNum() == 0){
                for (int i = 0; i < cellNum; i++) {
                    String value = cells.getCell(i).getStringCellValue();
                    excelFiledList.add(value);
                }
            }else{
                Object o = type.newInstance();
                for (int i = 0; i < cellNum; i++) {
                    String value = cells.getCell(i).getStringCellValue();
                    String FiledName = excelFiledList.get(i);
                    Method method = methodMap.get(FiledName.toLowerCase());
                    Class<?> parameterType = method.getParameterTypes()[0];
                    method.invoke(o,parameterType.getConstructor(String.class).newInstance(value));
                }
                brands.add((TbBrand) o);
            }
        }
    }

    @Test
    public void mahfsd(){
        System.out.println("setName".replace("set","").toLowerCase());
    }


}
