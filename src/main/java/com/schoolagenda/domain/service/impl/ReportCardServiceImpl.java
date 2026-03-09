package com.schoolagenda.domain.service.impl;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.schoolagenda.application.web.dto.ReportCardGradeDTO;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.service.ReportCardService;
import com.schoolagenda.domain.service.TeacherClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportCardServiceImpl implements ReportCardService {

    private final TemplateEngine templateEngine;
    private final StudentRepository studentRepository;
    private final TeacherClassService teacherClassService;

//    @Override
//    public byte[] generateReportCard(Long studentId) {
//        Student student = studentRepository.findById(studentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));
//
//        // 1. Prepara o contexto de dados para o Thymeleaf
//        Context context = new Context();
//        context.setVariable("studentName", student.getFullName());
//        context.setVariable("className", student.getSchoolClass().getName());
//        context.setVariable("globalAverage", student.getGlobalAverage());
//
//        // Aqui você buscaria a lista consolidada de notas por disciplina
//        // context.setVariable("grades", listGradesByStudent(studentId));
//
//        // 2. Renderiza o template HTML para uma String
//        String htmlContent = templateEngine.process("reports/report-card", context);
//
//        // 3. Converte HTML para PDF (byte array)
//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            ITextRenderer renderer = new ITextRenderer();
//            renderer.setDocumentFromString(htmlContent);
//            renderer.layout();
//            renderer.createPDF(outputStream);
//            return outputStream.toByteArray();
//        } catch (Exception e) {
//            throw new RuntimeException("Falha ao gerar o PDF do boletim", e);
//        }
//    }

    @Override
    public byte[] generateReportCard(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        // 1. Busca os dados reais de todas as disciplinas
        List<ReportCardGradeDTO> grades = teacherClassService.listGradesByStudent(studentId);

        // 2. Prepara o contexto do Thymeleaf
        Context context = new Context();
        context.setVariable("studentName", student.getFullName());
        context.setVariable("className", student.getSchoolClass().getName());
        context.setVariable("grades", grades);
        context.setVariable("globalAverage", student.getGlobalAverage());

        // 3. Renderiza e Converte
        String htmlContent = templateEngine.process("reports/report-card", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
}
