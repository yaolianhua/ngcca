package io.hotcloud.kubernetes.model.workload;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class StatefulSetUpdateStrategy {

   private String type = "RollingUpdate";
   private RollingUpdate rollingUpdate = new RollingUpdate();

   @Data
   public static class RollingUpdate {
      private Integer partition = 0;
   }
}
