package ru.iate.diplom.enteties;

import javax.persistence.*;

@Entity
@Table(name = "fields")
public class Field {

    @Override
    public String toString() {
        return "Field{" +
                "depth=" + depth +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", comment='" + comment + '\'' +
                ", order=" + order +
                ", name='" + name + '\'' +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL )
    private Record record;
    private int depth;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Field(Record record, String type, String value, String comment, String name, int depth, int order) {
        this.record = record;
        this.type = type;
        this.value = value;
        this.comment = comment;
        this.name = name;
        this.depth = depth;
        this.order = order;
    }

    @Column(name = "type")
    private String type;

    @Column(name = "value")
    private String value;

    @Column(name = "comment")
    private String comment;

    @Column(name = "seq_id")
    private int order;

    public Field() {

    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}