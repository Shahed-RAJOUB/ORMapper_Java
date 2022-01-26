package at.rajoub;

import at.rajoub.model.StudentEntity;
import at.rajoub.model.TestEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrmTest {

    @Test
    void selectByIdEntityFromTable_true() {
        // this entity exists in the table
        TestEntity Expected_Entity = TestEntity.builder().id(2L).testName("test_1").build();
        Orm orm = new Orm();
        //this entity is extracted using ORM
        TestEntity Result = orm.SelectByID(TestEntity.class, 2);
        assertEquals(Result, Expected_Entity);
    }

    @Test
    void selectAllColumnsNames_true() throws SQLException {
        List<String> expextedColumns = Arrays.asList("id", "testName");
        Orm orm = new Orm();
        List<String> results = orm.SelectAllColumns(TestEntity.class);
        assertEquals(results, expextedColumns);
    }

    @Test
    void selectAllEntities_true() throws SQLException {
        List<TestEntity> expected = new ArrayList<>();
        TestEntity Expected_Entity_1 = TestEntity.builder().id(2L).testName("test_1").build();
        TestEntity Expected_Entity_2 = TestEntity.builder().id(3L).testName("test_2").build();
        TestEntity Expected_Entity_3 = TestEntity.builder().id(4L).testName("test_3").build();
        expected.add(Expected_Entity_1);
        expected.add(Expected_Entity_2);
        expected.add(Expected_Entity_3);
        Orm orm = new Orm();
        List<TestEntity> results = orm.SelectAllRows(TestEntity.class);
        assertEquals(results, expected);
    }

    @Test
    void selectbyColumnFromEntity_true() {
        // this entity exists in the table
        TestEntity Expected_Entity = TestEntity.builder().id(2L).testName("test_1").build();
        Orm orm = new Orm();
        //this entity is extracted using ORM
        List<TestEntity> Result =orm.SelectbyColumn(TestEntity.class, "testName","test_1");
        assertEquals(Result.get(0), Expected_Entity.getTestName());
    }

    @Test
    void insert() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Orm orm = new Orm();
        orm.CreateTable(StudentEntity.class);
        StudentEntity Expected_Entity = StudentEntity.builder().student_name("Azam").build();
        orm.Insert(Expected_Entity);
        List<StudentEntity> Result = orm.SelectbyColumn(StudentEntity.class , "student_name" , "Azam");
        assertEquals(Result.get(0), Expected_Entity);
    }

    @Test
    void updatebyID() throws SQLException, InvocationTargetException, IllegalAccessException {
        Orm orm = new Orm();
        StudentEntity entity = orm.SelectByID(StudentEntity.class , 1);
        entity.setStudent_name("test");
        orm.UpdatebyID(1 , entity);
        StudentEntity Result = orm.SelectByID(StudentEntity.class , 1);
        assertEquals(Result.getStudent_name(), "test");
    }

}