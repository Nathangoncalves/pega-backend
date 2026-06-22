package br.tcc.pega.service;

import br.tcc.pega.dto.StudentDto;
import br.tcc.pega.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentService extends CrudService<Student, StudentDto> {

    Page<StudentDto> findByResponsavelId(UUID responsavelId, Pageable pageable);

    StudentDto updateScore(UUID studentId, Integer points);
}
