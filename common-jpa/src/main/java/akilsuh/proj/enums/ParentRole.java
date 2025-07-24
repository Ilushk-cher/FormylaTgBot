package akilsuh.proj.enums;

public enum ParentRole {
    FATHER("Папа"),
    MOTHER("Мама");

    private final String parent;

    ParentRole(String parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return this.parent;
    }

    public boolean equals(String parent) {
        return this.parent.equals(parent);
    }
}
