package com.example.school.util.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.school.dao.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class ClassroomSeeder {
    private static final Logger log = LoggerFactory.getLogger(ClassroomSeeder.class);
    public static void seed(Random random) {
        String sql = "INSERT INTO classrooms (room_code, building, capacity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            class BuildingInfo {
                String name;
                String prefix;
                int floors;

                BuildingInfo(String name, String prefix, int floors) {
                    this.name = name;
                    this.prefix = prefix;
                    this.floors = floors;
                }
            }

            BuildingInfo[] buildings = {
                    new BuildingInfo("Main Building", "MB", 3),
                    new BuildingInfo("Science Block", "SB", 4),
                    new BuildingInfo("Engineering Hall", "EH", 3),
                    new BuildingInfo("Arts Wing", "AW", 2)
            };

            for (BuildingInfo b : buildings) {
                for (int floor = 1; floor <= b.floors; floor++) {
                    int numRoomsPerFloor = 8 + random.nextInt(8); // 8 to 15 rooms per floor
                    
                    for (int roomNum = 1; roomNum <= numRoomsPerFloor; roomNum++) {
                        String roomCode = String.format("%s-%d%02d", b.prefix, floor, roomNum);
                        
                        // Realistic capacity distribution: Small/Medium rooms more common
                        int capacity;
                        int probability = random.nextInt(100);
                        if (probability < 75) {
                            // Regular classrooms: 20, 30, 40, 50, or 60
                            capacity = 20 + (random.nextInt(5) * 10);
                        } else {
                            // Lecture halls/Large rooms: 100, 150, 200, 250
                            capacity = 100 + (random.nextInt(4) * 50);
                        }

                        pstmt.setString(1, roomCode);
                        pstmt.setString(2, b.name);
                        pstmt.setInt(3, capacity);
                        pstmt.executeUpdate();
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            log.error("An exception occurred: ", e);
        }
    }
}
