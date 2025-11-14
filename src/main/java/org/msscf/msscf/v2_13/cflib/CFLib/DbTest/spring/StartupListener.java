/*
 *	MSS Code Factory CFLib 2.13 Database Testing
 *
 *	Copyright (C) 2016-2025 Mark Stephen Sobkow (mailto:mark.sobkow@gmail.com)
 *	
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *	
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see &lt;https://www.gnu.org/licenses/&gt;.
 *	
 *	If you wish to modify and use this code without publishing your changes,
 *	or integrate it with proprietary code, please contact Mark Stephen Sobkow
 *	for a commercial license at mark.sobkow@gmail.com
 */
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
