package org.msscf.msscf.v2_13.cflib.CFLib.DbTest.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupListener {

    @Autowired
    // @Qualifier("TestSecDb")
    private TestSecDb testSecDb;

    @Autowired
    // @Qualifier("TestAppDb")
    private TestAppDb testAppDb;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {

        System.err.println("Executing testSecDb.performTests()");
        try {
            String response = testSecDb.performTests(null);
            if (response != null) {
                System.err.println("TestSecDb.performTests() responded: " + response);
            }
            else {
                System.err.println("TestSecDb.performTests() did not return a response");
            }
        }
        catch (Throwable th) {
            System.err.println("testSecDb.performTests() threw " + th.getClass().getCanonicalName() + " - " + th.getMessage());
            th.printStackTrace(System.err);
        }

        System.err.println("Executing testAppDb.performTests()");
        try {
            String response = testAppDb.performTests(null);
            if (response != null) {
                System.err.println("TestAppDb.performTests() responded: " + response);
            }
            else {
                System.err.println("TestAppDb.performTests() did not return a response");
            }
        }
        catch (Throwable th) {
            System.err.println("testAppDb.performTests() threw " + th.getClass().getCanonicalName() + " - " + th.getMessage());
            th.printStackTrace(System.err);
        }

        System.err.println("DbTest StartupListener tests complete.");
    }
}