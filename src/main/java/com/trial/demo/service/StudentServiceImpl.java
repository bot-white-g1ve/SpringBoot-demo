package com.trial.demo.service;

import com.trial.demo.converter.StudentConverter;
import com.trial.demo.dao.Student;
import com.trial.demo.dao.StudentRepository;
import com.trial.demo.dto.StudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public StudentDTO getStudentById(long id) {
        Student student = studentRepository.findById(id).orElseThrow(RuntimeException::new);
        return StudentConverter.converStudent(student);
    }

    @Override
    public Long addNewStudent(StudentDTO studentDTO){
        List<Student> studentList = studentRepository.findByEmail(studentDTO.getEmail());
        if (!CollectionUtils.isEmpty(studentList)){
            throw new IllegalStateException("email:"+studentDTO.getEmail()+" has been taken");
        }
        Student student = studentRepository.save(StudentConverter.convertStudentDTO(studentDTO));
        return student.getId();
    }

    public void deleteStudentById(long id){
        studentRepository.findById(id).orElseThrow(()->new IllegalArgumentException("id: "+id+" does not exist"));
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional // 如果失败回滚
    public StudentDTO updateStudentById(long id, String name, String email) {
        Student studentPre = studentRepository.findById(id).orElseThrow(()->new IllegalArgumentException("id: "+id+" does not exist"));
        if (StringUtils.hasLength(name) && !studentPre.getName().equals(name)){
            studentPre.setName(name);
        }
        if (StringUtils.hasLength(email) && !studentPre.getEmail().equals(email)){
            studentPre.setEmail(email);
        }
        Student student = studentRepository.save(studentPre);
        return StudentConverter.converStudent(student);
    }
}

