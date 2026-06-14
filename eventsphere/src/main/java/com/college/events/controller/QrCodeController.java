package com.college.events.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/qr")
@CrossOrigin(origins = "*") // Allows your HTML frontend to talk to this endpoint freely
public class QrCodeController {

    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQrCode(@RequestParam("token") String token) {
        try {
            // 1. Initialize the ZXing QR writer engine
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            
            // 2. Convert your token text into a 250x250 bit pixel matrix map
            BitMatrix bitMatrix = qrCodeWriter.encode(token, BarcodeFormat.QR_CODE, 250, 250);
            
            // 3. Channel the grid pixels into a raw PNG byte stream
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] imageBytes = pngOutputStream.toByteArray();
            
            // 4. Ship the picture directly back to the browser
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
            
        } catch (Exception e) {
            // If anything breaks, return an empty server error body
            return ResponseEntity.internalServerError().body(null);
        }
    }
}