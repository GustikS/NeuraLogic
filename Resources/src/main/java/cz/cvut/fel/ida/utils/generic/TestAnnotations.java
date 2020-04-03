package cz.cvut.fel.ida.utils.generic;

import org.jetbrains.annotations.TestOnly;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
    @ExtendWith({TestLogging.class})
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
    @ExtendWith({TestLogging.class})
    @Tag("AdHoc")
    public @interface AdHoc {
    }


    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @Tag("SlowBenchmark")
    @ExtendWith({TestLogging.PreciseBenchmarking.class})
    @interface SlowBenchmark {
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Test
    @TestOnly
    @ExtendWith({TestLogging.class})
    @Tag("FastBenchmark")
    @interface FastBenchmark {
    }


//---------------------------------------------------------------


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
}