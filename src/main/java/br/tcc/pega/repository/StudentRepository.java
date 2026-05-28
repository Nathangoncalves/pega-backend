package br.tcc.pega.repository;

import br.tcc.pega.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByUserId(Long userId);
}
