/***************************************************************************
 * Copyright 2020 Kieker Project (http://kieker-monitoring.net)
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

package kieker.analysis.model;

import kieker.model.analysismodel.deployment.DeployedOperation;
import kieker.model.analysismodel.execution.AggregatedInvocation;
import kieker.model.analysismodel.execution.ExecutionFactory;
import kieker.model.analysismodel.execution.ExecutionModel;
import kieker.model.analysismodel.trace.OperationCall;
import kieker.model.analysismodel.util.ComposedKey;

/**
 * @author Sören Henning
 *
 * @since 1.14
 */
public class SourceAwareExecutionModelAssembler implements IExecutionModelAssembler {

	private final ExecutionFactory factory = ExecutionFactory.eINSTANCE;

	private final ExecutionModel executionModel;

	private final String dataSource;

	public SourceAwareExecutionModelAssembler(final ExecutionModel executionModel, final String dataSource) {
		this.executionModel = executionModel;
		this.dataSource = dataSource;
	}

	@Override
	public void addOperationCall(final OperationCall operationCall) {
		// Check if operationCall is an entry operation call. If so than source is null
		final DeployedOperation source = operationCall.getParent() != null ? operationCall.getParent().getOperation() : null; // NOCS (declarative)
		final DeployedOperation target = operationCall.getOperation();

		this.addExecution(source, target);
	}

	protected void addExecution(final DeployedOperation source, final DeployedOperation target) {
		final ComposedKey<DeployedOperation, DeployedOperation> key = ComposedKey.of(source, target);
		if (!this.executionModel.getAggregatedInvocations().containsKey(key)) {
			final AggregatedInvocation invocation = this.factory.createAggregatedInvocation();
			invocation.setSource(source);
			invocation.setTarget(target);
			invocation.getSources().add(this.dataSource);

			this.executionModel.getAggregatedInvocations().put(key, invocation);
		} else {
			final AggregatedInvocation element = this.executionModel.getAggregatedInvocations().get(key);
			if (!element.getSources().contains(this.dataSource)) {
				element.getSources().add(this.dataSource);
			}
		}
	}

}
