/***************************************************************************
 * Copyright 2013 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/

package livedemo.filter;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import kieker.analysis.IProjectContext;
import kieker.analysis.display.XYPlot;
import kieker.analysis.display.annotation.Display;
import kieker.analysis.plugin.annotation.InputPort;
import kieker.analysis.plugin.annotation.Plugin;
import kieker.analysis.plugin.annotation.Property;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.common.configuration.Configuration;
import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.common.record.jvm.ThreadsStatusRecord;

/**
 * @author Nils Christian Ehmke
 * 
 * @since 1.10
 */
@Plugin(configuration = {
	@Property(name = ThreadsStatusDisplayFilter.CONFIG_PROPERTY_NAME_NUMBER_OF_ENTRIES,
			defaultValue = ThreadsStatusDisplayFilter.CONFIG_PROPERTY_VALUE_NUMBER_OF_ENTRIES) })
public class ThreadsStatusDisplayFilter extends AbstractFilterPlugin {

	public static final String INPUT_PORT_NAME_RECORDS = "inputRecordEvents";
	public static final String INPUT_PORT_NAME_TIMESTAMPS = "inputTimeEvents";

	public static final String CONFIG_PROPERTY_NAME_NUMBER_OF_ENTRIES = "numberOfEntries";
	public static final String CONFIG_PROPERTY_VALUE_NUMBER_OF_ENTRIES = "100";

	public static final String CONFIG_PROPERTY_VALUE_RESPONSETIME_TIMEUNIT = "NANOSECONDS";

	private static final Log LOG = LogFactory.getLog(ThreadsStatusDisplayFilter.class);

	private final XYPlot plot;
	private final int numberOfEntries;
	private final TimeUnit timeunit;
	private final List<ThreadsStatusRecord> records;

	public ThreadsStatusDisplayFilter(final Configuration configuration, final IProjectContext projectContext) {
		super(configuration, projectContext);
		this.numberOfEntries = configuration.getIntProperty(ThreadsStatusDisplayFilter.CONFIG_PROPERTY_NAME_NUMBER_OF_ENTRIES);
		final String recordTimeunitProperty = projectContext.getProperty(IProjectContext.CONFIG_PROPERTY_NAME_RECORDS_TIME_UNIT);
		TimeUnit recordTimeunit;
		try {
			recordTimeunit = TimeUnit.valueOf(recordTimeunitProperty);
		} catch (final IllegalArgumentException ex) { // already caught in AnalysisController, should never happen
			ThreadsStatusDisplayFilter.LOG.warn(recordTimeunitProperty + " is no valid TimeUnit! Using NANOSECONDS instead.");
			recordTimeunit = TimeUnit.NANOSECONDS;
		}
		this.timeunit = recordTimeunit;
		this.plot = new XYPlot(this.numberOfEntries);
		this.records = new CopyOnWriteArrayList<ThreadsStatusRecord>();
	}

	@InputPort(name = ThreadsStatusDisplayFilter.INPUT_PORT_NAME_RECORDS, eventTypes = { ThreadsStatusRecord.class })
	public synchronized void inputRecords(final ThreadsStatusRecord record) {
		this.records.add(record);
	}

	@InputPort(name = ThreadsStatusDisplayFilter.INPUT_PORT_NAME_TIMESTAMPS, eventTypes = { Long.class })
	public synchronized void inputTimeEvents(final Long timestamp) {
		// Calculate the minutes and seconds of the logging timestamp of the record
		final Date date = new Date(TimeUnit.MILLISECONDS.convert(timestamp, this.timeunit));
		final String minutesAndSeconds = date.toString().substring(14, 19);

		for (final ThreadsStatusRecord record : this.records) {
			this.plot.setEntry("Threads", minutesAndSeconds, record.getThreadCount());
			this.plot.setEntry("Daemon Threads", minutesAndSeconds, record.getDaemonThreadCount());
			this.plot.setEntry("Peak Threads", minutesAndSeconds, record.getPeakThreadCount());
			this.plot.setEntry("Total Started Threads", minutesAndSeconds, record.getTotalStartedThreadCount());
		}

		this.records.clear();
	}

	@Display(name = "XYPlot")
	public XYPlot getPlot() {
		return this.plot;
	}

	@Override
	public Configuration getCurrentConfiguration() {
		final Configuration configuration = new Configuration();
		configuration.setProperty(ThreadsStatusDisplayFilter.CONFIG_PROPERTY_NAME_NUMBER_OF_ENTRIES, String.valueOf(this.numberOfEntries));
		return configuration;
	}

}
