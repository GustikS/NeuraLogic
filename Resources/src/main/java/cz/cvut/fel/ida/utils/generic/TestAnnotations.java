package cz.cvut.fel.ida.utils.generic;

import org.jetbrains.annotations.TestOnly;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.openjdk.jmh.annotations.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public @interface TestAnnotations {

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @Tag("Fast")
    public @interface Fast {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @ParameterizedTest
    @TestOnly
    @Tag("Fast")
    public @interface Parameterized {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @Tag("Slow")
    public @interface Slow {
    }



    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @Tag("Unit")
    @interface Unit {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @Tag("Integration")
    @interface Integration {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @Tag("Functional")
    @interface Functional {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @Tag("Usecase")
    @interface Usecase {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(time = 5)
    @Threads(4)
    @Tag("SlowBenchmark")
    @interface SlowBenchmark {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    @Warmup(time = 3)   //3 seconds
//    @Fork(2)
//    @Threads(4)
    @Tag("FastBenchmark")
    @interface FastBenchmark {
    }

}