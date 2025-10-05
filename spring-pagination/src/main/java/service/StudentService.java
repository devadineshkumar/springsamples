package service;

import dto.SearchCriteria;
import dto.StudentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {
    StudentDto createStudent(StudentDto studentDto);
    StudentDto updateStudent(Long id, StudentDto studentDto);
    void deleteStudent(Long id);
    StudentDto getStudent(Long id);
    Page<StudentDto> searchStudents(SearchCriteria criteria, Pageable pageable);
}
