package io.hotcloud.buildpack.api;


/**
 * @param <I> the type of the input for the first stage handler
 * @param <O> the final stage handler's output type
 * @author yaolianhua789@gmail.com
 *
 * <p> Main Pipeline class that initially sets the current handler. Processed output of the initial
 * handler is then passed as the input to the next stage handlers.
 */
public class Pipeline<I, O> {

    private final Handler<I, O> currentHandler;

    public Pipeline(Handler<I, O> currentHandler) {
        this.currentHandler = currentHandler;
    }

    /**
     * The Pipeline pattern uses ordered stages to process a sequence of input values. Each implemented
     * task is represented by a stage of the pipeline. You can think of pipelines as similar to assembly
     * lines in a factory, where each item in the assembly line is constructed in stages. The partially
     * assembled item is passed from one assembly stage to another. The outputs of the assembly line
     * occur in the same order as that of the inputs.
     */
    public <K> Pipeline<I, K> next(Handler<O, K> nextHandler) {
        return new Pipeline<>(input -> nextHandler.process(currentHandler.process(input)));
    }

    /**
     * Start pipeline
     */
    public O execute(I input) {
        return currentHandler.process(input);
    }
}
