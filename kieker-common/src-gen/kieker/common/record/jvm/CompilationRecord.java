/***************************************************************************
 * Copyright 2023 Kieker Project (http://kieker-monitoring.net)
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
package kieker.common.record.jvm;

import java.nio.BufferOverflowException;

import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.io.IValueDeserializer;
import kieker.common.record.io.IValueSerializer;

/**
 * @author Nils Christian Ehmke
 *         API compatibility: Kieker 1.15.0
 *
 * @since 1.10
 */
public class CompilationRecord extends AbstractJVMRecord {
	/** Descriptive definition of the serialization size of the record. */
	public static final int SIZE = TYPE_SIZE_LONG // AbstractJVMRecord.timestamp
			+ TYPE_SIZE_STRING // AbstractJVMRecord.hostname
			+ TYPE_SIZE_STRING // AbstractJVMRecord.vmName
			+ TYPE_SIZE_STRING // CompilationRecord.jitCompilerName
			+ TYPE_SIZE_LONG; // CompilationRecord.totalCompilationTimeMS

	public static final Class<?>[] TYPES = {
		long.class, // AbstractJVMRecord.timestamp
		String.class, // AbstractJVMRecord.hostname
		String.class, // AbstractJVMRecord.vmName
		String.class, // CompilationRecord.jitCompilerName
		long.class, // CompilationRecord.totalCompilationTimeMS
	};

	/** property name array. */
	public static final String[] VALUE_NAMES = {
		"timestamp",
		"hostname",
		"vmName",
		"jitCompilerName",
		"totalCompilationTimeMS",
	};

	/** default constants. */
	public static final String JIT_COMPILER_NAME = "";
	private static final long serialVersionUID = 3634137431488075031L;

	/** property declarations. */
	private final String jitCompilerName;
	private final long totalCompilationTimeMS;

	/**
	 * Creates a new instance of this class using the given parameters.
	 *
	 * @param timestamp
	 *            timestamp
	 * @param hostname
	 *            hostname
	 * @param vmName
	 *            vmName
	 * @param jitCompilerName
	 *            jitCompilerName
	 * @param totalCompilationTimeMS
	 *            totalCompilationTimeMS
	 */
	public CompilationRecord(final long timestamp, final String hostname, final String vmName, final String jitCompilerName, final long totalCompilationTimeMS) {
		super(timestamp, hostname, vmName);
		this.jitCompilerName = jitCompilerName == null ? "" : jitCompilerName;
		this.totalCompilationTimeMS = totalCompilationTimeMS;
	}

	/**
	 * @param deserializer
	 *            The deserializer to use
	 * @throws RecordInstantiationException
	 *             when the record could not be deserialized
	 */
	public CompilationRecord(final IValueDeserializer deserializer) throws RecordInstantiationException {
		super(deserializer);
		this.jitCompilerName = deserializer.getString();
		this.totalCompilationTimeMS = deserializer.getLong();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
		serializer.putLong(this.getTimestamp());
		serializer.putString(this.getHostname());
		serializer.putString(this.getVmName());
		serializer.putString(this.getJitCompilerName());
		serializer.putLong(this.getTotalCompilationTimeMS());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?>[] getValueTypes() {
		return TYPES; // NOPMD
	}

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

		final CompilationRecord castedRecord = (CompilationRecord) obj;
		if ((this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) || (this.getTimestamp() != castedRecord.getTimestamp()) || !this.getHostname().equals(castedRecord.getHostname()) || !this.getVmName().equals(castedRecord.getVmName())) {
			return false;
		}
		if (!this.getJitCompilerName().equals(castedRecord.getJitCompilerName())) {
			return false;
		}
		if (this.getTotalCompilationTimeMS() != castedRecord.getTotalCompilationTimeMS()) {
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
		code += this.getHostname().hashCode();
		code += this.getVmName().hashCode();
		code += this.getJitCompilerName().hashCode();
		code += ((int) this.getTotalCompilationTimeMS());

		return code;
	}

	public final String getJitCompilerName() {
		return this.jitCompilerName;
	}

	public final long getTotalCompilationTimeMS() {
		return this.totalCompilationTimeMS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String result = "CompilationRecord: ";
		result += "timestamp = ";
		result += this.getTimestamp() + ", ";

		result += "hostname = ";
		result += this.getHostname() + ", ";

		result += "vmName = ";
		result += this.getVmName() + ", ";

		result += "jitCompilerName = ";
		result += this.getJitCompilerName() + ", ";

		result += "totalCompilationTimeMS = ";
		result += this.getTotalCompilationTimeMS() + ", ";

		return result;
	}
}
