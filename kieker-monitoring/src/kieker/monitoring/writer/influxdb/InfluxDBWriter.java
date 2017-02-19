/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.monitoring.writer.influxdb;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;

import kieker.common.configuration.Configuration;
import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.writer.AbstractMonitoringWriter;

/**
 * @author Teerat Pitakrat
 *
 * @since 1.13
 */
public class InfluxDBWriter extends AbstractMonitoringWriter {

	public static final String CONFIG_PROPERTY_DB_URL = "databaseURL";
	public static final String CONFIG_PROPERTY_DB_PORT = "databasePort";
	public static final String CONFIG_PROPERTY_DB_USERNAME = "databaseUsername";
	public static final String CONFIG_PROPERTY_DB_PASSWORD = "databasePassword";
	public static final String CONFIG_PROPERTY_DB_NAME = "databaseName";

	private static final Log LOG = LogFactory.getLog(InfluxDBWriter.class);

	private final String dbURL;
	private final int dbPort;
	private final String dbUsername;
	private final String dbPassword;
	private final String dbName;
	private volatile InfluxDB influxDB;
	private volatile int influxDBMajorVersion;
	private volatile boolean isConnected;

	/**
	 * Creates a new instance of this class using the given parameters.
	 *
	 * @param configuration
	 *            The configuration for this component.
	 */
	public InfluxDBWriter(final Configuration configuration) {
		super(configuration);

		this.dbURL = this.configuration.getStringProperty(CONFIG_PROPERTY_DB_URL);
		this.dbPort = this.configuration.getIntProperty(CONFIG_PROPERTY_DB_PORT);
		this.dbUsername = this.configuration.getStringProperty(CONFIG_PROPERTY_DB_USERNAME);
		this.dbPassword = this.configuration.getStringProperty(CONFIG_PROPERTY_DB_PASSWORD);
		this.dbName = this.configuration.getStringProperty(CONFIG_PROPERTY_DB_NAME);
		this.isConnected = false;
	}

	/**
	 * Connect to InfluxDB.
	 *
	 * @throws IOException
	 *             If connection to InfluxDB fails.
	 **/
	protected final void connectToInfluxDB() throws IOException {
		LOG.info("Connecting to database using the following parameters:");
		LOG.info("URL = " + this.dbURL);
		LOG.info("Port = " + this.dbPort);
		LOG.info("Username = " + this.dbUsername);
		LOG.info("Password = " + this.dbPassword);
		this.influxDB = InfluxDBFactory.connect(this.dbURL + ":" + this.dbPort, this.dbUsername, this.dbPassword);
		if (!this.influxDB.isBatchEnabled()) {
			this.influxDB.enableBatch(2000, 500, TimeUnit.MILLISECONDS);
		}

		// Test connection
		final Pong pong;
		try {
			pong = this.influxDB.ping();
			LOG.info("Connected to InfluxDB");
		} catch (final RuntimeException e) { // NOCS NOPMD (thrown by InfluxDB library)
			throw new IOException("Cannot connect to InfluxDB with the following parameters:"
					+ "URL = " + this.dbURL
					+ "; Port = " + this.dbPort
					+ "; Username = " + this.dbUsername
					+ "; Password = " + this.dbPassword
					, e);
		}
		final String influxDBVersion = pong.getVersion();
		final String[] splitVersion = influxDBVersion.split("\\.");
		this.influxDBMajorVersion = Integer.parseInt(splitVersion[0]);
		LOG.info("Version: " + influxDBVersion);
		LOG.info("Response time: " + pong.getResponseTime());

		// Create database if it does not exist
		final List<String> dbList = this.influxDB.describeDatabases();
		if (!dbList.contains(this.dbName)) {
			LOG.info("Database " + this.dbName + " does not exist. Creating ...");
			this.influxDB.createDatabase(this.dbName);
			LOG.info("Done");
		}
		this.isConnected = true;
	}

	@Override
	public final void writeMonitoringRecord(final IMonitoringRecord monitoringRecord) {
		// Check connection to InfluxDB
		if (!this.isConnected) {
			try {
				this.connectToInfluxDB();
			} catch (final IOException e) {
				LOG.error("Cannot connect to InfluxDB. Dropping record.", e);
				return;
			}
		}

		// Extract data
		final String recordName = monitoringRecord.getClass().getSimpleName();
		final long timestamp = monitoringRecord.getLoggingTimestamp();
		// final String[] propertyNames = monitoringRecord.getPropertyNames();
		final String[] propertyNames = { "operationSignature", "sessionId", "traceId", "tin", "tout", "hostname", "eoi", "ess" }; // Mock array
		final Class<?>[] valueTypes = monitoringRecord.getValueTypes();
		final Object[] values = monitoringRecord.toArray();

		// Build data point
		final Point.Builder pointBuilder = Point.measurement(recordName);
		pointBuilder.time(timestamp, TimeUnit.NANOSECONDS);
		for (int i = 0; i < propertyNames.length; i++) {
			final String name = propertyNames[i];
			final Class<?> type = valueTypes[i];
			final Object value = values[i];
			if (type == int.class) {
				pointBuilder.addField(name, (int) value);
			} else if (type == long.class) {
				pointBuilder.addField(name, (long) value);
			} else if (type == boolean.class) {
				pointBuilder.addField(name, (boolean) value);
			} else if (type == String.class) {
				pointBuilder.addField(name, (String) value);
				pointBuilder.tag(name, (String) value);
			}
		}
		final Point point = pointBuilder.build();

		// Write to InfluxDB
		if (this.influxDBMajorVersion < 1) {
			this.influxDB.write(this.dbName, "default", point);
		} else {
			this.influxDB.write(this.dbName, "autogen", point);
		}
	}

	@Override
	public void onStarting() {
		try {
			this.connectToInfluxDB();
		} catch (final IOException e) {
			LOG.error("Cannot connect to InfluxDB.", e);
		}
	}

	@Override
	public void onTerminating() {
		LOG.info("Closing database");
		this.influxDB.close();
		LOG.info("Closing database done");
	}

}
