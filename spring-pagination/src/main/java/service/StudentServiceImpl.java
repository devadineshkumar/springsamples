package service;

import dto.StudentDto;
import dto.SearchCriteria;
import modal.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import repository.StudentRepository;
import repository.StudentSpecification;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    private StudentDto toDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .name(student.getName())
                .section(student.getSection())
                .address(student.getAddress())
                .totalMark(student.getTotalMark())
                .rank(student.getRank())
                .build();
    }

    private Student toEntity(StudentDto dto) {
        return Student.builder()
                .id(dto.getId())
                .name(dto.getName())
                .section(dto.getSection())
                .address(dto.getAddress())
                .totalMark(dto.getTotalMark())
                .rank(dto.getRank())
                .build();
    }

    @Override
    public StudentDto createStudent(StudentDto studentDto) {
        Student student = toEntity(studentDto);
        return toDto(studentRepository.save(student));
    }

    @Override
    public StudentDto updateStudent(Long id, StudentDto studentDto) {
        Student student = studentRepository.findById(id).orElseThrow();
        student.setName(studentDto.getName());
        student.setSection(studentDto.getSection());
        student.setAddress(studentDto.getAddress());
        student.setTotalMark(studentDto.getTotalMark());
        student.setRank(studentDto.getRank());
        return toDto(studentRepository.save(student));
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public StudentDto getStudent(Long id) {
        return studentRepository.findById(id).map(this::toDto).orElseThrow();
    }

    @Override
    public Page<StudentDto> searchStudents(SearchCriteria criteria, Pageable pageable) {
        return studentRepository.findAll(StudentSpecification.filterByCriteria(criteria), pageable).map(this::toDto);
    }
}
