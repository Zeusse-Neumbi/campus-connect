package com.example.school.util.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class StudentParentSeeder {
    private static final Logger log = LoggerFactory.getLogger(StudentParentSeeder.class);
    public static void seed(Random random) {
        String insertStudentParent = "INSERT INTO student_parents (student_id, parent_id, relationship) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertStudentParent)) {

            conn.setAutoCommit(false);

            List<Integer> studentIds = SeederUtils.getIds("students");
            List<Integer> parentIds = SeederUtils.getIds("parents");

            if (studentIds.isEmpty() || parentIds.isEmpty())
                return;

            Collections.shuffle(studentIds);
            Collections.shuffle(parentIds);

            int singleParentStudentCount = (int) (studentIds.size() * 0.50);
            int dualParentStudentCount = (int) (studentIds.size() * 0.20);
            int parentsWithMultipleCount = (int) (parentIds.size() * 0.40);

            int totalLinksNeeded = singleParentStudentCount + (dualParentStudentCount * 2);

            List<Integer> parentLinkPool = new ArrayList<>();
            for (int i = 0; i < parentIds.size(); i++) {
                int pid = parentIds.get(i);
                if (i < parentsWithMultipleCount) {
                    parentLinkPool.add(pid);
                    parentLinkPool.add(pid);
                } else {
                    parentLinkPool.add(pid);
                }
            }

            while (parentLinkPool.size() > totalLinksNeeded) {
                parentLinkPool.remove(parentLinkPool.size() - 1);
            }
            while (parentLinkPool.size() < totalLinksNeeded) {
                parentLinkPool.add(parentIds.get(random.nextInt(parentIds.size())));
            }

            Collections.shuffle(parentLinkPool);
            Set<String> assignedLinks = new HashSet<>();
            String[] relationships = { "Mother", "Father", "Guardian", "Step-parent", "Aunt", "Uncle" };

            for (int i = 0; i < singleParentStudentCount; i++) {
                int sid = studentIds.get(i);
                int pid = findValidParent(sid, parentLinkPool, assignedLinks, parentIds);
                if (pid != -1) {
                    pstmt.setInt(1, sid);
                    pstmt.setInt(2, pid);
                    pstmt.setString(3, relationships[random.nextInt(relationships.length)]);
                    pstmt.executeUpdate();
                }
            }

            for (int i = singleParentStudentCount; i < singleParentStudentCount + dualParentStudentCount; i++) {
                int sid = studentIds.get(i);

                int pid1 = findValidParent(sid, parentLinkPool, assignedLinks, parentIds);
                if (pid1 != -1) {
                    pstmt.setInt(1, sid);
                    pstmt.setInt(2, pid1);
                    pstmt.setString(3, "Mother");
                    pstmt.executeUpdate();
                }

                int pid2 = findValidParent(sid, parentLinkPool, assignedLinks, parentIds);
                if (pid2 != -1) {
                    pstmt.setInt(1, sid);
                    pstmt.setInt(2, pid2);
                    pstmt.setString(3, "Father");
                    pstmt.executeUpdate();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }

    private static int findValidParent(int sid, List<Integer> pool, Set<String> assigned,
            List<Integer> fallbackPool) {
        for (int i = 0; i < pool.size(); i++) {
            int pid = pool.get(i);
            String link = sid + "-" + pid;
            if (!assigned.contains(link)) {
                assigned.add(link);
                pool.remove(i);
                return pid;
            }
        }
        for (int pid : fallbackPool) {
            String link = sid + "-" + pid;
            if (!assigned.contains(link)) {
                assigned.add(link);
                return pid;
            }
        }
        return -1;
    }
}
