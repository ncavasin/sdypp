package shared;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class VectorOperationResultDto {
    private List<Float> v1;
    private List<Float> v2;
    private List<Float> result;

    public VectorOperationResultDto(List<Float> v1, List<Float> v2, List<Float> result) {
        setV1(v1);
        setV2(v2);
        setResult(result);
    }
}
