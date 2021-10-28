package at.rajoub.model;

import at.rajoub.meta.annotation.Table;

@Table(tableName = "test")
public class TestEntity {

    private Long id;
    private String testName;

    public Long getid() {
        return id;
    }

    public String gettestName() {
        return testName;
    }
}
