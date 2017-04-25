/***************************************************************************
<<<<<<< HEAD
 * Copyright 2021 Kieker Project (http://kieker-monitoring.net)
=======
 * Copyright 2017 Kieker Project (http://kieker-monitoring.net)
>>>>>>> d690fb62e (committing fix for issue 1524 introducing a parameter names array.)
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
package kieker.common.record.flow.trace.operation;

import java.nio.BufferOverflowException;

import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.io.IValueDeserializer;
import kieker.common.record.io.IValueSerializer;

/**
 * @author Jan Waller
 *         API compatibility: Kieker 1.15.0
 * 
 * @since 1.6
 */
public class AfterOperationEvent extends AbstractOperationEvent {
	/** Descriptive definition of the serialization size of the record. */
	public static final int SIZE = TYPE_SIZE_LONG // IEventRecord.timestamp
			+ TYPE_SIZE_LONG // ITraceRecord.traceId
			+ TYPE_SIZE_INT // ITraceRecord.orderIndex
			+ TYPE_SIZE_STRING // IOperationSignature.operationSignature
			+ TYPE_SIZE_STRING; // IClassSignature.classSignature

	public static final Class<?>[] TYPES = {
		long.class, // IEventRecord.timestamp
		long.class, // ITraceRecord.traceId
		int.class, // ITraceRecord.orderIndex
		String.class, // IOperationSignature.operationSignature
		String.class, // IClassSignature.classSignature
	};
<<<<<<< HEAD

	/** property name array. */
	public static final String[] VALUE_NAMES = {
=======
	
	
	
	/** property name array. */
	private static final String[] PROPERTY_NAMES = {
>>>>>>> d690fb62e (committing fix for issue 1524 introducing a parameter names array.)
		"timestamp",
		"traceId",
		"orderIndex",
		"operationSignature",
		"classSignature",
	};
<<<<<<< HEAD

	private static final long serialVersionUID = 8888716286291758775L;

=======
	
	
>>>>>>> d690fb62e (committing fix for issue 1524 introducing a parameter names array.)
	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param timestamp
	 *            timestamp
	 * @param traceId
	 *            traceId
	 * @param orderIndex
	 *            orderIndex
	 * @param operationSignature
	 *            operationSignature
	 * @param classSignature
	 *            classSignature
	 */
	public AfterOperationEvent(final long timestamp, final long traceId, final int orderIndex, final String operationSignature, final String classSignature) {
		super(timestamp, traceId, orderIndex, operationSignature, classSignature);
	}

	/**
	 * @param deserializer
	 *            The deserializer to use
	 * @throws RecordInstantiationException
	 *             when the record could not be deserialized
	 */
<<<<<<< HEAD
	public AfterOperationEvent(final IValueDeserializer deserializer) throws RecordInstantiationException {
		super(deserializer);
=======
	protected AfterOperationEvent(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
		super(values, valueTypes);
	}

	/**
	 * This constructor converts the given buffer into a record.
	 * 
	 * @param buffer
	 *            The bytes for the record
	 * @param stringRegistry
	 *            The string registry for deserialization
	 * 
	 * @throws BufferUnderflowException
	 *             if buffer not sufficient
	 */
	public AfterOperationEvent(final ByteBuffer buffer, final IRegistry<String> stringRegistry) throws BufferUnderflowException {
		super(buffer, stringRegistry);
>>>>>>> d690fb62e (committing fix for issue 1524 introducing a parameter names array.)
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
		serializer.putLong(this.getTimestamp());
		serializer.putLong(this.getTraceId());
		serializer.putInt(this.getOrderIndex());
		serializer.putString(this.getOperationSignature());
		serializer.putString(this.getClassSignature());
	}
<<<<<<< HEAD

=======
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerStrings(final IRegistry<String> stringRegistry) {	// NOPMD (generated code)
		stringRegistry.get(this.getOperationSignature());
		stringRegistry.get(this.getClassSignature());
	}
>>>>>>> d690fb62e (committing fix for issue 1524 introducing a parameter names array.)
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?>[] getValueTypes() {
		return TYPES; // NOPMD
	}
<<<<<<< HEAD

=======
>>>>>>> d690fb62e (committing fix for issue 1524 introducing a parameter names array.)
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getValueNames() {
		return VALUE_NAMES; // NOPMD
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getValueNames() {
		return PROPERTY_NAMES; // NOPMD
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		return SIZE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}

		final AfterOperationEvent castedRecord = (AfterOperationEvent) obj;
		if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) {
			return false;
		}
		if (this.getTimestamp() != castedRecord.getTimestamp()) {
			return false;
		}
		if (this.getTraceId() != castedRecord.getTraceId()) {
			return false;
		}
		if (this.getOrderIndex() != castedRecord.getOrderIndex()) {
			return false;
		}
		if (!this.getOperationSignature().equals(castedRecord.getOperationSignature())) {
			return false;
		}
		if (!this.getClassSignature().equals(castedRecord.getClassSignature())) {
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int code = 0;
		code += ((int) this.getTimestamp());
		code += ((int) this.getTraceId());
		code += ((int) this.getOrderIndex());
		code += this.getOperationSignature().hashCode();
		code += this.getClassSignature().hashCode();

		return code;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String result = "AfterOperationEvent: ";
		result += "timestamp = ";
		result += this.getTimestamp() + ", ";

		result += "traceId = ";
		result += this.getTraceId() + ", ";

		result += "orderIndex = ";
		result += this.getOrderIndex() + ", ";

		result += "operationSignature = ";
		result += this.getOperationSignature() + ", ";

		result += "classSignature = ";
		result += this.getClassSignature() + ", ";

		return result;
	}
}
