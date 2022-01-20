package at.rajoub.meta;

import at.rajoub.meta.annotation.Column;
import at.rajoub.meta.annotation.ForeignKey;
import at.rajoub.meta.annotation.PrimaryKey;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;


@Getter
public class Field {
    private final String columnName;
    private final String fieldName;
    private final Class<?> type;
    private final Entity entity;
    private boolean primaryKey;
    private boolean foreignKey;
    private Method setMethod;
    private Method getMethod;
    private Class<?> FKJoinedEntity;

    @SneakyThrows
    public Field(java.lang.reflect.Field field, Entity entity) {
        this.entity = entity;
        type = field.getType();
        columnName = field.isAnnotationPresent(Column.class) && (!"".equals(field.getAnnotation(Column.class).columnName()))
                ? field.getAnnotation(Column.class).columnName()
                : field.getName();
        primaryKey = field.isAnnotationPresent(PrimaryKey.class);
        foreignKey = field.isAnnotationPresent(ForeignKey.class);
        fieldName = field.getName();
        getMethod = Arrays.stream(entity.getType().getDeclaredMethods())
                .filter(m -> ("get" + fieldName).equalsIgnoreCase(m.getName()))
                .findFirst().orElseThrow();
        setMethod = Arrays.stream(entity.getType().getDeclaredMethods())
                .filter(m -> ("set" + fieldName).equalsIgnoreCase(m.getName()))
                .findFirst().orElseThrow();
        FKJoinedEntity = field.isAnnotationPresent(ForeignKey.class) ? field.getAnnotation(ForeignKey.class).joinedTo() : null;
    }
}
