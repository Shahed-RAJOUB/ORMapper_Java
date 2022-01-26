package at.rajoub.model;

import at.rajoub.meta.annotation.Column;
import at.rajoub.meta.annotation.PrimaryKey;
import at.rajoub.meta.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(tableName = "students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEntity {

    @PrimaryKey
    @Column(columnName = "student_id")
    private int student_id;

    @Column(columnName = "student_name")
    private String student_name;
}
