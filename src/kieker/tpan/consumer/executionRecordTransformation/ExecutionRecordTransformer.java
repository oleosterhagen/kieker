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
package kieker.tpan.consumer.executionRecordTransformation;

import java.util.ArrayList;
import java.util.StringTokenizer;
import kieker.tpan.consumer.IKiekerRecordConsumer;
import kieker.tpan.consumer.RecordConsumerExecutionException;
import kieker.tpan.datamodel.AllocationComponentInstance;
import kieker.tpan.datamodel.AssemblyComponentInstance;
import kieker.tpan.datamodel.ComponentType;
import kieker.tpan.datamodel.Execution;
import kieker.tpan.datamodel.ExecutionContainer;
import kieker.tpan.datamodel.Operation;
import kieker.tpan.datamodel.Signature;
import kieker.tpan.datamodel.factories.SystemEntityFactory;
import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.OperationExecutionRecord;

/**
 * Transforms KiekerExecutionRecords into Execution objects.
 *
 * @author Andre van Hoorn
 */
public class ExecutionRecordTransformer implements IKiekerRecordConsumer {

    private final SystemEntityFactory systemFactory;

    private ArrayList<IExecutionListener> listeners =
            new ArrayList<IExecutionListener>();

    public ExecutionRecordTransformer(
            final SystemEntityFactory systemFactory) {
        this.systemFactory = systemFactory;
    }

    public String[] getRecordTypeSubscriptionList() {
        return new String[]{OperationExecutionRecord.class.getName()};
    }

    public void addListener (IExecutionListener l){
        this.listeners.add(l);
    }

    private Signature createSignature(final String operationSignatureStr){
        String returnType = "N/A";
        String name;
        String[] paramTypeList;
        int openParenIdx = operationSignatureStr.indexOf('(');
        if (openParenIdx == -1){ // no parameter list
            paramTypeList = new String[]{};
            name = operationSignatureStr;
        } else {
            name = operationSignatureStr.substring(0, openParenIdx);
            StringTokenizer strTokenizer =
                    new StringTokenizer(operationSignatureStr.substring(openParenIdx+1, operationSignatureStr.length()-1), ",");
            paramTypeList = new String[strTokenizer.countTokens()];
            for (int i=0; strTokenizer.hasMoreTokens(); i++){
                paramTypeList[i] = strTokenizer.nextToken().trim();
            }
        }

        return new Signature(name, returnType, paramTypeList);
    }

    public void consumeMonitoringRecord(AbstractMonitoringRecord monitoringRecord) throws RecordConsumerExecutionException {
        if (!(monitoringRecord instanceof OperationExecutionRecord)) {
            throw new RecordConsumerExecutionException("Can only process records of type"
                    + OperationExecutionRecord.class.getName() + " but received" + monitoringRecord.getClass().getName());
        }
        OperationExecutionRecord execRec = (OperationExecutionRecord) monitoringRecord;

        String executionContainerName = execRec.vmName;
                //(this.considerExecutionContainer) ? execRec.vmName : "DEFAULTCONTAINER";
        String componentTypeName = execRec.componentName;
        String assemblyComponentName = componentTypeName;
        String allocationComponentName =
                new StringBuilder(executionContainerName).append("::").append(assemblyComponentName).toString();
        String operationFactoryName =
                new StringBuilder(assemblyComponentName).append(".").append(execRec.opname).toString();
        String operationSignatureStr = execRec.opname;

        AllocationComponentInstance allocInst = this.systemFactory.getAllocationFactory().getAllocationComponentInstanceByFactoryIdentifier(allocationComponentName);
        if (allocInst == null) { /* Allocation component instance doesn't exist */
            AssemblyComponentInstance assemblyComponent =
                    this.systemFactory.getAssemblyFactory().getAssemblyComponentInstanceByFactoryIdentifier(assemblyComponentName);
            if (assemblyComponent == null) { // assembly instance doesn't exist
                ComponentType componentType =
                        this.systemFactory.getTypeRepositoryFactory().getComponentTypeByFactoryIdentifier(componentTypeName);
                if (componentType == null) {
                    /* Component type doesn't exist */
                    componentType = this.systemFactory.getTypeRepositoryFactory().createAndRegisterComponentType(componentTypeName, componentTypeName);
                }
                assemblyComponent = this.systemFactory.getAssemblyFactory().createAndRegisterAssemblyComponentInstance(assemblyComponentName, componentType);
            }
            ExecutionContainer execContainer = this.systemFactory.getExecutionEnvironmentFactory().getExecutionContainerByFactoryIdentifier(executionContainerName);
            if (execContainer == null){ /* doesn't exist, yet */
               execContainer = systemFactory.getExecutionEnvironmentFactory().createAndRegisterExecutionContainer(executionContainerName, executionContainerName);
            }
            allocInst = this.systemFactory.getAllocationFactory().createAndRegisterAllocationComponentInstance(allocationComponentName, assemblyComponent, execContainer);
        }

        Operation op = this.systemFactory.getOperationFactory().getOperationByFactoryIdentifier(operationFactoryName);
        if (op == null) { /* Operation doesn't exist */
            Signature signature = createSignature(operationSignatureStr);
            op = this.systemFactory.getOperationFactory().createAndRegisterOperation(operationFactoryName, allocInst.getAssemblyComponent().getType(), signature);
            allocInst.getAssemblyComponent().getType().addOperation(op);
        }

        Execution execution = new Execution(op, allocInst, execRec.traceId,
                execRec.sessionId, execRec.eoi, execRec.ess, execRec.tin, execRec.tout);
        for (IExecutionListener l : this.listeners){
            try {
                l.newExecutionEvent(execution);
            } catch (ExecutionEventProcessingException ex) {
                throw new RecordConsumerExecutionException("ExecutionEventProcessingException occured", ex);
            }
        }
    }

    public boolean execute() throws RecordConsumerExecutionException {
        return true;
    }

    public void terminate() {
        for (IExecutionListener l : this.listeners){
            l.terminate();
        }
    }
}
