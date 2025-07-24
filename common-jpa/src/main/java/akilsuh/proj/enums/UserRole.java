package akilsuh.proj.enums;

public enum UserRole {
    STUDENT("Ученик"),
    PARENT("Родитель"),
    ADMIN("Администратор");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }

    public boolean equals(String role) {
        return this.role.equals(role);
    }
}
