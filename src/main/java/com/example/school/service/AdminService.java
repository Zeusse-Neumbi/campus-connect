package com.example.school.service;

import com.example.school.dao.*;
import com.example.school.model.*;
import com.example.school.util.PasswordUtil;
import java.util.*;

public class AdminService {

    private final UserDao userDao;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final CourseDao courseDao;
    private final ClassroomDao classroomDao;
    private final CourseGroupDao courseGroupDao;
    private final CourseSessionDao courseSessionDao;
    private final ParentDao parentDao;
    private final StudentParentDao studentParentDao;
    private final EnrollmentDao enrollmentDao;
    private final ExamDao examDao;
    private final ExamResultDao examResultDao;
    private final AttendanceDao attendanceDao;

    public AdminService(UserDao userDao, StudentDao studentDao, TeacherDao teacherDao, CourseDao courseDao,
            ClassroomDao classroomDao, CourseGroupDao courseGroupDao, CourseSessionDao courseSessionDao,
            ParentDao parentDao, StudentParentDao studentParentDao, EnrollmentDao enrollmentDao,
            ExamDao examDao, ExamResultDao examResultDao, AttendanceDao attendanceDao) {
        this.userDao = userDao;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.courseDao = courseDao;
        this.classroomDao = classroomDao;
        this.courseGroupDao = courseGroupDao;
        this.courseSessionDao = courseSessionDao;
        this.parentDao = parentDao;
        this.studentParentDao = studentParentDao;
        this.enrollmentDao = enrollmentDao;
        this.examDao = examDao;
        this.examResultDao = examResultDao;
        this.attendanceDao = attendanceDao;
    }

    // Dashboard Stats
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("userCount", userDao.findAll().size());
        stats.put("studentCount", studentDao.findAll().size());
        stats.put("teacherCount", teacherDao.findAll().size());
        stats.put("parentCount", parentDao.findAll().size());
        stats.put("courseCount", courseDao.findAll().size());
        stats.put("classroomCount", classroomDao.findAll().size());
        stats.put("courseGroupCount", courseGroupDao.findAll().size());
        stats.put("sessionCount", courseSessionDao.findAll().size());
        stats.put("studentParentLinkCount", studentParentDao.findAll().size());
        return stats;
    }

    // User Management
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public List<User> searchUsers(String query, int page, int pageSize) {
        if (query != null && !query.trim().isEmpty()) {
            return userDao.search(query, page, pageSize);
        }
        return userDao.search("", page, pageSize);
    }

    public int countUsers(String query) {
        if (query != null && !query.trim().isEmpty()) {
            return userDao.count(query);
        }
        return userDao.count();
    }

    public Optional<User> getUserById(int id) {
        return userDao.findById(id);
    }

    public void createUser(User user, String password) {
        if (password != null && !password.isEmpty()) {
            user.setPassword(PasswordUtil.hashPassword(password));
        }
        userDao.save(user);
    }

    // Complex create with role-specific logic (Student/Teacher)
    public int createUserWithRole(User user, String password, Map<String, String> roleData) {
        if (password != null && !password.isEmpty()) {
            user.setPassword(PasswordUtil.hashPassword(password));
        }
        int newUserId = userDao.save(user);

        if (newUserId != -1) {
            Role role = user.getRole();
            if (role == Role.STUDENT) {
                Student student = new Student(0, newUserId,
                        roleData.get("studentNumber"),
                        roleData.get("dateOfBirth"),
                        roleData.get("gender"),
                        roleData.get("admissionDate"));
                studentDao.save(student);
            } else if (role == Role.TEACHER) {
                Teacher teacher = new Teacher(0, newUserId,
                        roleData.get("employeeId"),
                        roleData.get("specialization"),
                        roleData.get("hireDate"));
                teacherDao.save(teacher);
            } else if (role == Role.PARENT) {
                Parent parent = new Parent(0, newUserId,
                        roleData.get("address"),
                        roleData.get("occupation"));
                parentDao.save(parent);
            }
        }
        return newUserId;
    }

    public void updateUser(User user, String password) {
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(PasswordUtil.hashPassword(password));
        }
        userDao.update(user);
    }

    public void deleteUser(int userId) {
        userDao.delete(userId);
    }

    // Student Management
    public List<Student> searchStudents(String query, int page, int pageSize) {
        if (query != null && !query.trim().isEmpty()) {
            return studentDao.search(query, page, pageSize);
        }
        return studentDao.search("", page, pageSize);
    }

    public int countStudents(String query) {
        if (query != null && !query.trim().isEmpty()) {
            return studentDao.count(query);
        }
        return studentDao.count("");
    }

    public void updateStudent(Student student) {
        studentDao.update(student);
    }

    public void deleteStudent(int id) {
        studentDao.delete(id);
    }

    public Optional<Student> getStudentById(int id) {
        return studentDao.findById(id);
    }

    public Optional<Student> getStudentByUserId(int userId) {
        return studentDao.findByUserId(userId);
    }

    public List<Student> getAllStudents() {
        return studentDao.findAll();
    }

    // Teacher Management
    public List<Teacher> searchTeachers(String query, int page, int pageSize) {
        if (query != null && !query.trim().isEmpty()) {
            return teacherDao.search(query, page, pageSize);
        }
        return teacherDao.search("", page, pageSize);
    }

    public int countTeachers(String query) {
        if (query != null && !query.trim().isEmpty()) {
            return teacherDao.count(query);
        }
        return teacherDao.count("");
    }

    public void updateTeacher(Teacher teacher) {
        teacherDao.update(teacher);
    }

    public void deleteTeacher(int id) {
        teacherDao.delete(id);
    }

    public Optional<Teacher> getTeacherById(int id) {
        return teacherDao.findById(id);
    }

    public Optional<Teacher> getTeacherByUserId(int userId) {
        return teacherDao.findByUserId(userId);
    }

    public List<Teacher> getAllTeachers() {
        return teacherDao.findAll();
    }

    // Course Management
    public List<Course> getAllCourses() {
        return courseDao.findAll();
    }

    public List<Course> searchCourses(String query, int page, int pageSize) {
        return courseDao.search(query, page, pageSize);
    }

    public int countCourses(String query) {
        return courseDao.count(query);
    }

    public List<Course> getStudentCourses(int studentId) {
        return courseDao.findByStudentId(studentId);
    }

    public List<Course> getTeacherCourses(int teacherId) {
        return courseDao.findByTeacherId(teacherId);
    }

    public void createCourse(Course course) {
        courseDao.save(course);
    }

    public void updateCourse(Course course) {
        courseDao.update(course);
    }

    public void deleteCourse(int id) {
        courseDao.delete(id);
    }

    // Classroom Management
    public List<Classroom> getAllClassrooms() {
        return classroomDao.findAll();
    }

    public List<Classroom> searchClassrooms(String query, List<String> buildings, Integer minCapacity, int page, int pageSize) {
        return classroomDao.searchWithFilters(query, buildings, minCapacity, page, pageSize);
    }

    public int countClassrooms(String query, List<String> buildings, Integer minCapacity) {
        return classroomDao.countWithFilters(query, buildings, minCapacity);
    }

    public Optional<Classroom> getClassroomById(int id) {
        return classroomDao.findById(id);
    }

    public void createClassroom(Classroom classroom) {
        classroomDao.save(classroom);
    }

    public void updateClassroom(Classroom classroom) {
        classroomDao.update(classroom);
    }

    public void deleteClassroom(int id) {
        classroomDao.delete(id);
    }

    // Course Group Management
    public List<CourseGroup> getAllCourseGroups() {
        return courseGroupDao.findAll();
    }

    public List<CourseGroup> searchCourseGroups(String query, int page, int pageSize) {
        return courseGroupDao.search(query, page, pageSize);
    }

    public int countCourseGroups(String query) {
        return courseGroupDao.count(query);
    }

    public List<CourseGroup> getCourseGroupsByCourseId(int courseId) {
        return courseGroupDao.findByCourseId(courseId);
    }

    public void createCourseGroup(CourseGroup group) {
        courseGroupDao.save(group);
    }

    public void updateCourseGroup(CourseGroup group) {
        courseGroupDao.update(group);
    }

    public void deleteCourseGroup(int id) {
        courseGroupDao.delete(id);
    }

    // Course Session Management
    public List<CourseSession> getAllCourseSessions() {
        return courseSessionDao.findAll();
    }

    public List<CourseSession> searchCourseSessions(String query, int page, int pageSize) {
        return courseSessionDao.search(query, page, pageSize);
    }

    public int countCourseSessions(String query) {
        return courseSessionDao.count(query);
    }

    public List<CourseSession> getCourseSessionsByCourseId(int courseId) {
        return courseSessionDao.findByCourseId(courseId);
    }

    public List<CourseSession> getCourseSessionsByDateRange(String startDate, String endDate) {
        return courseSessionDao.findByDateRange(startDate, endDate);
    }

    public void createCourseSession(CourseSession session) {
        courseSessionDao.save(session);
    }

    public void updateCourseSession(CourseSession session) {
        courseSessionDao.update(session);
    }

    public void deleteCourseSession(int id) {
        courseSessionDao.delete(id);
    }

    // Parent Management
    public List<Parent> getAllParents() {
        return parentDao.findAll();
    }

    public List<Parent> searchParents(String query, int page, int pageSize) {
        return parentDao.search(query, page, pageSize);
    }

    public int countParents(String query) {
        return parentDao.count(query);
    }

    public Optional<Parent> getParentByUserId(int userId) {
        return parentDao.findByUserId(userId);
    }

    public Optional<Parent> getParentById(int id) {
        return parentDao.findById(id);
    }

    public void updateParent(Parent parent) {
        parentDao.update(parent);
    }

    public void deleteParent(int id) {
        parentDao.delete(id);
    }

    // Student-Parent Link Management
    public void linkStudentToParent(int studentId, int parentId, String relationship) {
        studentParentDao.link(studentId, parentId, relationship);
    }

    public void unlinkStudentFromParent(int studentId, int parentId) {
        studentParentDao.unlink(studentId, parentId);
    }

    public List<java.util.Map<String, Object>> getAllStudentParentLinks() {
        return studentParentDao.findAll();
    }

    public List<java.util.Map<String, Object>> searchStudentParentLinks(String query, int page, int pageSize) {
        return studentParentDao.search(query, page, pageSize);
    }

    public int countStudentParentLinks(String query) {
        return studentParentDao.count(query);
    }

    public List<java.util.Map<String, Object>> getLinkedStudents(int parentId) {
        return studentParentDao.findStudentsByParentId(parentId);
    }

    public List<java.util.Map<String, Object>> getLinkedParents(int studentId) {
        return studentParentDao.findParentsByStudentId(studentId);
    }
}

