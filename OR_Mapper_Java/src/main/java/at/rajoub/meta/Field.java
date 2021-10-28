package at.rajoub.meta;

import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

@Getter
public class Field {
    private String columnName;
    private Class<?> type;
    private Entity entity;
    private Method method;
    private Method setMethod;
    private boolean nullable;
    private boolean primaryKey;
    private boolean foreignKey;
    //private String foreignKeyFieldName;
    @SneakyThrows
    public Field(java.lang.reflect.Field field, Entity entity){
        this.entity =entity;
        type = field.getType();
        columnName = field.getName();
        entity.getType().getMethod("get" +field.getName());
    }
}
