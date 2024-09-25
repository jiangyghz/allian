package com.bond.allianz.utils;

import com.sun.media.sound.InvalidFormatException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelUtil {


    /**
     * 根据文件路径获取Workbook对象
     * @param filepath 文件全路径
     * @return
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static Workbook getWorkbook(String filepath)
            throws EncryptedDocumentException, InvalidFormatException, IOException {
        InputStream is = null;
        Workbook wb = null;
        if (StringUtils.isBlank(filepath)) {
            throw new IllegalArgumentException("文件路径不能为空");
        } else {
            String suffiex = getSuffiex(filepath);
            if (StringUtils.isBlank(suffiex)) {
                throw new IllegalArgumentException("文件后缀不能为空");
            }
            if ("xls".equals(suffiex) || "xlsx".equals(suffiex)) {
                try {
                    is = new FileInputStream(filepath);
                    wb = WorkbookFactory.create(is);
                }
                catch (Exception  ex){

                }
                finally {
                    if (is != null) {
                        is.close();
                    }
                    if (wb != null) {
                        wb.close();
                    }
                }
            } else {
                throw new IllegalArgumentException("该文件非Excel文件");
            }
        }
        return wb;
    }
    /**
     * 获取后缀
     * @param filepath filepath 文件全路径
     */
    private static String getSuffiex(String filepath) {
        if (StringUtils.isBlank(filepath)) {
            return "";
        }
        int index = filepath.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return filepath.substring(index + 1, filepath.length());
    }


    /**
     * 经销商导出
     * @param map
     * @param model
     * @return
     */
    public   static HSSFWorkbook ExportAgency(List<Map<String,Object>> map, String model) {
        Workbook workbook=null;
        try {
            FileInputStream fmodel = new FileInputStream(model);
             workbook = new HSSFWorkbook(fmodel);
            //workbook = getWorkbook(model);
            int sheetindex = 0;
            Sheet sheet = workbook.getSheetAt(sheetindex);
            short height = sheet.getRow(1).getHeight();
            List<String> cols = new ArrayList<>();
            List<CellStyle> cellstyle=new ArrayList<>();

            //由于getRawUnicodeString 是私有，只能通过getsst 方法获取拼音
//            for(int i=0;i<100;i++){
//                try {
//                    UnicodeString unicode = ((HSSFWorkbook) workbook).getInternalWorkbook().getSSTString(i);
//                    cols.add(unicode.getExtendedRst().getPhoneticText());
//                }
//                catch (Exception exxx){
//                    break;
//                }
//            }
            sheet.getRow(0).forEach(it -> {
                cols.add(((HSSFRichTextString)it.getRichStringCellValue()).getRawUnicodeString().getExtendedRst().getPhoneticText());
            });
            sheet.getRow(1).forEach(it -> {
                cellstyle.add(it.getCellStyle());
                //HSSFRichTextString rtr =(HSSFRichTextString)it.getRichStringCellValue();
                // cols.add(((HSSFRichTextString) it.getRichStringCellValue()).getRawUnicodeString().getExtendedRst().getPhoneticText());
            });

            int tempIndex = 0;
            for(int index=0;index<map.size();index++){ //数据循环
                if (tempIndex == 65535)
                {
                    sheetindex++;
                    sheet = workbook.getSheetAt(sheetindex);//
                    tempIndex = 0;
                }
                tempIndex++;
                Row r = sheet.getRow(index + 1);
                if (r == null)
                {
                    r = sheet.createRow(index + 1);
                }
                if (r.getFirstCellNum()== -1)//填充cell
                {
                    for (int i = 0; i < cols.size(); i++)
                    {
                        Cell cell = r.createCell(i);
                        cell.setCellStyle(cellstyle.get(i));
                    }
                }
                r.setHeight(height);
                for (int i = 0; i < cols.size(); i++){ //列循环
                    Cell cell=r.getCell(i);
                    if (cols.get(i).equals("index")) //序号
                    {
                        cell.setCellValue(index + 1);
                    }else{
                        if (cols.get(i).indexOf("$") == -1)
                        {
                            Object v=map.get(index).get(cols.get(i));
                            if(v==null){
                                cell.setCellValue("");
                            }else {
                                cell.setCellValue(v.toString());
                            }
                        }else{
                            String name = cols.get(i).split("\\$")[0];
                            String type = cols.get(i).split("\\$")[1];
                            Object v2=map.get(index).get(name);

                            String v = "";
                            if(v2!=null){
                                v=v2.toString();
                            }
                            String vv = "";
                            switch (type){
                                case "enum":
                                    if("valid".equals(name)){
                                        vv = ("1".equals(v)? "是" : "否");
                                    }
                                    if("isactived".equals(name)){
                                        vv = ("1".equals(v)? "是" : "否");
                                    }
                                    if("is_elec_invoice".equals(name)){
                                        vv = ("1".equals(v)? "是" : "否");
                                    }
                                    break;
                            }
                            cell.setCellValue(vv);
                        }//end $
                    }
                }//end cols

            }//end map

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (HSSFWorkbook) workbook;
    }

}
