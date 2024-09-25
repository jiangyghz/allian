package com.bond.allianz.Dao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Component
public class toexcel {
    private static String wximage;
    @Value("${upload.wximage}")
    public  void setWximage(String wximage){
        this.wximage=wximage;
    }

    public static String createCSVFile(List<Map<String, Object>>list, Map<String, Object> title, String outPutPath,
                                       String fileName) {
        File csvFile = null;
        if (outPutPath.equals(""))
        {
            outPutPath=wximage;
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            outPutPath= request.getSession().getServletContext().getRealPath("excel");

        }
        File path = new File(outPutPath);
        //如果目录不存在
        if(!path.exists()) {
            //创建目录
            path.mkdirs();
        }
        BufferedWriter csvFileOutputStream = null;
        try {
//            File file = new File(outPutPath);
//            if (!file.exists()) {
//                file.mkdir();
//            }
            if (fileName.equals(""))fileName=setting.GetCurrenntTime()+"";
            csvFile = File.createTempFile(fileName, ".csv", new File(outPutPath));
            //System.out.println("csvFile：" + csvFile);
            // UTF-8使正确读取分隔符","  
            //如果生产文件乱码，windows下用gbk，linux用UTF-8
            String encode="utf-8";
            String os = System.getProperty("os.name");
            if(os.toLowerCase().startsWith("win"))
            {
                encode="gbk";
            }
            encode="gbk";
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), encode), 1024);
            int ii=0;
            String[]al=new String[title.size()];
            for (Map.Entry<String, Object> m : title.entrySet())
            {
                al[ii]=formatCsvStirng( m.getValue().toString());

                csvFileOutputStream.write("\t"+formatCsvStirng( m.getValue().toString())+"\t");
                if (ii!=title.size()-1)    csvFileOutputStream.write(",");
                ii++;
            }

            csvFileOutputStream.newLine();
            for (int i=0;i<list.size();i++)
            {
                ii=0;
                Map<String, Object>map=list.get(i);
                for (Map.Entry<String, Object> m : title.entrySet()){
                    String tt=al[ii];
                 //   if (tt.contains("日期")||tt.contains("金额")||tt.contains("数量")||tt.contains("序号")||tt.contains("价")||tt.contains("凭证号")
                    if (tt.contains("交易编号")||tt.contains("身份证号")||tt.contains("手机")||tt.contains("发票号")||tt.contains("保单号")||tt.contains("交易流水号")||tt.contains("商户订单号"))

                            csvFileOutputStream.write("\t"+formatCsvStirng(setting.NUllToSpace(map.get(m.getKey())))+"\t");

                    else
                        csvFileOutputStream.write(formatCsvStirng(setting.NUllToSpace(map.get(m.getKey()))));

                    if (ii!=title.size()-1)    csvFileOutputStream.write(",");
                    ii++;
                }
                csvFileOutputStream.newLine();


            }

            csvFileOutputStream.flush();
        } catch (Exception e) {
            return  e.toString();
        } finally {
            try {
                csvFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileName=csvFile.getName();
        return "wximage/"+fileName;
    }
    public static String formatCsvStirng(String value)
    {
        value = value.replace("\n", "");
        value = value.replace("\r", "");
        value = value.replace("\t", "");
        value = value.replace("\"", "");
        value = value.replace(",", "，");
        value = value.replace("?", "？");
       // value="\t" +value+"\t" ;
        return  value;
    }
}
