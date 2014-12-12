/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.compute;

import org.apache.ignite.*;
import org.apache.ignite.cluster.*;
import org.apache.ignite.resources.*;
import org.gridgain.grid.util.typedef.*;
import org.gridgain.grid.util.typedef.internal.*;

import java.util.*;

/**
 * This class defines simplified adapter for {@link ComputeTask}. This adapter can be used
 * when jobs can be randomly assigned to available grid nodes. This adapter is sufficient
 * in most homogeneous environments where all nodes are equally suitable for executing grid
 * job. See {@link #split(int, Object)} method for more details.
 * <p>
 * Below is a coding example of how you would use {@code GridComputeTaskSplitAdapter}:
 * <pre name="code" class="java">
 * public class MyFooBarTask extends GridComputeTaskSplitAdapter&lt;Object, String&gt; {
 *     &#64;Override
 *     protected Collection&lt;? extends ComputeJob&gt; split(int gridSize, Object arg) throws IgniteCheckedException {
 *         List&lt;MyFooBarJob&gt; jobs = new ArrayList&lt;MyFooBarJob&gt;(gridSize);
 *
 *         for (int i = 0; i &lt; gridSize; i++) {
 *             jobs.add(new MyFooBarJob(arg));
 *         }
 *
 *         // Node assignment via load balancer
 *         // happens automatically.
 *         return jobs;
 *     }
 *
 *     // Aggregate results into one compound result.
 *     public String reduce(List&lt;GridComputeJobResult&gt; results) throws IgniteCheckedException {
 *         // For the purpose of this example we simply
 *         // concatenate string representation of every
 *         // job result
 *         StringBuilder buf = new StringBuilder();
 *
 *         for (GridComputeJobResult res : results) {
 *             // Append string representation of result
 *             // returned by every job.
 *             buf.append(res.getData().string());
 *         }
 *
 *         return buf.string();
 *     }
 * }
 * </pre>
 * @param <T> Type of the task execution argument.
 * @param <R> Type of the task result returning from {@link ComputeTask#reduce(List)} method.
 */
public abstract class ComputeTaskSplitAdapter<T, R> extends ComputeTaskAdapter<T, R> {
    /** */
    private static final long serialVersionUID = 0L;

    /** Load balancer. */
    @IgniteLoadBalancerResource
    private ComputeLoadBalancer balancer;

    /**
     * This is a simplified version of {@link ComputeTask#map(List, Object)} method.
     * <p>
     * This method basically takes given argument and splits it into a collection
     * of {@link ComputeJob} using provided grid size as indication of how many node are
     * available. These jobs will be randomly mapped to available grid nodes. Note that
     * if number of jobs is greater than number of grid nodes (i.e, grid size), the grid
     * nodes will be reused and some jobs will end up on the same grid nodes.
     *
     * @param gridSize Number of available grid nodes. Note that returned number of
     *      jobs can be less, equal or greater than this grid size.
     * @param arg Task execution argument. Can be {@code null}.
     * @return Collection of grid jobs. These jobs will be randomly mapped to
     *      available grid nodes. Note that if number of jobs is greater than number of
     *      grid nodes (i.e, grid size), the grid nodes will be reused and some jobs
     *      will end up on the same grid nodes.
     * @throws IgniteCheckedException Thrown in case of any errors.
     *
     * @see ComputeTask#map(List, Object)
     */
    protected abstract Collection<? extends ComputeJob> split(int gridSize, T arg) throws IgniteCheckedException;

    /** {@inheritDoc} */
    @Override public final Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> subgrid, T arg)
        throws IgniteCheckedException {
        assert subgrid != null;
        assert !subgrid.isEmpty();

        Collection<? extends ComputeJob> jobs = split(subgrid.size(), arg);

        if (F.isEmpty(jobs))
            throw new IgniteCheckedException("Split returned no jobs.");

        Map<ComputeJob, ClusterNode> map = U.newHashMap(jobs.size());

        for (ComputeJob job : jobs) {
            ClusterNode old = map.put(job, balancer.getBalancedNode(job, null));

            if (old != null)
                throw new IgniteCheckedException("Failed to map task (same job instance is being mapped more than once) " +
                    "[job=" + job + ", task=" + this + ']');
        }

        return map;
    }
}
