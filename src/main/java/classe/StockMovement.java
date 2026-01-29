package classe;

import java.time.Instant;
import java.util.Objects;

public class StockMovement {
    private Integer id;
    private StockValue value;
    private MovementTypeEnum type;
    private Instant creationDatetime;

    public StockMovement (){};

    public StockMovement(Integer id, StockValue value, MovementTypeEnum type, Instant creationDatetime) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.creationDatetime = creationDatetime;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public StockValue getValue() { return value; }
    public void setValue(StockValue value) { this.value = value; }

    public MovementTypeEnum getType() { return type; }
    public void setType(MovementTypeEnum type) { this.type = type; }

    public Instant getCreationDatetime() { return creationDatetime; }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StockMovement that)) return false;
        return Objects.equals(id, that.id)  && type == that.type && Objects.equals(creationDatetime, that.creationDatetime) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, creationDatetime, value);
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                "id=" + id +
                ", type=" + type +
                ", creationDatetime=" + creationDatetime +
                ", value=" + value +
                '}';
    }
}
