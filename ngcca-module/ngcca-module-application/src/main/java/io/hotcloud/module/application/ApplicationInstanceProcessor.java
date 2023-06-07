package io.hotcloud.module.application;

public interface ApplicationInstanceProcessor<I> {

    Integer DEFAULT_ORDER = 0;

    void processCreate(I input);

    void processDelete(I input);

    int order();

    Type getType();

    enum Type {
        //
        Service,
        //
        Ingress,
        //
        ImageBuild,
        //
        Deployment
    }
}
