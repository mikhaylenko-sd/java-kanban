package manager;

public class GeneratorId {
    private int id;

    public int generate() {
        id++;
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
