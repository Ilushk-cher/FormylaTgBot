package akilsuh.proj.service.enums.commands;

public enum ActiveUserCommands {
    MY_ACCOUNT("/my_account");
    private final String cmd;

    ActiveUserCommands(String cmd) {
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
