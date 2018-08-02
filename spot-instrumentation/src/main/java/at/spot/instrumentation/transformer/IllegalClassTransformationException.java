package at.spot.instrumentation.transformer;

import java.lang.instrument.IllegalClassFormatException;

public class IllegalClassTransformationException extends IllegalClassFormatException {

    protected Throwable rootCause;

    public IllegalClassTransformationException(final String message) {
        super(message);
    }

    public IllegalClassTransformationException(final String message, final Throwable rootCause) {
        this(message);
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return rootCause;
    }

    public void setRootCause(final Throwable rootCause) {
        this.rootCause = rootCause;
    }
}
