package com.example.finance_control.service;

import com.example.finance_control.domain.activity.Activity;
import com.example.finance_control.repository.activity.ActivityRepository;
import com.example.finance_control.utils.FormatDateTime;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ExportService {

    @Autowired
    private ActivityRepository activityRepository;

    public void writeActivitiesToCsv(String userId, HttpServletResponse response) throws IOException {
        List<Activity> activities = activityRepository.findByUserId(userId);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=activities.csv");

        PrintWriter writer = response.getWriter();
        writer.println("Descrição;Tipo;Quantia;Data e Hora");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withLocale(Locale.forLanguageTag("pt-BR"));

        for (Activity activity : activities) {
            writer.println(String.format("%s;%s;%.2f;%s",
                    activity.getDescription(),
                    activity.getType(),
                    activity.getValue(),
                    FormatDateTime.formatDate(activity.getDate())
            ));
    }
}

    public void writeActivitiesToPdf(String userId, HttpServletResponse response) throws IOException, DocumentException {
        List<Activity> activities = activityRepository.findByUserId(userId);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=activities.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        document.add(new Paragraph("Reporte de Atividades"));
        document.add(new Paragraph(" "));

        for (Activity activity : activities) {
            document.add(new Paragraph(
                    "Descrição: " + activity.getDescription() + "\n" +
                    "Tipo: " + activity.getType() + "\n" +
                    "Valor: R$ " + String.format("%.2f",activity.getValue()) + "\n" +
                    "Data: " + FormatDateTime.formatDate(activity.getDate()) + "\n"
                    ));
            document.add(new Paragraph(" "));
        }

        document.close();
    }
}
