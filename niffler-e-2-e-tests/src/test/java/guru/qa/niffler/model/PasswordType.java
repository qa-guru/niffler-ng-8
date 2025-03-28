package guru.qa.niffler.model;

public enum PasswordType {
    PASSWORD,
    TEXT;
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
