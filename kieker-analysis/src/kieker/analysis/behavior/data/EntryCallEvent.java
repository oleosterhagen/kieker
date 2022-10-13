/***************************************************************************
 * Copyright 2022 Kieker Project (http://kieker-monitoring.net)
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
package kieker.analysis.behavior.data;

/**
 * Entry call events with request data.
 *
 * @author Reiner Jung
 * @since 2.0.0
 */
public class EntryCallEvent {

	/** property declarations. */
	private final long entryTime;
	private final long exitTime;
	private String operationSignature;
	private String classSignature;
	private final String sessionId;
	private final String hostname;

	private final String[] parameters;
	private final String[] values;
	private final int requestType;

	public EntryCallEvent(final long entryTime, final long exitTime, final String operationSignature, final String classSignature,
			final String sessionId, final String hostname,
			final String[] parameters, final String[] values, final int requestType) {
		this.entryTime = entryTime;
		this.exitTime = exitTime;
		this.operationSignature = operationSignature;
		this.classSignature = classSignature;
		this.sessionId = sessionId;
		this.hostname = hostname;
		this.parameters = parameters;
		this.values = values;
		this.requestType = requestType;
	}

	public String getOperationSignature() {
		return this.operationSignature;
	}

	public void setOperationSignature(final String operationSignature) {
		this.operationSignature = operationSignature;
	}

	public String getClassSignature() {
		return this.classSignature;
	}

	public void setClassSignature(final String classSignature) {
		this.classSignature = classSignature;
	}

	public long getEntryTime() {
		return this.entryTime;
	}

	public long getExitTime() {
		return this.exitTime;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public String getHostname() {
		return this.hostname;
	}

	public String[] getParameters() {
		return this.parameters;
	}

	public String[] getValues() {
		return this.values;
	}

	public int getRequestType() {
		return this.requestType;
	}

}
