package com.bond.allianz.controller;

import com.bond.allianz.Dao.logs;
import com.bond.allianz.Dao.setting;
import com.bond.allianz.service.UserLogService;
import com.bond.allianz.utils.QRCodeGenerator;
import com.google.zxing.WriterException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping("/import")
public class ImportController extends BaseController {

    @Value("${upload.wximage}")
    private String wximage;

    @Autowired
    private UserLogService userLogService;

    @ResponseBody
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String upload(@RequestParam(value="file") MultipartFile file) {
        String result = "";
        //List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("files");
        String folder = Params("folder");
        if (folder.isEmpty()) {
            folder = "/temp/";
        }
        String savePath="";
        String filePath = folder;
        if(filePath.indexOf("wximage")>-1&&!"".equals(wximage)){
            if(filePath.indexOf("/wximage/")>-1){ //兼容子文件夹
                savePath = wximage+filePath.replace("/wximage/","").replace("/","\\");
            }else {
                savePath = wximage;
            }
        }else{
            savePath = request.getServletContext().getRealPath(filePath);
        }
        try {
            File f = new File(savePath);
            //如果目录不存在
            if (!f.exists()) {
                //创建目录
                f.mkdirs();
            }
            if (!file.isEmpty()) {
                String ext=FilenameUtils.getExtension(file.getOriginalFilename());
                if(Arrays.asList("asp","aspx","jsp","php","exe","jspx","cgi").contains(ext.toLowerCase())||ext.toLowerCase().indexOf("jsp")>-1){
                    result = JsonSerializer(new Object[]{false, "文件类型错误"});
                }else {
                    String fileName = file.getOriginalFilename();
                    String newName = setting.GetCurrenntTime() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
                    File dest = new File(savePath + newName);
                    try {
                        file.transferTo(dest);
                    } catch (IOException e) {
                        logs.error("文件保存错误:", e);
                    }
                    result = JsonSerializer(new Object[]{true, newName});
                }
            }
        } catch (Exception e) {
            logs.error(e.toString(), "文件保存错误");
        }
        return result;
    }

    /**
     * 菜单点击
     * @param agencyid
     * @param uid
     * @param name
     * @param url
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/clickmenu",method =RequestMethod.POST)
    public String clickmenu(String agencyid,String uid,String name,String url){
        String result = "";
        try{
            userLogService.insertClick(agencyid,uid,name,url);
        }
        catch (Exception ex){
            logs.error("菜单点击记录错误",ex);
        }
        return  result;
    }

    /**
     * 二维码输出
     * @param w
     * @return
     */
    @GetMapping(value="/qrimage")
    public  ResponseEntity<byte[]> getQRImage(String w) {
        //二维码内的信息
        int width=ParamsInt("width",300);//宽
        int height=ParamsInt("height",300);//高
        byte[] qrcode = null;
        try {
            qrcode = QRCodeGenerator.getQRCodeImage(w, width, height);
        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }

        // Set headers
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<byte[]> (qrcode, headers, HttpStatus.CREATED);
    }
}
