package utils;

import ida.utils.tuples.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    public static long getAppRemainingMemory() {
        long allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        long presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
        return presumableFreeMemory;
    }

    public static String identifyFileTypeUsingFilesProbeContentType(final String fileName) {
        String fileType = "Undetermined";
        final File file = new File(fileName);
        try {
            fileType = Files.probeContentType(file.toPath());
        } catch (IOException ioException) {
            LOG.severe("ERROR: Unable to determine file type for " + fileName + " due to exception " + ioException);
        }
        return fileType;
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
                return aIterator.hasNext() && bIterator.hasNext();
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
            for (int i=0; i < batchSize && base.tryAdvance(batch::add); i++)
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
}
