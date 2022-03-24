package io.hotcloud.buildpack.api;

/**
 * @param <I> the input type of the handler
 * @param <O> the processed output type of the handler
 * @author yaolianhua789@gmail.com
 *
 * <p>Forms a contract to all stage handlers to accept a certain type of input and return a processed
 * output.
 */
public interface Handler<I, O> {

    O process(I input);
}
