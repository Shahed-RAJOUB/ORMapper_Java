package at.rajoub.meta;

import at.rajoub.meta.annotation.Table;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Entity {

    private String tableName;
    private Class<?> type;
    private List<Field> fields;

    @Builder
    public Entity(Class<?> type) {
        Table annotation = type.getAnnotation(Table.class);
        tableName = annotation.tableName();
        this.type = type;
        fields = Arrays.stream(type.getDeclaredFields())
                .map(field -> new Field(field, this))
                .collect(Collectors.toList());
    }
    public Field getPrimaryKey(){
        return fields.stream().filter(Field::isPrimaryKey).findFirst().orElseThrow();
    }
    public List<Field> allFieldsNoPK(){
        return fields.stream().filter(f->!f.isPrimaryKey()).collect(Collectors.toList());
    }
}
