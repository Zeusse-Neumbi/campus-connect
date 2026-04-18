package com.example.school.dao.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager {

        private static HikariDataSource dataSource;

        static {
                try {
                        Properties props = new Properties();
                        try (InputStream input = DatabaseManager.class.getClassLoader()
                                        .getResourceAsStream("application.properties")) {
                                if (input == null) {
                                        throw new RuntimeException("Sorry, unable to find application.properties");
                                }
                                props.load(input);
                        }

                        HikariConfig config = new HikariConfig();
                        config.setJdbcUrl(props.getProperty("db.url"));
                        config.setDriverClassName(props.getProperty("db.driver"));

                        // Optional: Set pool properties
                        config.setConnectionInitSql("PRAGMA foreign_keys=ON;");
                        
                        if (props.getProperty("db.pool.maximumPoolSize") != null) {
                                config.setMaximumPoolSize(
                                                Integer.parseInt(props.getProperty("db.pool.maximumPoolSize")));
                        }
                        if (props.getProperty("db.pool.minimumIdle") != null) {
                                config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minimumIdle")));
                        }

                        dataSource = new HikariDataSource(config);

                        initDatabase();
                } catch (Exception e) {
                        throw new RuntimeException("Error initializing database connection pool", e);
                }
        }

        public static Connection getConnection() throws SQLException {
                return dataSource.getConnection();
        }

        private static void initDatabase() {
                try (Connection conn = getConnection();
                                Statement stmt = conn.createStatement()) {

                        // Enable Foreign Keys
                        stmt.execute("PRAGMA foreign_keys = ON;");

                        // 1. Roles
                        stmt.execute("CREATE TABLE IF NOT EXISTS roles (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "role_name TEXT UNIQUE NOT NULL" +
                                        ");");

                        // 2. Users
                        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "email TEXT UNIQUE NOT NULL, " +
                                        "password TEXT NOT NULL, " +
                                        "role_id INTEGER NOT NULL, " +
                                        "first_name TEXT NOT NULL, " +
                                        "last_name TEXT NOT NULL, " +
                                        "phone TEXT, " +
                                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE" +
                                        ");");

                        // 3. Parents
                        stmt.execute("CREATE TABLE IF NOT EXISTS parents (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "user_id INTEGER UNIQUE NOT NULL, " +
                                        "address TEXT, " +
                                        "occupation TEXT, " +
                                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE" +
                                        ");");

                        // 4. Students
                        stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "user_id INTEGER UNIQUE NOT NULL, " +
                                        "student_number TEXT UNIQUE NOT NULL, " +
                                        "date_of_birth DATE, " +
                                        "gender TEXT CHECK(gender IN ('MALE', 'FEMALE')), " +
                                        "admission_date DATE, " +
                                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE" +
                                        ");");

                        // 5. Teachers
                        stmt.execute("CREATE TABLE IF NOT EXISTS teachers (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "user_id INTEGER UNIQUE NOT NULL, " +
                                        "employee_id TEXT UNIQUE NOT NULL, " +
                                        "specialization TEXT, " +
                                        "hire_date DATE, " +
                                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE" +
                                        ");");

                        // 6. Student-Parent Link
                        stmt.execute("CREATE TABLE IF NOT EXISTS student_parents (" +
                                        "student_id INTEGER NOT NULL, " +
                                        "parent_id INTEGER NOT NULL, " +
                                        "relationship TEXT NOT NULL, " +
                                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "PRIMARY KEY (student_id, parent_id), " +
                                        "FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE, " +
                                        "FOREIGN KEY (parent_id) REFERENCES parents (id) ON DELETE CASCADE" +
                                        ");");

                        // 7. Classrooms
                        stmt.execute("CREATE TABLE IF NOT EXISTS classrooms (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "room_code TEXT UNIQUE NOT NULL, " +
                                        "building TEXT, " +
                                        "capacity INTEGER CHECK(capacity > 0), " +
                                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                        ");");

                        // 8. Courses
                        stmt.execute("CREATE TABLE IF NOT EXISTS courses (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "course_code TEXT UNIQUE NOT NULL, " +
                                        "course_name TEXT NOT NULL, " +
                                        "description TEXT, " +
                                        "credits INT NOT NULL DEFAULT 0 CHECK(credits >= 0), " +
                                        "teacher_id INTEGER NOT NULL, " +
                                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (teacher_id) REFERENCES teachers (id)" +
                                        ");");

                        // 9. Course Groups
                        stmt.execute("CREATE TABLE IF NOT EXISTS course_groups (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "course_id INTEGER NOT NULL, " +
                                        "group_name TEXT, " +
                                        "capacity INTEGER CHECK(capacity > 0), " +
                                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE" +
                                        ");");

                        // 10. Student Groups
                        stmt.execute("CREATE TABLE IF NOT EXISTS student_groups (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "student_id INTEGER NOT NULL, " +
                                        "course_group_id INTEGER NOT NULL, " +
                                        "joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE, " +
                                        "FOREIGN KEY (course_group_id) REFERENCES course_groups (id) ON DELETE CASCADE, " +
                                        "UNIQUE (student_id, course_group_id)" +
                                        ");");

                        // 11. Enrollments
                        stmt.execute("CREATE TABLE IF NOT EXISTS enrollments (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "student_id INTEGER NOT NULL, " +
                                        "course_id INTEGER NOT NULL, " +
                                        "enrollment_date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE, " +
                                        "FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE, " +
                                        "UNIQUE (student_id, course_id)" +
                                        ");");

                        // 12. Course Sessions
                        stmt.execute("CREATE TABLE IF NOT EXISTS course_sessions (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "course_id INTEGER NOT NULL, " +
                                        "course_group_id INTEGER, " +
                                        "classroom_id INTEGER NOT NULL, " +
                                        "session_date DATE NOT NULL, " +
                                        "start_time TEXT NOT NULL, " +
                                        "end_time TEXT NOT NULL, " +
                                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE, " +
                                        "FOREIGN KEY (course_group_id) REFERENCES course_groups (id) ON DELETE CASCADE, " +
                                        "FOREIGN KEY (classroom_id) REFERENCES classrooms (id) ON DELETE CASCADE" +
                                        ");");

                        // 13. Attendance
                        stmt.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "course_session_id INTEGER NOT NULL, " +
                                        "student_id INTEGER NOT NULL, " +
                                        "status TEXT NOT NULL, " +
                                        "marked_at DATE NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (course_session_id) REFERENCES course_sessions (id) ON DELETE CASCADE, " +
                                        "FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE, " +
                                        "UNIQUE (course_session_id, student_id)" +
                                        ");");

                        // 14. Exams
                        stmt.execute("CREATE TABLE IF NOT EXISTS exams (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "course_id INTEGER NOT NULL, " +
                                        "exam_name TEXT NOT NULL, " +
                                        "exam_type TEXT NOT NULL, " +
                                        "exam_date DATE NOT NULL, " +
                                        "max_score REAL NOT NULL DEFAULT 20, " +
                                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE" +
                                        ");");

                        // 15. Exam Results
                        stmt.execute("CREATE TABLE IF NOT EXISTS exam_results (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "exam_id INTEGER NOT NULL, " +
                                        "student_id INTEGER NOT NULL, " +
                                        "score INT NOT NULL CHECK(score >= 0), " +
                                        "graded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                                        "remark TEXT, " +
                                        "FOREIGN KEY (exam_id) REFERENCES exams (id) ON DELETE CASCADE, " +
                                        "FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE, " +
                                        "UNIQUE (exam_id, student_id)" +
                                        ");");

                        // Seed Roles
                        stmt.execute("INSERT OR IGNORE INTO roles (id, role_name) VALUES (1, 'ADMIN');");
                        stmt.execute("INSERT OR IGNORE INTO roles (id, role_name) VALUES (2, 'TEACHER');");
                        stmt.execute("INSERT OR IGNORE INTO roles (id, role_name) VALUES (3, 'STUDENT');");
                        stmt.execute("INSERT OR IGNORE INTO roles (id, role_name) VALUES (4, 'PARENT');");

                        // Call DataSeeder only if Users table is empty
                        com.example.school.dao.UserDao userDao = new com.example.school.dao.impl.UserDaoSqliteImpl();
                        if (userDao.count() == 0) {
                                new com.example.school.util.DataSeeder().seed();
                        }

                } catch (SQLException e) {
                        throw new RuntimeException("Error initializing database", e);
                }
        }
}
