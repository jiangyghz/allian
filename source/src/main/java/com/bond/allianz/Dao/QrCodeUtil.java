package com.bond.allianz.Dao;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.google.zxing.client.j2se.MatrixToImageWriter.writeToFile;
@Component
public class QrCodeUtil {
    private static String wximage;
    @Value("${upload.wximage}")
    public  void setWximage(String wximage){
        this.wximage=wximage;
    }

    public static String createQrCode(String url) {
        //String path = request.getServletContext().getRealPath("/wximage/");
        String fileName = setting.GetCurrenntTime() + ".jpg" ;
        try {


            Map<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 400, 400, hints);
            File file = new File(wximage, fileName);
            if (file.exists() || ((file.getParentFile().exists() || file.getParentFile().mkdirs()) && file.createNewFile())) {
                writeToFile(bitMatrix, "jpg", file);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "wximage/"+fileName;
    }


}
