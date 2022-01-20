package at.rajoub.model;

import at.rajoub.meta.annotation.Column;
import at.rajoub.meta.annotation.PrimaryKey;
import at.rajoub.meta.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(tableName = "test")
@Data
@NoArgsConstructor
public class TestEntity {
    @PrimaryKey
    @Column(columnName = "id")
    private Long id;
    @Column(columnName = "testName")
    private String testName;

}
