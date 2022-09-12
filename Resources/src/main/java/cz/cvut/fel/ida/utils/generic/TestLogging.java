package cz.cvut.fel.ida.utils.generic;

import cz.cvut.fel.ida.logging.Logging;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.logging.Logger;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class TestLogging implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, ExtensionContext.Store.CloseableResource {
    private static final Logger LOG = Logger.getLogger(Benchmarking.class.getName());

    public static Double baselinePerformanceCoeff;

    Logging logging;

    @Override
    public void beforeEach(ExtensionContext context) {
//        LOG.info("beforeEach is called");
        context.getRoot().getStore(GLOBAL).put("any unique name", this);
        try {
            logging = Logging.initTestLogging("log" + sanitize(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        logging.finish();
        logging = null;
    }

    @Override
    public void close() {
        // Your "after all tests" logic goes here
//        logging.finish();
    }

    private String sanitize(ExtensionContext context) {
        return context.getUniqueId().replaceAll("engine:junit-jupiter", "")
                .replaceAll("\\[", "").replaceAll("\\]", "")
                .replaceAll("/", "_").replaceAll(":", "")
                .replaceAll("class", "").replaceAll("cz.cvut.fel.ida", "")
                .replaceAll("test-template", "").replaceAll("java.lang", "").replaceAll("method","");
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.warning("beforeAll is called");
    }

    public static class PreciseBenchmarking extends TestLogging {
        private static boolean started = false;

        @Override
        public void beforeEach(ExtensionContext context){
            // Your "before all tests" startup logic goes here
            // The following line registers a callback hook when the root test context is shut down
            super.beforeEach(context);
            if (!started){
                baselinePerformanceCoeff = Benchmarking.getBaselinePerformanceCoeff();
                LOG.warning("===========> Speed coefficient for this machine: " + baselinePerformanceCoeff);
            }
            started = true;
        }
    }
}