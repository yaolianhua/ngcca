package io.hotcloud.service.application;

public interface ApplicationInstanceProcessor<I> {

    Integer DEFAULT_ORDER = 0;

    void processCreate(I input);

    void processDelete(I input);

    default void processFailed(I input) {

    }

    int order();

    Type getType();

    enum Type {
        //
        SERVICE,
        //
        INGRESS,
        //
        IMAGE_BUILD,
        //
        DEPLOYMENT
    }
}
