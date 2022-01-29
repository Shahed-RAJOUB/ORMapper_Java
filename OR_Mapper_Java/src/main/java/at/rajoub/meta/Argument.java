package at.rajoub.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
/*
 *Argumentation used to build logic queries
 */
public class Argument {

    private String column;
    private Object value;
    private CompareOperator opr;

}
