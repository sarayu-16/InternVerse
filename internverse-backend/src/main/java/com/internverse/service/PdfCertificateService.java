package com.internverse.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Builds simple PDF certificates using OpenPDF (LGPL).
 */
@Service
public class PdfCertificateService {

    public byte[] buildCertificatePdf(String studentName, String programTitle, String verificationCode,
                                      LocalDate issueDate) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.DARK_GRAY);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.BLACK);
            document.add(new Paragraph("Certificate of Completion", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("This certifies that", bodyFont));
            document.add(new Paragraph(" "));
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(0, 90, 140));
            document.add(new Paragraph(studentName, nameFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(
                    "has successfully completed the internship program: " + programTitle, bodyFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(
                    "Issued on: " + issueDate.format(DateTimeFormatter.ISO_DATE), bodyFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Verification code: " + verificationCode, bodyFont));
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate PDF", e);
        }
    }
}
