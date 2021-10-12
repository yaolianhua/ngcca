package io.hotCloud.core.kubernetes;

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

     enum  Effect{
         //
        NoSchedule, PreferNoSchedule, NoExecute
    }

    enum Operator{
         //
        Exists, Equal
    }
}
