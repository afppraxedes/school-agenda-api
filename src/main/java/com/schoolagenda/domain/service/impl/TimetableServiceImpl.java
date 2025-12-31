package com.schoolagenda.domain.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.schoolagenda.application.web.dto.request.TimetableRequest;
import com.schoolagenda.application.web.dto.response.TimetableResponse;
import com.schoolagenda.application.web.mapper.TimetableMapper;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.exception.BusinessResourceException;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.SchoolClass;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.model.TeacherClass;
import com.schoolagenda.domain.model.Timetable;
import com.schoolagenda.domain.repository.SchoolClassRepository;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.repository.TeacherClassRepository;
import com.schoolagenda.domain.repository.TimetableRepository;
import com.schoolagenda.domain.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.*;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimetableServiceImpl implements TimetableService {

    private final TimetableRepository timetableRepository;
    private final TeacherClassRepository teacherClassRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final TimetableMapper timetableMapper;

    @Override
    @Transactional
    public TimetableResponse create(TimetableRequest request) {
        TeacherClass tc = teacherClassRepository.findById(request.teacherClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo Professor/Turma não encontrado"));

        if (timetableRepository.hasTeacherConflict(tc.getTeacher().getId(), request.dayOfWeek(), request.startTime(), request.endTime())) {
            throw new BusinessResourceException("Conflito: Professor já possui aula neste horário.");
        }

        if (timetableRepository.hasClassConflict(tc.getSchoolClass().getId(), request.dayOfWeek(), request.startTime(), request.endTime())) {
            throw new BusinessResourceException("Conflito: Turma já possui aula neste horário.");
        }

//        LocalTime startTime = request.startTime();
//        ZoneId chicagoZone = ZoneId.of("America/Sao_Paulo");
////        LocalTime timeInChicago = LocalTime.now(ZoneId.from(startTime)); // clock = Clock.system(chicagoZone)
//// Or better:
//        ZonedDateTime zdt = ZonedDateTime.now(chicagoZone);
//        startTime = zdt.toLocalTime();
//
////        LocalTime endTime = request.endTime();
//        ZonedDateTime zonedUTC = ZonedDateTime.from(startTime.atOffset((ZoneOffset) ZoneId.of("UTC")));
//        // converting to IST
//        ZonedDateTime zonedIST = zonedUTC.withZoneSameInstant(ZoneId.of("America/São Paulo"));

        // TODO: Problemas com "TimeZone" ao salvar no banco. Verificar a configuração do Hibernate/JPA e do próprio banco de dados.
        // Ou tentar fazer a conversão manualmente aqui antes de salvar.
        Timetable timetable = new Timetable();
        timetable.setTeacherClass(tc);
        timetable.setDayOfWeek(request.dayOfWeek());
        timetable.setStartTime(request.startTime());
        timetable.setEndTime(request.endTime());
        timetable.setRoomName(request.roomName());

        return timetableMapper.toResponse(timetableRepository.save(timetable));
    }

    @Override
    @Transactional
    public TimetableResponse update(Long id, TimetableRequest request) {
        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horário não encontrado"));

        TeacherClass tc = teacherClassRepository.findById(request.teacherClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo Professor/Turma não encontrado"));

        // Validação de conflitos (Ignorando o ID atual para permitir edição no mesmo horário)
        if (hasConflictIgnoringSelf(id, tc, request)) {
            throw new BusinessResourceException("Conflito: O novo horário colide com uma aula existente.");
        }

        timetableMapper.updateEntity(request, timetable);
        timetable.setTeacherClass(tc); // Atualiza o vínculo se necessário

        return timetableMapper.toResponse(timetableRepository.save(timetable));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!timetableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Horário não encontrado.");
        }
        timetableRepository.deleteById(id);
    }

    // Método auxiliar para validar conflito ignorando o próprio registro
    private boolean hasConflictIgnoringSelf(Long currentId, TeacherClass tc, TimetableRequest request) {
        // Nota: Seria necessário adicionar métodos no Repository que recebam o ID para ignorar:
        // "AND t.id <> :currentId" nas queries de conflito.
        return timetableRepository.hasTeacherConflictIgnoringId(tc.getTeacher().getId(),
                request.dayOfWeek(), request.startTime(), request.endTime(), currentId)
                || timetableRepository.hasClassConflictIgnoringId(tc.getSchoolClass().getId(),
                request.dayOfWeek(), request.startTime(), request.endTime(), currentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> findBySchoolClassId(Long schoolClassId) {
        return timetableRepository.findByTeacherClass_SchoolClass_Id(schoolClassId)
                .stream().map(timetableMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> findByTeacherId(Long teacherId) {
        return timetableRepository.findByTeacherClass_Teacher_Id(teacherId)
                .stream().map(timetableMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TimetableResponse getNextClass(AgendaUserDetails currentUser) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalTime now = LocalTime.now();

        Optional<Timetable> nextClass = Optional.empty();

        if (currentUser.hasRole(UserRole.TEACHER)) {
            nextClass = timetableRepository.findNextTeacherClass(currentUser.getId(), today, now);
        } else if (currentUser.hasRole(UserRole.STUDENT)) {
            // Busca a turma do aluno primeiro
            Student student = studentRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

            nextClass = timetableRepository.findNextStudentClass(student.getSchoolClass().getId(), today, now);
        }

        return nextClass.map(timetableMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Não há mais aulas agendadas para hoje."));
    }

    // TODO: Implementar método de geração de relatórios em PDF mais elaborado conforme necessário,
    //  utilizando o JasperReports! Apenas verificar se a dependência do iText está ok para o projeto.
    // Método para geração de relatórios de horários em PDF (exemplo simples)
    @Override
    public byte[] generateTimetablePdf(Long schoolClassId) {
        List<Timetable> schedules = timetableRepository.findByTeacherClass_SchoolClass_Id(schoolClassId);
        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Horário Escolar - " + schoolClass.getName())
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(18));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 4, 4}))
                .useAllAvailableWidth();

        table.addHeaderCell("Dia");
        table.addHeaderCell("Horário");
        table.addHeaderCell("Disciplina");
        table.addHeaderCell("Professor");

        // Ordenar por dia da semana e hora antes de iterar
        schedules.stream()
                .sorted(Comparator.comparing(Timetable::getDayOfWeek).thenComparing(Timetable::getStartTime))
                .forEach(t -> {
                    table.addCell(t.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")));
                    table.addCell(t.getStartTime() + " - " + t.getEndTime());
                    table.addCell(t.getTeacherClass().getSubject().getName());
                    table.addCell(t.getTeacherClass().getTeacher().getName());
                });

        document.add(table);
        document.close();
        return baos.toByteArray();
    }
}
