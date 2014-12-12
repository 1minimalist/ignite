/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.compute;

import org.apache.ignite.*;
import org.jetbrains.annotations.*;

/**
 * This exception defines execution rejection. This exception is used to indicate
 * the situation when execution service provided by the user in configuration
 * rejects execution.
 * @see org.apache.ignite.configuration.IgniteConfiguration#getExecutorService()
 */
public class ComputeExecutionRejectedException extends IgniteCheckedException {
    /** */
    private static final long serialVersionUID = 0L;

    /**
     * Creates new execution rejection exception with given error message.
     *
     * @param msg Error message.
     */
    public ComputeExecutionRejectedException(String msg) {
        super(msg);
    }

    /**
     * Creates new execution rejection given throwable as a cause and
     * source of error message.
     *
     * @param cause Non-null throwable cause.
     */
    public ComputeExecutionRejectedException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    /**
     * Creates new execution rejection exception with given error message
     * and optional nested exception.
     *
     * @param msg Error message.
     * @param cause Optional nested exception (can be {@code null}).
     */
    public ComputeExecutionRejectedException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
