package at.rajoub.meta;

import at.rajoub.meta.annotation.Table;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Entity {

    private String tableName;
    private Class<?> type;

    private List<Field> fields;

    public Entity(Class<?> type) {
        Table annotation = type.getAnnotation(Table.class);
        tableName = annotation.tableName();
        this.type = type;

        fields = Arrays.stream(type.getDeclaredFields())
                .map(field -> new Field(field, this))
                .collect(Collectors.toList());
    }
}
