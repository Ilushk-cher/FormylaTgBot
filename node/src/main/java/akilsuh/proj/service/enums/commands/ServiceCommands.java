package akilsuh.proj.service.enums.commands;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    START("/start"),
    CANCEL("/cancel");

    private final String cmd;

    ServiceCommands(String cmd) {
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
