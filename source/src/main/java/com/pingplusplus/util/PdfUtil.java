package com.pingplusplus.util;

import com.bond.allianz.Dao.logs;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PdfUtil {


    /**
     * pdf  第一页 生成图片
     * @param filepath
     * @return
     */

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
        PdfDocument pdf=null;
        try {
            File file = new File(savepath);
            File saveparentpath=file.getParentFile();
            if (!saveparentpath.exists()) saveparentpath.mkdirs();
            //Initialize PDF document
            pdf = new PdfDocument(new PdfReader(modelfile), new PdfWriter(savepath));


            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            Map<String, PdfFormField> fields = form.getFormFields();

            //处理中文问题  UniCNS-UCS2-H  繁体    UniGB-UCS2-H 简体
            String RESOURCESPATH = PdfUtil.class.getResource("/").getPath();
            //PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", false);// PdfEncodings.IDENTITY_H  "UniGB-UCS2-H"  STSongStd-Light
            PdfFont font = PdfFontFactory.createTtcFont(RESOURCESPATH+"static/font/simsun.ttc",1, PdfEncodings.IDENTITY_H, false,true);


//            if(map.containsKey("lan")){
//                if("zh-tw".equals(map.get("lan").toString())){ //繁体用繁体字体
//                    font = PdfFontFactory.createFont("MSung-Light", "UniCNS-UCS2-H", false);
//                }
//            }
            java.util.Iterator<String> it = fields.keySet().iterator();
            List<String> deletearr=new ArrayList<>();
            while (it.hasNext()) {
                //获取文本域名称
                String name = it.next();
                if (map.containsKey(name)&&map.get(name)!=null&&!"".equals(map.get(name).toString())) {
                    PdfFormField field=fields.get(name);
                    switch (field.getClass().getSimpleName()){
                        case "PdfTextFormField"://填充文本域
                            field.setFont(font).setValue(map.get(name).toString());//.setFont(font).setFontSize(12);
                            break;
                        case "PdfButtonFormField"://checkbox radio
                            ////1勾选，2圆圈，3叉叉，4菱形，5方块，6星星
                            if(name.startsWith("qm_"))
                            {
                                ((PdfButtonFormField)field).setImage(map.get(name).toString());
                            }
                            else {
                                ((PdfButtonFormField) field).setCheckType(1).setValue("On");
                            }
                            break;
                    }
                }else{
                    //if(name.startsWith("qm_")){ //签名图片为空的时候必须删除field
                    deletearr.add(name);
                    //}
                }
            }
            deletearr.forEach(arr->{
                form.removeField(arr);
                //fields.remove(arr);
            });
            form.flattenFields();//设置表单域不可编辑
            //添加附件图片
            int picpage=Integer.parseInt(map.get("picpage").toString());
            for(int i=0;i<picpage;i++) {
                PdfPage page=pdf.addNewPage();

                Image img=new Image(ImageDataFactory.create(map.get("qm_pic_"+i).toString()));
                ImageData imgdate=ImageDataFactory.create(map.get("qm_pic_"+i).toString());
                PdfCanvas canvas = new PdfCanvas(page);
                float width=0;//画图高宽
                float height=0;
                float scalx=img.getImageWidth()/(page.getPageSize().getWidth()-100);
                float scaly=img.getImageHeight()/(page.getPageSize().getHeight()-100);
                if(scalx>scaly) {//宽多
                    if ((page.getPageSize().getWidth() - 100) > img.getImageWidth()) {
                        width = img.getImageWidth();
                        height=width*img.getImageHeight()/img.getImageWidth();
                    } else {
                        width = page.getPageSize().getWidth() - 100;
                        height=width*img.getImageHeight()/img.getImageWidth();
                    }
                }else{
                    if((page.getPageSize().getHeight()-100)>img.getImageHeight()){
                        height=img.getImageHeight();
                        width=height*img.getImageWidth()/img.getImageHeight();
                    }else{
                        height=page.getPageSize().getHeight()-100;
                        width=height*img.getImageWidth()/img.getImageHeight();
                    }
                }
                canvas.addImage(imgdate,(page.getPageSize().getWidth()-width)/2,page.getPageSize().getHeight()-height-40,width,true);

                Text title =  new Text(map.get("picname_"+i)==null?" ":map.get("picname_"+i).toString()).setFont(font);
                Paragraph p = new Paragraph().add(title);
                Rectangle rectangle2 = new Rectangle(page.getPageSize().getWidth()/2-120, page.getPageSize().getHeight()-height-120, 250, 60);
                Canvas canvas2 =null;
                try {
                    canvas2=new Canvas(canvas, pdf, rectangle2);
                    canvas2.add(p);
                }
                catch (Exception exxx){}
                finally {
                    canvas2.close();
                }
            }
//            //删除多余的空白页
//            int picpage=Integer.parseInt(map.get("picpage").toString());
//            for(int i=2+15;i>2;i--){ //一共17页
//                if(i>(2+picpage)){
//                    pdf.removePage(i);
//                }
//            }

            pdf.close();
            result = true;

        } catch (Exception e) {
            logs.error(e.toString(),"pdf");
            try {
                pdf.close();
            }
            catch (Exception exxx){}

            e.printStackTrace();
        }
        return result;
    }
}

