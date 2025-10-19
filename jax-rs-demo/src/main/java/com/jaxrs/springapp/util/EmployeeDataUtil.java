package com.jaxrs.springapp.util;

import com.jaxrs.springapp.model.Employee;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Utility that holds an in-memory store of Employee objects and can return a random list on each request.
 * Also exposes CRUD operations backed by the in-memory store.
 */
@Component
public class EmployeeDataUtil {

    private final ConcurrentHashMap<Long, Employee> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);
    private final Random rnd = new Random();

    public EmployeeDataUtil() {
        // Initialize with some random employees
        for (int i = 0; i < 10; i++) {
            Employee e = randomEmployee();
            e.setId(idGen.getAndIncrement());
            store.put(e.getId(), e);
        }
    }

    private Employee randomEmployee() {
        String[] fn = {"Alice", "Bob", "Carol", "David", "Eve", "Frank", "Grace", "Heidi", "Ivan", "Judy"};
        String[] ln = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson"};
        String first = fn[rnd.nextInt(fn.length)];
        String last = ln[rnd.nextInt(ln.length)];
        String email = (first + "." + last + idGen.get() + "@example.com").toLowerCase();
        String position = rnd.nextBoolean() ? "Developer" : "QA";
        return new Employee(null, first, last, email, position);
    }

    // Return a random list of employees every time (size between 1 and current size)
    public List<Employee> randomList() {
        List<Employee> all = new ArrayList<>(store.values());
        if (all.isEmpty()) return Collections.emptyList();
        int size = 1 + rnd.nextInt(all.size());
        Collections.shuffle(all, rnd);
        return all.stream().limit(size).collect(Collectors.toList());
    }

    // CRUD operations on the in-memory store
    public List<Employee> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Employee save(Employee employee) {
        if (employee.getId() == null) {
            employee.setId(idGen.getAndIncrement());
        }
        store.put(employee.getId(), employee);
        return employee;
    }

    public Optional<Employee> update(Long id, Employee employee) {
        if (!store.containsKey(id)) return Optional.empty();
        employee.setId(id);
        store.put(id, employee);
        return Optional.of(employee);
    }

    public boolean delete(Long id) {
        return store.remove(id) != null;
    }

    public void clear() {
        store.clear();
        idGen.set(1);
    }
}
