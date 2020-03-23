package cz.cvut.fel.ida.utils.generic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by gusta on 26.3.17.
 */
public class Utilities {

    private static final Logger LOG = Logger.getLogger(Utilities.class.getName());

    public static int mb = 1024 * 1024;
    public static double gcPercentLimit = 0.2;
    public static long remainingMemoryLimit = 500;

    private static long tic = System.currentTimeMillis();
    private static long lastGarbageCollectionTime = 0;

    public static long allocatedMemory = 0;

    public static void myExit(Logger log, String msg) throws Exception {
        log.severe(msg);
        throw new Exception(msg);
    }

    public static String[] splitArgs(String args) {
        return args.split("(?=\")|(?<=\")\\s| ");
    }

    public static String getResourcePath(String filename) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(filename);
        return resource.getPath();
    }

    public static String readResourceFile(String path) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while (true) {
                if (!((length = inputStream.read(buffer)) != -1)) break;
                result.write(buffer, 0, length);
            }
        } catch (IOException e) {
            LOG.severe(e.getMessage());
            return null;
        }
        try {
            return result.toString(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOG.severe(e.getMessage());
            return null;
        }
    }

    public static String sanitize(String name) {
        String sane = name.replaceAll("[:.;'/\\\\]", "_");
        return sane;
    }

    public static void logMemory() {
        long appRemainingMemory = Utilities.getAppRemainingMemory();
        if (appRemainingMemory < remainingMemoryLimit)
            LOG.warning("Possible performance decrease due to GC and sweeping - please increase memory via -Xmx ! Remaining Java application memory only : " + appRemainingMemory + "mb");
        else
            LOG.finer("Remaining Java application memory : " + appRemainingMemory + "mb");
        logGCStats();
    }

    public static long getAppRemainingMemory() {
        allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        long presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
        return presumableFreeMemory / mb;
    }

    public static void logGCStats() {
        long totalGarbageCollections = 0;
        long garbageCollectionTime = 0;

        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            totalGarbageCollections += gc.getCollectionCount();
            garbageCollectionTime += gc.getCollectionTime();
        }
        long now = System.currentTimeMillis();
        double gcDelta = (garbageCollectionTime - lastGarbageCollectionTime);
        double gcPercent = gcDelta / (now - tic);
        if (gcPercent > gcPercentLimit) {
            LOG.warning("Garbage collection takes more than " + gcPercentLimit * 100 + "% of calculation time!");
        }
        LOG.finer(totalGarbageCollections + " garbage colletions with total time: " + garbageCollectionTime / 1000 + "s, made " + gcPercent * 100 + "% of time spent in GC since the last time.");
        tic = now;
        lastGarbageCollectionTime = garbageCollectionTime;
    }

    public static <A, B> List<Pair<A, B>> zipLists(ArrayList<A> as, ArrayList<B> bs) {
        return IntStream.range(0, Math.min(as.size(), bs.size()))
                .mapToObj(i -> new Pair<>(as.get(i), bs.get(i)))
                .collect(Collectors.toList());
    }

    public static <A, B> List<Pair<A, B>> zipLists(List<A> as, List<B> bs) {
        Iterator<A> it1 = as.iterator();
        Iterator<B> it2 = bs.iterator();
        List<Pair<A, B>> result = new LinkedList<>();
        while (it1.hasNext() && it2.hasNext()) {
            result.add(new Pair<A, B>(it1.next(), it2.next()));
        }
        return result;
    }

    public static <A, B, C> Stream<C> zipStreams(Stream<? extends A> a,
                                                 Stream<? extends B> b,
                                                 BiFunction<? super A, ? super B, ? extends C> zipper) {
        Objects.requireNonNull(zipper);
        Spliterator<? extends A> aSpliterator = Objects.requireNonNull(a).spliterator();
        Spliterator<? extends B> bSpliterator = Objects.requireNonNull(b).spliterator();

        // Zipping looses DISTINCT and SORTED characteristics
        int characteristics = aSpliterator.characteristics() & bSpliterator.characteristics() &
                ~(Spliterator.DISTINCT | Spliterator.SORTED);

        long zipSize = ((characteristics & Spliterator.SIZED) != 0)
                ? Math.min(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
                : -1;

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new Iterator<C>() {
            @Override
            public boolean hasNext() {
                boolean hasNextA = aIterator.hasNext();
                boolean hasNextB = bIterator.hasNext();
                if (hasNextA && hasNextB) {
                    return true;
                } else {
                    if (hasNextA || hasNextB) {
                        LOG.severe("Streams to be zipped have different sizes! Possibly mismatch of examples and labels?");
                        throw new IllegalStateException("Stream size mismatch");
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public C next() {
                return zipper.apply(aIterator.next(), bIterator.next());
            }
        };

        Spliterator<C> split = Spliterators.spliterator(cIterator, zipSize, characteristics);
        return (a.isParallel() || b.isParallel())
                ? StreamSupport.stream(split, true)
                : StreamSupport.stream(split, false);
    }

    public static class BatchSpliterator<E> implements Spliterator<List<E>> {

        private final Spliterator<E> base;
        private final int batchSize;

        public BatchSpliterator(Spliterator<E> base, int batchSize) {
            this.base = base;
            this.batchSize = batchSize;
        }

        @Override
        public boolean tryAdvance(Consumer<? super List<E>> action) {
            final List<E> batch = new ArrayList<>(batchSize);
            for (int i = 0; i < batchSize && base.tryAdvance(batch::add); i++)
                ;
            if (batch.isEmpty())
                return false;
            action.accept(batch);
            return true;
        }

        @Override
        public Spliterator<List<E>> trySplit() {
            if (base.estimateSize() <= batchSize)
                return null;
            final Spliterator<E> splitBase = this.base.trySplit();
            return splitBase == null ? null
                    : new BatchSpliterator<>(splitBase, batchSize);
        }

        @Override
        public long estimateSize() {
            final double baseSize = base.estimateSize();
            return baseSize == 0 ? 0
                    : (long) Math.ceil(baseSize / (double) batchSize);
        }

        @Override
        public int characteristics() {
            return base.characteristics();
        }

    }

    /**
     * Collect a Stream<T> to List<T> with properly terminating it (calling all the closing callbacks)
     *
     * @param stream
     * @param <T>
     * @return
     */
    public static <T> List<T> terminateSampleStream(Stream<T> stream) {
        Timing timing = new Timing();
        timing.tic();
        LOG.fine("------------------------------------------ PROCESSING SAMPLES (= terminating stream) -------------------------------------------------------");
        List<T> list = stream.collect(Collectors.toList());
        stream.close(); //THE IMPORTANT PART
        timing.toc();
        LOG.fine("------------------------------------------ SAMPLES PROCESSED into list of " + list.get(0).getClass().getSimpleName() + " (in " + timing.timeTaken + ")-------------------------------------------------------");
        logMemory();
        return list;
    }
}
