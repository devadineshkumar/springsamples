package modal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

    @Entity
    @Data
    public class Exam {
        @Id
        private Long id;
        private String name;
        private int english;
        private int maths;
        private int science;
        private int totalMarks;
        private boolean isPassed;
    }