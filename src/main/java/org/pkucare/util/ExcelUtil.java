package org.pkucare.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.pkucare.annotation.ExcelExport;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ExcelUtil {

    /**
     * 导出excel
     *
     * @param fileName
     * @param sheetName
     * @param response
     */
    public static <T> void export(String fileName, String sheetName, List<T> dataList, HttpServletResponse response) {

        OutputStream outputStream;
        try {
            if (dataList == null || dataList.size() == 0) {
                response.reset();
                response.setContentType("text/html charset=UTF-8");
                response.setCharacterEncoding("utf-8");
                response.getWriter().write("<script>");
                response.getWriter().write("alert('当前无数据可供导出!')");
                response.getWriter().write("</script>");
            } else {
                Class clzz = dataList.get(0).getClass();
                Field[] fields = clzz.getDeclaredFields();
                Field[] columnFields = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(ExcelExport.class)).toArray(Field[]::new);

                String exportFileName = fileName + DateFormatUtil.format(new Date(), "yyyyMMddHHmm");
                // 清空输出流
                response.reset();
                //设定输出文件头
                response.setHeader("Content-disposition", "attachment; filename=" + new String(exportFileName.getBytes("utf-8"),"iso-8859-1") +".xls");
                // 定义输出类型
                Workbook wb = new HSSFWorkbook();
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                outputStream = response.getOutputStream();
                Font headFont = wb.createFont();
                // 大小
                headFont.setFontHeightInPoints((short) 12);
                //粗体显示
                headFont.setBold(true);
                headFont.setFontName("黑体");
                CellStyle headStyle = wb.createCellStyle();
                headStyle.setFont(headFont);
                //设置单元格水平方向对其方式
                headStyle.setAlignment(HorizontalAlignment.CENTER);
                //设置单元格垂直方向对其方式
                headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                headStyle.setBorderTop(BorderStyle.THIN);
                headStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                // 左边边框
                headStyle.setBorderLeft(BorderStyle.THIN);
                // 左边边框颜色
                headStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                // 右边边框
                headStyle.setBorderRight(BorderStyle.THIN);
                // 右边边框颜色
                headStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                // 下边框
                headStyle.setBorderBottom(BorderStyle.THIN);
                // 下边框颜色
                headStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                headStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                // 前景色
                headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                // 自动换行
                headStyle.setWrapText(true);


                //XLS内容样式body style
                CellStyle bodyStyle = wb.createCellStyle();
                bodyStyle.setAlignment(HorizontalAlignment.CENTER);
                bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                // 左边边框
                bodyStyle.setBorderLeft(BorderStyle.THIN);
                // 左边边框颜色
                bodyStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                // 右边边框
                bodyStyle.setBorderRight(BorderStyle.THIN);
                // 右边边框颜色
                bodyStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                // 下边框
                bodyStyle.setBorderBottom(BorderStyle.THIN);
                // 下边框颜色
                bodyStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                // 自动换行
                bodyStyle.setWrapText(true);
                //创建sheet
                Sheet sheet = wb.createSheet(sheetName);
                //生成XLS sheet头 头标题
                Row sheetHeadRow = sheet.createRow(0);
                sheetHeadRow.setHeight((short) 400);
                for (int i = 0; i < columnFields.length; i++) {
                    Cell cell = sheetHeadRow.createCell(i);
                    cell.setCellValue(columnFields[i].getAnnotation(ExcelExport.class).value());
                    cell.setCellStyle(headStyle);
                }
                for (int i = 0; i < dataList.size(); i++) {
                    // sheet页数据行
                    Row sheetRow = sheet.createRow(i + 1);
                    for (int j = 0; j < columnFields.length; j++) {
                        Cell cell = sheetRow.createCell(j);
                        String dataValue = getStringValueFromObj(columnFields[j], dataList.get(i));
                        if (null != dataValue) {
                            cell.setCellValue(dataValue.toString());
                            cell.setCellStyle(bodyStyle);
                        } else {
                            ////没有内容时也能生成XLS的边框格子美观些
                            cell.setCellValue("");
                            cell.setCellStyle(bodyStyle);
                        }
                    }
                }
                for (int i = 0; i < columnFields.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                wb.write(outputStream);
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getStringValueFromObj(Field field, Object instance) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        String objStrVal = "";
        PropertyDescriptor pd = new PropertyDescriptor(field.getName(), instance.getClass());
        Method getter = pd.getReadMethod();
        Object valObj = getter.invoke(instance);
        if (valObj != null) {
            String dataFormate = field.getAnnotation(ExcelExport.class).dataFormate();
            if (field.getType().getTypeName().equals(Date.class.getTypeName())) {
                if (!StringUtils.isEmpty(dataFormate)) {
                    objStrVal = DateFormatUtil.format((Date) valObj, dataFormate);
                } else {
                    objStrVal = DateFormatUtil.format((Date) valObj, "yyyy/MM/dd");
                }
            } else {
                objStrVal = valObj.toString();
            }
        }
        return objStrVal;
    }

    /**
     * 描述：是否是2003的excel，返回true是2003
     *
     * @param filePath
     * @return
     */
    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * excel版本是2007以上
     *
     * @param filePath
     * @return
     */
    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * <p>说明：将json字符串处理为Map对象</p>
     * <p>时间：2016-3-10 下午4:14:02</p>
     *
     * @param params
     * @return
     */
    protected static Map<String, Object> dealParamsToMap(String params) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isEmpty(params)) {
            return map;
        }
        try {
            map = JSONObject.parseObject(params);
        } catch (Exception e) {
            e.printStackTrace();
            return map;
        }
        return map;
    }
}
