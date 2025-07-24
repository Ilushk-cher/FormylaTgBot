package akilsuh.proj.service.enums.commands;

public enum ParentCommands {
    ADD_CHILD("/add_child");
    private final String cmd;

    ParentCommands(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

    public boolean equals(String cmd) {
        return this.cmd.equals(cmd);
    }
}
