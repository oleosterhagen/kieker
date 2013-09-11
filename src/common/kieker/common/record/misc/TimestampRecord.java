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

package kieker.common.record.misc;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.util.Bits;

/**
 * Record type which can be used to store a timestamp.
 * 
 * @author Andre van Hoorn, Jan Waller
 * 
 * @since 1.5
 */
public class TimestampRecord extends AbstractMonitoringRecord implements IMonitoringRecord.Factory {

	public static final Class<?>[] TYPES = {
		long.class, // timestamp
	};

	private static final long serialVersionUID = 4673929935358689920L;

	private final long timestamp;

	/**
	 * 
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param timestamp
	 *            The timestamp to be stored in this record.
	 */
	public TimestampRecord(final long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param values
	 *            The array containing the values for the fields of this record.
	 */
	public TimestampRecord(final Object[] values) { // NOPMD (direct store of long)
		AbstractMonitoringRecord.checkArray(values, TYPES);
		this.timestamp = (Long) values[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] toArray() {
		return new Object[] { this.getTimestamp(), };
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] toByteArray() {
		final byte[] arr = new byte[8];
		Bits.putLong(arr, 0, this.getTimestamp());
		return arr;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated This record uses the {@link kieker.common.record.IMonitoringRecord.Factory} mechanism. Hence, this method is not implemented.
	 */
	@Deprecated
	public final void initFromArray(final Object[] values) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated This record uses the {@link kieker.common.record.IMonitoringRecord.Factory} mechanism. Hence, this method is not implemented.
	 */
	@Deprecated
	public final void initFromByteArray(final byte[] values) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<?>[] getValueTypes() {
		return TYPES; // NOPMD
	}

	/**
	 * @return the timestamp
	 */
	public final long getTimestamp() {
		return this.timestamp;
	}
}
