package kieker.tpmon.writer;

import java.util.Vector;
import kieker.common.record.IMonitoringRecord;
import kieker.tpmon.writer.util.async.AbstractWorkerThread;

/*
 * 
 * ==================LICENCE=========================
 * Copyright 2006-2009 Kieker Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================================
 */

/**
 * @author Andre van Hoorn
 */
public interface IMonitoringLogWriter {

    /** Writes the passed monitoring record.
     *
     *@return true iff the operation was successful.
     */
    public boolean writeMonitoringRecord(IMonitoringRecord monitoringRecord);

    /**
     * Initialize instance from passed initialization string which is typically
     * a list of separated parameter/values pairs.
     * The implementing class AbstractMonitoringLogWriter includes convenient
     * methods to extract configuration values from an initString.
     *
     * @param initString the initialization string
     * @return true iff the initialiation was successful
     */
    public boolean init(String initString);

    /**
     * Returns a vector of workers, or null if none.
     */
    public Vector<AbstractWorkerThread> getWorkers();

    // TODO: check if required
    /**
     * Sets the writer's debug level.
     *
     * @param debug true iff debug shall be enabled
     */
    public void setDebug(boolean debug);

    // TODO: check if required
    /**
     * Returns the writer's debug level.
     *
     * @return true iff debug enabled
     */
    public boolean isDebug();

    /**
     * Returns a human-readable information string about the writer's
     * configuration and state.
     *
     * @return the information string.
     */
    public String getInfoString();
}
