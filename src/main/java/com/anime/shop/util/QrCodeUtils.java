package com.anime.shop.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * 核销二维码生成工具（生成微信可扫码的Base64格式）
 */
public class QrCodeUtils {

    /**
     * 生成核销二维码
     * @param verifyCode 核销码（二维码内容）
     * @return Base64编码的二维码图片（前端可直接<img src="xxx" />显示）
     */
    public static String generateVerifyQrCode(String verifyCode) {
        if (verifyCode == null || verifyCode.isEmpty()) {
            return "https://picsum.photos/100/100"; // 空核销码返回默认图
        }
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // 2. 生成300x300的二维码矩阵（黑色码点，白色背景）
            MatrixToImageConfig config = new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF);
            var bitMatrix = qrCodeWriter.encode(verifyCode, BarcodeFormat.QR_CODE, 300, 300);

            // 3. 修复：使用writeToStream而非writeToPath，解决参数不匹配问题
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream); // 原错误行修复
            // 正确写法：MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream, config);

            // 4. 修复：使用JDK原生Base64替代Spring废弃的Base64Utils
            byte[] qrCodeBytes = outputStream.toByteArray();
            String base64Code = Base64.getEncoder().encodeToString(qrCodeBytes);

            // 5. 返回前端可直接显示的Base64 URL
            return "data:image/png;base64," + base64Code;
        } catch (Exception e) {
            e.printStackTrace();
            // 生成失败时返回默认二维码占位图
            return "https://picsum.photos/100/100";
        }
    }
}