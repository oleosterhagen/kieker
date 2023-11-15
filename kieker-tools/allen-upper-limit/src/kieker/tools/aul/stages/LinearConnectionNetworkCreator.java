/***************************************************************************
 * Copyright (C) 2023 Kieker Project (https://kieker-monitoring.net)
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
package kieker.tools.aul.stages;

import java.util.Map;

import org.mosim.refactorlizar.architecture.evaluation.graphs.Node;

import com.google.common.graph.MutableGraph;

import kieker.model.analysismodel.deployment.DeployedComponent;

/**
 * @author Reiner Jung
 * @since 2.0.0
 */
public class LinearConnectionNetworkCreator implements INetworkCreator {

	@Override
	public void createEdges(final MutableGraph<Node<DeployedComponent>> graph,
			final Map<Integer, Node<DeployedComponent>> nodes, final Integer numOfNodes) {
		for (int i = 0; i < (numOfNodes - 1); i++) {
			final Node<DeployedComponent> source = nodes.get(i);
			final Node<DeployedComponent> target = nodes.get(i + 1);
			graph.putEdge(source, target);
		}
	}

}
