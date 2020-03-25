package cz.cvut.fel.ida.utils.generic;

import org.jetbrains.annotations.TestOnly;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    @ExtendWith({TestLogging.class})
    @Tag("Fast")
    public @interface Fast {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @ExtendWith({TestLogging.class})
    @Tag("Medium")
    public @interface Medium {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @ParameterizedTest
    @TestOnly
    public @interface Parameterized {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @ExtendWith({TestLogging.class})
    @Tag("Slow")
    public @interface Slow {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @ExtendWith({TestLogging.class})
    @Tag("Interactive")
    public @interface Interactive {
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
    @TestOnly
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(time = 5)
    @Threads(4)
    @Tag("SlowBenchmark")
    @ExtendWith({TestLogging.class})
    @interface SlowBenchmark {
    }

//    @Target({TYPE, METHOD, ANNOTATION_TYPE})
//    @Retention(RUNTIME)
//    @TestOnly
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 2)   //3 seconds
    @Measurement(iterations = 2)
    @Fork(2)
    @Threads(4)
//    @Tag("FastBenchmark")
    @interface FastBenchmark {
    }
}