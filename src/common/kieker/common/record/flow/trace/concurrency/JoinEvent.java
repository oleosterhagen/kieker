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

package kieker.common.record.flow.trace.concurrency;

import kieker.common.record.flow.trace.AbstractTraceEvent;
import kieker.common.util.Bits;

/**
 * @author Jan Waller
 * 
 * @since 1.8
 */
public class JoinEvent extends AbstractTraceEvent {

	public static final Class<?>[] TYPES = {
		long.class, // Event.timestamp
		long.class, // TraceEvent.traceId
		int.class, // TraceEvent.orderIndex
		long.class, // joined traceId
	};

	private static final long serialVersionUID = 8348010228570530470L;

	private final long joinedTraceId;

	/**
	 * This constructor uses the given parameters to initialize the fields of this record.
	 * 
	 * @param timestamp
	 *            The time stamp.
	 * @param traceId
	 *            The trace ID.
	 * @param orderIndex
	 *            The order index.
	 * @param joinedTraceId
	 *            The joined trace ID.
	 */
	public JoinEvent(final long timestamp, final long traceId, final int orderIndex, final long joinedTraceId) {
		super(timestamp, traceId, orderIndex);
		this.joinedTraceId = joinedTraceId;
	}

	/**
	 * This constructor uses the given array to initialize the fields of this record.
	 * 
	 * @param values
	 *            The values for the record.
	 */
	public JoinEvent(final Object[] values) { // NOPMD (values stored directly)
		super(values, TYPES); // values[0..2]
		this.joinedTraceId = (Long) values[3];
	}

	/**
	 * This constructor uses the given array to initialize the fields of this record.
	 * 
	 * @param values
	 *            The values for the record.
	 * @param valueTypes
	 *            The types of the elements in the first array.
	 */
	public JoinEvent(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
		super(values, valueTypes); // values[0..2]
		this.joinedTraceId = (Long) values[3];
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] toArray() {
		return new Object[] { this.getTimestamp(), this.getTraceId(), this.getOrderIndex(), this.getJoinedTraceId(), };
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] toByteArray() {
		final byte[] arr = new byte[8 + 8 + 4 + 8];
		Bits.putLong(arr, 0, this.getTimestamp());
		Bits.putLong(arr, 8, this.getTraceId());
		Bits.putInt(arr, 8 + 8, this.getOrderIndex());
		Bits.putLong(arr, 8 + 8 + 4, this.getJoinedTraceId());
		return arr;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<?>[] getValueTypes() {
		return TYPES; // NOPMD
	}

	public final long getJoinedTraceId() {
		return this.joinedTraceId;
	}
}
