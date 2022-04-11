package shared;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class VectorOperationResultDto implements Serializable {
    private List<Float> v1;
    private List<Float> v2;
    private List<Float> result;
}
