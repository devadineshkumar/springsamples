package repository;

import dto.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import modal.Student;
import org.springframework.data.jpa.domain.Specification;

class StudentPredicateBuilder {
    public static Predicate build(Root<Student> root, CriteriaQuery<?> query, CriteriaBuilder cb, SearchCriteria criteria) {
        Predicate predicates = cb.conjunction();
        if (criteria.getName() != null && !criteria.getName().isEmpty()) {
            predicates = cb.and(predicates, cb.like(cb.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
        }
        if (criteria.getAddress() != null && !criteria.getAddress().isEmpty()) {
            predicates = cb.and(predicates, cb.like(cb.lower(root.get("address")), "%" + criteria.getAddress().toLowerCase() + "%"));
        }
        if (criteria.getSection() != null && !criteria.getSection().isEmpty()) {
            predicates = cb.and(predicates, cb.equal(root.get("section"), criteria.getSection()));
        }
        if (criteria.getTotalMark() != null) {
            predicates = cb.and(predicates, cb.equal(root.get("totalMark"), criteria.getTotalMark()));
        }
        if (criteria.getRank() != null) {
            predicates = cb.and(predicates, cb.equal(root.get("rank"), criteria.getRank()));
        }

        // how would you join the child tables if student has a one to many relationship with another table
        // if (criteria.getChildAttribute() != null) {
        //     Join<Student, ChildEntity> join = root.join("childEntities", JoinType.LEFT);
        //     predicates = cb.and(predicates, cb.equal(join.get("childAttribute"), criteria.getChildAttribute()));
        // }



        return predicates;
    }
}

public class StudentSpecification {
    public static Specification<Student> filterByCriteria(SearchCriteria criteria) {
        return (root, query, cb) -> StudentPredicateBuilder.build(root, query, cb, criteria);
    }
}
