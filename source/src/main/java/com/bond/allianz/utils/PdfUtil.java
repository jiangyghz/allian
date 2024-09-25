package com.bond.allianz.utils;

import com.bond.allianz.Dao.logs;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.File;
import java.util.Map;

/**
 * pdf工具
 */
public class PdfUtil {


    /**
     * 根据pdf模板生成pdf
     *
     * @param map 数据对象 key必须和模板中的表单名称一致
     * @param modelfile 模板路径
     * @param savepath
     */
    public static boolean createdPdfByModel(Map<String, Object> map, String modelfile, String savepath) {
        // 利用模板生成pdf
        // 生成的新文件路径
        boolean result = false;
        try {
            File file = new File(savepath);
            File saveparentpath=file.getParentFile();
            if (!saveparentpath.exists()) saveparentpath.mkdirs();
            //Initialize PDF document
            PdfDocument pdf = new PdfDocument(new PdfReader(modelfile), new PdfWriter(savepath));
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            Map<String, PdfFormField> fields = form.getFormFields();

            //处理中文问题
            //PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
            //处理中文问题  UniCNS-UCS2-H  繁体    UniGB-UCS2-H 简体
            String RESOURCESPATH = PdfUtil.class.getResource("/").getPath();
            //PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", false);// PdfEncodings.IDENTITY_H  "UniGB-UCS2-H"  STSongStd-Light
            PdfFont font = PdfFontFactory.createTtcFont(RESOURCESPATH+"static/font/simsun.ttc",1, PdfEncodings.IDENTITY_H, false,true);

            java.util.Iterator<String> it = fields.keySet().iterator();
            while (it.hasNext()) {
                //获取文本域名称
                String name = it.next();
                if (map.containsKey(name)) {
                    PdfFormField field=fields.get(name);
                    switch (field.getClass().getSimpleName()){
                        case "PdfTextFormField"://填充文本域
                            field.setFont(font).setValue(map.get(name).toString());
                            //field.setValue(map.get(name).toString()).setFont(font).setFontSize(12);
                            break;
                        case "PdfButtonFormField"://checkbox radio
                            ////1勾选，2圆圈，3叉叉，4菱形，5方块，6星星
                            field.setCheckType(1).setValue("On");
                            break;
                    }
                }
            }
            form.flattenFields();//设置表单域不可编辑
            pdf.close();
            result = true;
        } catch (Exception e) {
            logs.error("生成pdf错误:"+e.toString(), "pdf");
            e.printStackTrace();
        }
        return result;
    }
}
