package com.example.moneyminder.utils;

import com.example.moneyminder.DTOs.FinancialReportRequest;
import com.example.moneyminder.entity.Invoice;
import com.example.moneyminder.entity.Quote;
import com.example.moneyminder.entity.Transaction;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

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





    public static byte[] generateQuotePdf(Quote quote) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Quote Details")
                    .setFontSize(18)
                    .setBold());

            Table table = new Table(2);
            table.addCell(new Cell().add(new Paragraph("Quote Number:")));
            table.addCell(new Cell().add(new Paragraph(quote.getQuoteNumber())));
            table.addCell(new Cell().add(new Paragraph("Issue Date:")));
            table.addCell(new Cell().add(new Paragraph(dateFormat.format(quote.getIssueDate()))));
            table.addCell(new Cell().add(new Paragraph("Total Amount:")));
            table.addCell(new Cell().add(new Paragraph("$" + quote.getTotalAmount())));
            table.addCell(new Cell().add(new Paragraph("Status:")));
            table.addCell(new Cell().add(new Paragraph(quote.getStatus().toString())));

            document.add(table);

            document.add(new Paragraph("\nThank you for using MoneyMinder."));

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }



    public static byte[] generateFinancialReportPdf(List<Transaction> transactions, List<Invoice> invoices, List<Quote> quotes, FinancialReportRequest request) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Financial Report")
                    .setFontSize(18)
                    .setBold());
            document.add(new Paragraph("Report Type: " + request.getReportType()));
            document.add(new Paragraph("Date Range: " + dateFormat.format(request.getStartDate()) + " to " + dateFormat.format(request.getEndDate())));

            // Add Transactions Table
            document.add(new Paragraph("\nTransactions").setBold());
            Table transactionTable = new Table(3);
            transactionTable.addCell("Date");
            transactionTable.addCell("Amount");
            transactionTable.addCell("Type");
            for (Transaction transaction : transactions) {
                transactionTable.addCell(dateFormat.format(transaction.getDate()));
                transactionTable.addCell("$" + transaction.getAmount());
                transactionTable.addCell(transaction.getType().toString());
            }
            document.add(transactionTable);

            // Add Invoices Table
            document.add(new Paragraph("\nInvoices").setBold());
            Table invoiceTable = new Table(4);
            invoiceTable.addCell("Number");
            invoiceTable.addCell("Date");
            invoiceTable.addCell("Amount");
            invoiceTable.addCell("Status");
            for (Invoice invoice : invoices) {
                invoiceTable.addCell(invoice.getInvoiceNumber());
                invoiceTable.addCell(dateFormat.format(invoice.getIssueDate()));
                invoiceTable.addCell("$" + invoice.getTotalAmount());
                invoiceTable.addCell(invoice.getStatus().toString());
            }
            document.add(invoiceTable);

            // Add Quotes Table
            document.add(new Paragraph("\nQuotes").setBold());
            Table quoteTable = new Table(4);
            quoteTable.addCell("Number");
            quoteTable.addCell("Date");
            quoteTable.addCell("Amount");
            quoteTable.addCell("Status");
            for (Quote quote : quotes) {
                quoteTable.addCell(quote.getQuoteNumber());
                quoteTable.addCell(dateFormat.format(quote.getIssueDate()));
                quoteTable.addCell("$" + quote.getTotalAmount());
                quoteTable.addCell(quote.getStatus().toString());
            }
            document.add(quoteTable);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Financial Report PDF", e);
        }
    }


    public static byte[] readFileAsBytes(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
            fis.close();
            return fileBytes;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
    }
}
