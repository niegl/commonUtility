package commonUtility.consumer;

import java.util.Objects;

@FunctionalInterface
public interface TriConsumer<T,U,W> {
    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param w the third input argument
     */
    void accept(T t, U u, W w) ;

    default TriConsumer<T, U, W> andThen(TriConsumer<? super T, ? super U, ? super W> after) {
        Objects.requireNonNull(after);

        return (l, r, s) -> {
            accept(l, r, s);
            after.accept(l, r, s);
        };
    }

}
