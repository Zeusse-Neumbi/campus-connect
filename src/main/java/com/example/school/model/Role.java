package com.example.school.model;

/**
 * Represents the role of a User in the system.
 */
public enum Role {
    ADMIN(1),
    TEACHER(2),
    STUDENT(3),
    PARENT(4);

    private final int id;

    Role(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Resolves a Role from its corresponding integer ID.
     * @param id The role ID (1=ADMIN, 2=TEACHER, etc.)
     * @return The Role enum, or null if invalid
     */
    public static Role fromId(int id) {
        for (Role role : values()) {
            if (role.id == id) {
                return role;
            }
        }
        return null;
    }
}
