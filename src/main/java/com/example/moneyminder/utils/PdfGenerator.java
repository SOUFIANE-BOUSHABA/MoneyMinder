package com.example.moneyminder.utils;

import com.example.moneyminder.entity.Invoice;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

public class PdfGenerator {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");



    public static byte[] generateInvoicePdf(Invoice invoice) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A6);
            Document document = new Document(pdfDoc);


            document.add(new Paragraph("MoneyMinder Invoice")
                    .setFontSize(18)
                    .setBold());


            Table table = new Table(2);
            table.addCell(new Cell().add(new Paragraph("Invoice Number:")));
            table.addCell(new Cell().add(new Paragraph(invoice.getInvoiceNumber())));
            table.addCell(new Cell().add(new Paragraph("Issue Date:")));
            table.addCell(new Cell().add(new Paragraph(dateFormat.format(invoice.getIssueDate()))));
            table.addCell(new Cell().add(new Paragraph("Total Amount:")));
            table.addCell(new Cell().add(new Paragraph("$" + invoice.getTotalAmount())));
            table.addCell(new Cell().add(new Paragraph("Status:")));
            table.addCell(new Cell().add(new Paragraph(invoice.getStatus().toString())));
            table.addCell(new Cell().add(new Paragraph("User:")));
            table.addCell(new Cell().add(new Paragraph(invoice.getUser().getFirstName() + " " + invoice.getUser().getLastName())));

            document.add(table);

            document.add(new Paragraph("\nThank you for using MoneyMinder."));

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
