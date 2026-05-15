package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

public class TranscriptExporter {

    /**
     * Convenient method to export both TXT and PDF formats
     */
    public static String exportTranscript(String username) {
        String txtPath = "transcript_" + username + ".txt";
        String pdfPath = "transcript_" + username + ".pdf";
        
        String res1 = exportToText(username, txtPath);
        String res2 = exportToPdf(username, pdfPath);
        
        if (res1.contains("successfully") && res2.contains("successfully")) {
            return txtPath + " and " + pdfPath;
        } else if (res1.contains("successfully")) {
            return txtPath;
        } else {
            return "Export failed";
        }
    }

    public static String export(String username, String path) {
        if (path.toLowerCase().endsWith(".pdf")) {
            return exportToPdf(username, path);
        } else {
            return exportToText(username, path);
        }
    }

    private static String exportToText(String username, String path) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            writer.println("UNIVERSITY AUTOMATION SYSTEM - TRANSCRIPT");
            writer.println("==========================================");
            writer.println("Student Username: " + username);
            writer.println("Export Date: " + new java.util.Date());
            writer.println();
            
            data.DataStore ds = data.DataStore.getInstance();
            model.User studentUser = ds.findUser(username);
            model.StudentProfile profile = ds.findStudentProfileByUsername(username);
            
            if (studentUser != null && profile != null) {
                writer.println("Student Name: " + studentUser.getFullName());
                writer.println("Student ID:   " + profile.getStudentId());
                writer.println("Department:   " + profile.getDepartment());
                if (profile.getSecondDepartment() != null && !profile.getSecondDepartment().isEmpty()) {
                    writer.println("2nd Major:    " + profile.getSecondDepartment());
                }
                writer.println("Year:         " + profile.getYear());
            }
            writer.println("----------------------------------------------------------------------------------");
            writer.println(String.format("%-12s %-30s %-8s %-8s %-8s %-8s %-12s", 
                "Course", "Title", "Credit", "Mid", "Fin", "Avg", "Grade"));
            writer.println("----------------------------------------------------------------------------------");

            java.util.List<model.GradeRecord> grades = ds.getGradesByStudent(username);
            for (model.GradeRecord grade : grades) {
                model.Course course = ds.findCourse(grade.getCourseCode());
                if (course != null) {
                    writer.println(String.format(java.util.Locale.US, "%-12s %-30s %-8d %-8.2f %-8.2f %-8.2f %-12s",
                            course.getCourseCode(),
                            course.getCourseName().length() > 28 ? course.getCourseName().substring(0, 25) + "..." : course.getCourseName(),
                            course.getCredit(),
                            grade.getMidterm(),
                            grade.getFinalExam(),
                            grade.calculateAverage(),
                            grade.getLetterGrade()
                    ));
                }
            }
            writer.println("----------------------------------------------------------------------------------");
            writer.println(String.format(java.util.Locale.US, "Cumulative GPA: %.2f", ds.calculateGPA(username)));
            writer.println("==========================================");
            
            return "Transcript exported successfully to TXT!";
        } catch (IOException e) {
            return "Error exporting text transcript: " + e.getMessage();
        }
    }

    private static String exportToPdf(String username, String path) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            // Font styles
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

            // Title
            Paragraph title = new Paragraph("UNIVERSITY AUTOMATION SYSTEM\nOFFICIAL TRANSCRIPT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            data.DataStore ds = data.DataStore.getInstance();
            model.User studentUser = ds.findUser(username);
            model.StudentProfile profile = ds.findStudentProfileByUsername(username);

            // Student Info
            if (studentUser != null && profile != null) {
                PdfPTable infoTable = new PdfPTable(2);
                infoTable.setWidthPercentage(100);
                infoTable.setSpacingAfter(20);
                
                infoTable.addCell(new Phrase("Full Name: " + studentUser.getFullName(), normalFont));
                infoTable.addCell(new Phrase("Student ID: " + profile.getStudentId(), normalFont));
                infoTable.addCell(new Phrase("Primary Dept: " + profile.getDepartment(), normalFont));
                infoTable.addCell(new Phrase("Current Year: " + profile.getYear(), normalFont));
                
                if (profile.getSecondDepartment() != null && !profile.getSecondDepartment().isEmpty()) {
                    infoTable.addCell(new Phrase("Double Major: " + profile.getSecondDepartment(), normalFont));
                    infoTable.addCell(new Phrase("2nd Major Year: " + profile.getSecondYear(), normalFont));
                } else {
                    PdfPCell empty = new PdfPCell(new Phrase(""));
                    empty.setBorder(Rectangle.NO_BORDER);
                    infoTable.addCell(empty);
                    infoTable.addCell(empty);
                }
                
                document.add(infoTable);
            }

            // Grades Table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            try {
                table.setWidths(new float[]{1.5f, 3.5f, 1f, 1f, 1f, 1f, 1f});
            } catch (Exception e) {}

            String[] headers = {"Code", "Course Name", "CR", "Mid", "Fin", "Avg", "Grd"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            java.util.List<model.GradeRecord> grades = ds.getGradesByStudent(username);
            for (model.GradeRecord grade : grades) {
                model.Course course = ds.findCourse(grade.getCourseCode());
                if (course != null) {
                    table.addCell(new Phrase(course.getCourseCode(), normalFont));
                    table.addCell(new Phrase(course.getCourseName(), normalFont));
                    table.addCell(new Phrase(String.valueOf(course.getCredit()), normalFont));
                    table.addCell(new Phrase(String.format("%.1f", grade.getMidterm()), normalFont));
                    table.addCell(new Phrase(String.format("%.1f", grade.getFinalExam()), normalFont));
                    table.addCell(new Phrase(String.format("%.1f", grade.calculateAverage()), normalFont));
                    table.addCell(new Phrase(grade.getLetterGrade(), headerFont));
                }
            }
            document.add(table);

            // GPA
            Paragraph gpaPar = new Paragraph("\nCumulative GPA: " + String.format("%.2f", ds.calculateGPA(username)), headerFont);
            gpaPar.setAlignment(Element.ALIGN_RIGHT);
            document.add(gpaPar);

            document.close();
            return "Transcript exported successfully as PDF!";
        } catch (Exception e) {
            return "Error exporting PDF: " + e.getMessage();
        }
    }
}
