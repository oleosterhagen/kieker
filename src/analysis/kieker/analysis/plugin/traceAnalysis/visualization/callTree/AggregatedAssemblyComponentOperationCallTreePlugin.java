package kieker.analysis.plugin.traceAnalysis.visualization.callTree;

/*
 * ==================LICENCE=========================
 * Copyright 2006-2010 Kieker Project
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
import java.io.File;
import kieker.analysis.datamodel.AssemblyComponent;
import kieker.analysis.datamodel.util.AssemblyComponentOperationPair;
import kieker.analysis.datamodel.Operation;
import kieker.analysis.datamodel.SynchronousCallMessage;
import kieker.analysis.datamodel.repository.AbstractSystemSubRepository;
import kieker.analysis.datamodel.repository.AssemblyComponentOperationPairFactory;
import kieker.analysis.datamodel.repository.SystemModelRepository;

/**
 *
 * @author Andre van Hoorn
 */
public class AggregatedAssemblyComponentOperationCallTreePlugin
        extends AggregatedCallTreePlugin<AssemblyComponentOperationPair> {

    public AggregatedAssemblyComponentOperationCallTreePlugin(
            final String name,
            final AssemblyComponentOperationPairFactory assemblyComponentOperationPairFactory,
            final SystemModelRepository systemEntityFactory,
            final File dotOutputFile,
            final boolean includeWeights,
            final boolean shortLabels) {
        super(name, systemEntityFactory,
                new AggregatedAssemblyComponentOperationCallTreeNode(
                AbstractSystemSubRepository.ROOT_ELEMENT_ID, systemEntityFactory,
                assemblyComponentOperationPairFactory, assemblyComponentOperationPairFactory.rootPair, true), // root node
                dotOutputFile,
                includeWeights,
                shortLabels);
    }
}

class AggregatedAssemblyComponentOperationCallTreeNode extends AbstractAggregatedCallTreeNode<AssemblyComponentOperationPair> {

    private final AssemblyComponentOperationPairFactory pairFactory;

    public AggregatedAssemblyComponentOperationCallTreeNode(final int id,
            final SystemModelRepository systemEntityFactory,
            final AssemblyComponentOperationPairFactory pairFactory,
            final AssemblyComponentOperationPair entity,
            final boolean rootNode) {
        super(id, systemEntityFactory, entity, rootNode);
        this.pairFactory = pairFactory;
    }

    @Override
    public AbstractCallTreeNode<AssemblyComponentOperationPair> newCall(SynchronousCallMessage callMsg) {
        AssemblyComponent AssemblyComponent =
                callMsg.getReceivingExecution().getAllocationComponent().getAssemblyComponent();
        Operation op = callMsg.getReceivingExecution().getOperation();
        AssemblyComponentOperationPair destination = // will never be null!
                this.pairFactory.getPairInstanceByPair(AssemblyComponent, op);
        WeightedDirectedCallTreeEdge<AssemblyComponentOperationPair> e =
                this.childMap.get(destination.getId());
        AbstractCallTreeNode<AssemblyComponentOperationPair> n;
        if (e != null) {
            n = e.getDestination();
        } else {
            n = new AggregatedAssemblyComponentOperationCallTreeNode(destination.getId(),
                    super.getSystemEntityFactory(), pairFactory, destination, false); // ! rootNode
            e = new WeightedDirectedCallTreeEdge<AssemblyComponentOperationPair>(this, n);
            this.childMap.put(destination.getId(), e);
            super.appendChildEdge(e);
        }
        e.incOutgoingWeight();
        return n;
    }
}
