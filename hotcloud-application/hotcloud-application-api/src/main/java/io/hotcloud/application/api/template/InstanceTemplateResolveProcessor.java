package io.hotcloud.application.api.template;

import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface InstanceTemplateResolveProcessor {

    /**
     * Parse k8s list of resources template
     *
     * @param namespace    k8s namespace
     * @param templateYaml template resource yaml e.g.
     *                     <pre>{@code
     *                     apiVersion: v1
     *                     kind: PersistentVolume
     *                     metadata:
     *                       name: pv-mongo-#{[namespace]}
     *                     spec:
     *                       accessModes:
     *                         - ReadWriteOnce
     *                       capacity:
     *                         storage: 1Gi
     *                       claimRef:
     *                         kind: PersistentVolumeClaim
     *                         name: pvc-mongo-#{[namespace]}
     *                         namespace: #{[namespace]}
     *                       nfs:
     *                         path: #{[nfs_path]}/#{[namespace]}/mongo
     *                         readOnly: false
     *                         server: #{[nfs_server]}
     *                       volumeMode: Filesystem
     *                       persistentVolumeReclaimPolicy: Retain
     *                       storageClassName: storage-class-application
     *                     ---
     *                     apiVersion: v1
     *                     kind: PersistentVolumeClaim
     *                     metadata:
     *                       name: pvc-mongo-#{[namespace]}
     *                       namespace: #{[namespace]}
     *                     spec:
     *                       accessModes:
     *                         - ReadWriteOnce
     *                       resources:
     *                         requests:
     *                           storage: 1Gi
     *                       volumeName: pv-mongo-#{[namespace]}
     *                     }
     *                     </pre>
     * @return resolved k8s yaml
     */
    @SuppressWarnings("unchecked")
    default <T> T process(String templateYaml, String namespace) {
        Assert.hasText(templateYaml, "template yaml is null");
        Assert.hasText(namespace, "namespace is null");

        Map<String, String> resolved = resolve(namespace);
        SpelExpressionParser parser = new SpelExpressionParser();
        String value = parser.parseExpression(templateYaml, new TemplateParserContext()).getValue(resolved, String.class);

        return (T) value;
    }

    /**
     * Supported instance template
     *
     * @return {@link Template}
     */
    Template support();

    /**
     * Populate yaml template mapping arguments
     *
     * @param namespace k8s namespace
     * @return arguments mapping
     */
    Map<String, String> resolve(String namespace);

}
