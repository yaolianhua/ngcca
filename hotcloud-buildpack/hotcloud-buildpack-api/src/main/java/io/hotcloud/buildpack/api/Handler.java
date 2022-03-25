package io.hotcloud.buildpack.api;

/**
 * @author yaolianhua789@gmail.com
 */
public interface Handler<I, O> {

    /**
     * Forms a contract to all stage handlers to accept a certain type of input and return a processed output.
     *
     * @param input <I> the input type of the handler
     * @return <O> the processed output type of the handler
     */
    O process(I input);
}
