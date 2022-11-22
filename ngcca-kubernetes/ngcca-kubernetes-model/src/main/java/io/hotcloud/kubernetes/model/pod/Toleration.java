package io.hotcloud.kubernetes.model.pod;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Toleration {

    private Effect effect = Effect.NoSchedule;
    private String key;
    private Long tolerationSeconds;

    private Operator operator = Operator.Exists;
    private String value;

    public enum Effect {
        //
        NoSchedule, PreferNoSchedule, NoExecute
    }

    public enum Operator {
        //
        Exists, Equal
    }
}
