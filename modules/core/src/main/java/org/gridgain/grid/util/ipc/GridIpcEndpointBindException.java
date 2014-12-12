/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.grid.util.ipc;

import org.apache.ignite.*;

/**
 * Represents exception occurred during IPC endpoint binding.
 */
public class GridIpcEndpointBindException extends IgniteCheckedException {
    /** */
    private static final long serialVersionUID = 0L;

    /**
     * Constructor.
     *
     * @param msg Message.
     */
    public GridIpcEndpointBindException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     *
     * @param msg Message.
     * @param cause Cause.
     */
    public GridIpcEndpointBindException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
