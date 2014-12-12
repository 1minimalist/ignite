/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.grid.kernal.processors.cache;

import org.apache.ignite.configuration.*;
import org.apache.ignite.spi.discovery.tcp.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.*;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.*;
import org.gridgain.grid.cache.*;
import org.gridgain.grid.cache.query.*;
import org.gridgain.testframework.junits.common.*;

import java.util.*;

import static org.gridgain.grid.cache.GridCacheMode.*;
import static org.gridgain.grid.cache.GridCacheWriteSynchronizationMode.*;

/**
 * Tests for cache query metrics.
 */
public class GridCacheQueryMetricsSelfTest extends GridCommonAbstractTest {
    /** */
    private static final int GRID_CNT = 2;

    /** */
    private static final GridCacheMode CACHE_MODE = REPLICATED;

    /** */
    private static TcpDiscoveryIpFinder ipFinder = new TcpDiscoveryVmIpFinder(true);

    /** {@inheritDoc} */
    @Override protected void beforeTest() throws Exception {
        startGridsMultiThreaded(GRID_CNT);
    }

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(gridName);

        TcpDiscoverySpi disco = new TcpDiscoverySpi();

        disco.setIpFinder(ipFinder);

        cfg.setDiscoverySpi(disco);

        GridCacheConfiguration cacheCfg = defaultCacheConfiguration();

        cacheCfg.setCacheMode(CACHE_MODE);
        cacheCfg.setWriteSynchronizationMode(FULL_SYNC);

        GridCacheQueryConfiguration qcfg = new GridCacheQueryConfiguration();

        qcfg.setIndexPrimitiveKey(true);

        cacheCfg.setQueryConfiguration(qcfg);

        cfg.setCacheConfiguration(cacheCfg);

        return cfg;
    }

    /**
     * JUnit.
     *
     * @throws Exception In case of error.
     */
    public void testAccumulativeMetrics() throws Exception {
        GridCache<String, Integer> cache = cache(0);

        GridCacheQuery <Map.Entry<String, Integer>> qry = cache.queries().createSqlQuery(Integer.class, "_val >= 0")
            .projection(grid(0));

        // Execute query.
        qry.execute().get();

        GridCacheQueryMetrics m = cache.queries().metrics();

        assert m != null;

        info("Metrics: " + m);

        assertEquals(1, m.executions());
        assertEquals(0, m.fails());
        assertTrue(m.averageTime() >= 0);
        assertTrue(m.maximumTime() >= 0);
        assertTrue(m.minimumTime() >= 0);

        // Execute again with the same parameters.
        qry.execute().get();

        m = cache.queries().metrics();

        assert m != null;

        info("Metrics: " + m);

        assertEquals(2, m.executions());
        assertEquals(0, m.fails());
        assertTrue(m.averageTime() >= 0);
        assertTrue(m.maximumTime() >= 0);
        assertTrue(m.minimumTime() >= 0);
    }

    /**
     * JUnit.
     *
     * @throws Exception In case of error.
     */
    public void testSingleQueryMetrics() throws Exception {
        GridCache<String, Integer> cache = cache(0);

        GridCacheQuery<Map.Entry<String, Integer>> qry = cache.queries().createSqlQuery(Integer.class, "_val >= 0")
            .projection(grid(0));

        // Execute.
        qry.execute().get();

        GridCacheQueryMetrics m = qry.metrics();

        info("Metrics: " + m);

        assertEquals(1, m.executions());
        assertEquals(0, m.fails());
        assertTrue(m.averageTime() >= 0);
        assertTrue(m.maximumTime() >= 0);
        assertTrue(m.minimumTime() >= 0);

        // Execute.
        qry.execute().get();

        m = qry.metrics();

        info("Metrics: " + m);

        assertEquals(2, m.executions());
        assertEquals(0, m.fails());
        assertTrue(m.averageTime() >= 0);
        assertTrue(m.maximumTime() >= 0);
        assertTrue(m.minimumTime() >= 0);
    }
}
