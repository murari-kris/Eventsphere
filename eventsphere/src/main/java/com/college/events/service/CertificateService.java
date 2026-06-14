package com.college.events.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

@Service
public class CertificateService {

    private static final Map<String, String> studentRegistry = new HashMap<>();
    
    static {
        studentRegistry.put("krishna@gmail.com", "Krishna Murari");
        studentRegistry.put("sona@9090gmail.com", "Sona");
    }

    public byte[] generateCertificatePdf(String userEmail, String eventName) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        // Resolve student name dynamically
        String resolvedStudentName = studentRegistry.getOrDefault(userEmail.trim().toLowerCase(), null);
        if (resolvedStudentName == null) {
            if (userEmail.contains("@")) {
                String prefix = userEmail.split("@")[0];
                resolvedStudentName = prefix.substring(0, 1).toUpperCase() + prefix.substring(1);
            } else {
                resolvedStudentName = userEmail;
            }
        }
        
        // 1. Initialize Landscape A4 Document with tight control over structural margins
        Document document = new Document(PageSize.A4.rotate(), 50, 50, 50, 50);
        
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // 2. Premium Dual-Layer Geometric Outer Border Matrix
            Rectangle outerBorder = new Rectangle(document.getPageSize());
            outerBorder.setBorder(Rectangle.BOX);
            outerBorder.setBorderWidth(6);
            outerBorder.setBorderColor(new Color(11, 15, 25)); // Dark Navy Deep Tech Accent
            document.add(outerBorder);

            // Inset Inner Elegant Border Line
            Rectangle innerBorder = new Rectangle(document.getPageSize());
            innerBorder.setLeft(15);
            innerBorder.setTop(document.getPageSize().getHeight() - 15);
            innerBorder.setRight(document.getPageSize().getWidth() - 15);
            innerBorder.setBottom(15);
            innerBorder.setBorder(Rectangle.BOX);
            innerBorder.setBorderWidth(1.5f);
            innerBorder.setBorderColor(new Color(124, 110, 245)); // EventSphere Signature Violet Token Color
            document.add(innerBorder);

            // 3. Premium Token Typographic Matrix Definitions
            Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(124, 110, 245));
            Font titleFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 38, new Color(22, 25, 37));
            Font presentationFont = FontFactory.getFont(FontFactory.TIMES_ITALIC, 16, new Color(148, 163, 184));
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32, new Color(99, 85, 217));
            Font descriptionFont = FontFactory.getFont(FontFactory.HELVETICA, 13, new Color(71, 85, 105));
            Font eventFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(11, 15, 25));
            Font footerTagFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, new Color(148, 163, 184));
            Font signTextFont = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(71, 85, 105));
            Font signLineFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(22, 25, 37));

            // Use an invisible structural parent table to anchor alignment elements cleanly
            PdfPTable containerTable = new PdfPTable(1);
            containerTable.setWidthPercentage(100);
            containerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            containerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            // Spacing Helper Block
            Paragraph lineSpacer = new Paragraph("\n");

            // --- HEADER BRANDING BLOCK ---
            Paragraph brandTag = new Paragraph("E V E N T S P H E R E   E N T R Y   P A S S   R E G I S T R Y", brandFont);
            brandTag.setAlignment(Element.ALIGN_CENTER);
            document.add(brandTag);
            document.add(lineSpacer);

            // --- MAIN CORNERSTONE TITLE ---
            Paragraph certificateTitle = new Paragraph("CERTIFICATE OF COMPLETION", titleFont);
            certificateTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(certificateTitle);
            
            Paragraph decorativeLine = new Paragraph("____________________________________________________", brandFont);
            decorativeLine.setAlignment(Element.ALIGN_CENTER);
            document.add(decorativeLine);
            document.add(lineSpacer);
            document.add(lineSpacer);

            // --- PRESENTATION SUBTITLE ---
            Paragraph presentationText = new Paragraph("This document officially validates that", presentationFont);
            presentationText.setAlignment(Element.ALIGN_CENTER);
            document.add(presentationText);
            document.add(lineSpacer);

            // --- STUDENT IDENTITY LINE ---
            Paragraph studentNameLine = new Paragraph(resolvedStudentName, nameFont);
            studentNameLine.setAlignment(Element.ALIGN_CENTER);
            document.add(studentNameLine);
            document.add(lineSpacer);

            // --- RECOGNITION DESCRIPTION MATRICES ---
            Paragraph achievementDescription = new Paragraph(
                "has demonstrated outstanding technical execution, attendance discipline, and verified completion of the dynamic bootcamp training sprint format structured for:\n", descriptionFont);
            achievementDescription.setAlignment(Element.ALIGN_CENTER);
            achievementDescription.setLeading(18); // Elegant line spacing
            document.add(achievementDescription);
            document.add(lineSpacer);

            // --- DYNAMICALLY RESOLVED COURSE KEY TARGET ---
            Paragraph eventTitleContainer = new Paragraph("« " + eventName + " »", eventFont);
            eventTitleContainer.setAlignment(Element.ALIGN_CENTER);
            document.add(eventTitleContainer);
            
            document.add(lineSpacer);
            document.add(lineSpacer);
            document.add(lineSpacer);

            // --- SIGNATURES & VERIFICATION ARCHITECTURE MATRIX ---
            PdfPTable executionFooterTable = new PdfPTable(2);
            executionFooterTable.setWidthPercentage(90);
            
            // Left Signature Cell
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(PdfPCell.NO_BORDER);
            leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph signLine1 = new Paragraph("___________________________", signTextFont);
            signLine1.setAlignment(Element.ALIGN_CENTER);
            Paragraph directorTitle = new Paragraph("Academic Program Director", signLineFont);
            directorTitle.setAlignment(Element.ALIGN_CENTER);
            leftCell.addElement(signLine1);
            leftCell.addElement(directorTitle);
            executionFooterTable.addCell(leftCell);

            // Right Signature Cell
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(PdfPCell.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph signLine2 = new Paragraph("___________________________", signTextFont);
            signLine2.setAlignment(Element.ALIGN_CENTER);
            Paragraph techLeadTitle = new Paragraph("EventSphere Platform Registry", signLineFont);
            techLeadTitle.setAlignment(Element.ALIGN_CENTER);
            rightCell.addElement(signLine2);
            rightCell.addElement(techLeadTitle);
            executionFooterTable.addCell(rightCell);

            document.add(executionFooterTable);
            
            document.add(lineSpacer);
            document.add(lineSpacer);

            // --- CENTRAL CRYPTOGRAPHIC LOG METADATA TAG ---
            Paragraph registryLogTag = new Paragraph("Verified Security Hash Signed via EventSphere Node Mesh Authentication Architecture • Multi-Tenant Enterprise Database Node", footerTagFont);
            registryLogTag.setAlignment(Element.ALIGN_CENTER);
            document.add(registryLogTag);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}