package io.github.purecaptcha;

import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import java.io.FileOutputStream;

public class TestGifMain {
    public static void main(String[] args) throws Exception {
        System.out.println("Testing GIF captcha...");
        
        Captcha captcha = CaptchaFactory.create(CaptchaType.ANIMATED_GIF);
        
        System.out.println("Answer: " + captcha.getAnswer());
        
        byte[] data = captcha.getImageData();
        if (data != null && data.length > 0) {
            System.out.println("Data length: " + data.length + " bytes");
            
            // 检查 GIF 文件头
            if (data.length >= 6) {
                String header = new String(data, 0, 6);
                System.out.println("Header: " + header);
            }
            
            FileOutputStream fos = new FileOutputStream("test-gif.gif");
            fos.write(data);
            fos.close();
            System.out.println("GIF saved to: test-gif.gif");
        } else {
            System.out.println("ERROR: GIF data is empty!");
        }
    }
}
