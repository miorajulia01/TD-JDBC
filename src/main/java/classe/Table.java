package classe;

import java.time.Instant;
import java.util.List;

public class Table {
    private Integer id;
    private int number;
    private List<Order> orders;

public Table() {}

    public Table(Integer id, int number) {
        this.id = id;
        this.number = number;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }


    public boolean isAvailableAt(Instant t) {
        return true;
    }
}
