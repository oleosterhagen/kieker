package kieker.monitoring.core.controller;

/**
 * @author Jan Waller, Robert von Massow
 */
public interface IMonitoringController extends IStateController, ITimeSourceController, IWriterController, ISamplingController {
	// ensures to publish this in the MBean
	public String toString();
}
