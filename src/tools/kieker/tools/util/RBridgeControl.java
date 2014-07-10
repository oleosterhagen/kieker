/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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

package kieker.tools.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

import org.math.R.Rsession;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REXPVector;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;

/**
 * 
 * @author Tillmann Carlos Bielefeld
 * @since 1.10
 * 
 */
public final class RBridgeControl {

	private static final Log LOG = LogFactory.getLog(RBridgeControl.class);
	private static final AtomicInteger NEXTVARID = new AtomicInteger(1);

	private Rsession rCon;

	protected RBridgeControl() {

		final OutputStream out;

		out = new OutputStream2StandardLog();
		try {
			this.rCon = Rsession.newLocalInstance(new PrintStream(out, true, "UTF-8"), null);
		} catch (final UnsupportedEncodingException e) {
			LOG.error(e.toString(), e);
		}
	}

	/**
	 * wraps the execution of an arbitrary R expression. Logs result and error
	 * 
	 * @param input
	 *            R expression
	 * @return result/error
	 */
	public Object evalWithR(final String input) {
		Object out = null;
		try {
			out = this.rCon.eval(input);

			Object output = null;

			if (out instanceof REXPString) {
				output = ((REXPString) out).asString();
			} else if (out instanceof REXPLogical) {
				output = ((REXPLogical) out).toDebugString();
			} else {
				output = out;
			}

			RBridgeControl.LOG.info("> REXP: " + input + " return: " + output);

		} catch (final REXPMismatchException exc) {
			RBridgeControl.LOG.error("Error R expr.: " + input + " Cause: "
					+ exc, exc);
		}
		return out;
	}

	/**
	 * 
	 * @param variable
	 *            variable to R
	 */
	public void toTS(final String variable) {
		if (variable != null) {
			final StringBuffer buf = new StringBuffer();
			buf.append(variable);
			buf.append(" <<- ts(");
			buf.append(variable);
			buf.append(')');
			this.evalWithR(buf.toString());
		}
	}

	/**
	 * 
	 * @param variable
	 *            variable to R
	 * @param frequency
	 *            frequency to R
	 */
	public void toTS(final String variable, final long frequency) {
		if (variable != null) {
			final StringBuffer buf = new StringBuffer(21);
			buf.append(variable);
			buf.append(" <<- ts(");
			buf.append(variable);
			buf.append(", frequency=");
			buf.append(frequency);
			buf.append(')');
			this.evalWithR(buf.toString());
		}
	}

	/**
	 * 
	 * @param input
	 *            string
	 * @return -666.666 error, else dbvalue
	 */
	public double eDbl(final String input) {
		try {
			return ((REXPDouble) this.evalWithR(input)).asDouble();
		} catch (final REXPMismatchException exc) {
			RBridgeControl.LOG.error("Error casting value from R: " + input
					+ " Cause: " + exc);
			return -666.666;
		}
	}

	/**
	 * 
	 * @param input
	 *            inputstring
	 * @return Rdata
	 */
	public String eString(final String input) {
		final REXPString str = (REXPString) this.evalWithR(input);
		if (str != null) {
			return str.toString();
		} else {
			return "";
		}
	}

	/**
	 * 
	 * @param input
	 *            inputstring
	 * @return Rdata
	 */
	public double[] eDblArr(final String input) {
		try {
			final REXPVector res = (REXPVector) this.evalWithR(input);
			return res.asDoubles();
		} catch (final REXPMismatchException e) {
			return new double[0];
		}
	}

	/**
	 * 
	 * @param variable
	 *            string
	 * 
	 * @param values
	 *            assign value
	 */
	public void assign(final String variable, final double[] values) {
		final StringBuffer buf = new StringBuffer();
		buf.append(variable);
		buf.append(" <<- c(");
		boolean first = true;
		for (final double item : values) {
			if (!first) {
				buf.append(',');
			} else {
				first = false;
			}
			buf.append(item);
		}
		buf.append(')');
		this.evalWithR(buf.toString());
	}

	/**
	 * 
	 * @param variable
	 *            string
	 * @param values
	 *            assign vaules
	 */
	public void assign(final String variable, final Double[] values) {
		final StringBuffer buf = new StringBuffer();
		buf.append(variable);
		buf.append(" <<- c(");
		boolean first = true;
		for (final Double item : values) {
			if (!first) {
				buf.append(',');
			} else {
				first = false;
			}
			if ((null == item) || item.isNaN()) {
				buf.append("NA");
			} else {
				buf.append(item);
			}
		}
		buf.append(')');
		this.evalWithR(buf.toString());
	}

	/**
	 * 
	 * @param variable
	 *            string
	 * @param values
	 *            assign vaules
	 */
	public void assign(final String variable, final Long[] values) {
		final StringBuffer buf = new StringBuffer();
		buf.append(variable);
		buf.append(" <<- c(");
		boolean first = true;
		for (final Long item : values) {
			if (!first) {
				buf.append(',');
			} else {
				first = false;
			}
			buf.append(item);
		}
		buf.append(',');
		this.evalWithR(buf.toString());
	}

	/**
	 * Returns a globally unique variable name.
	 * 
	 * @return string unique name
	 */
	public static String uniqueVarname() {
		return String.format("var_%s",
				RBridgeControl.NEXTVARID.getAndIncrement());
	}

	// GET SINGLETON INSTANCE
	// #############################
	public static final RBridgeControl getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * SINGLETON.
	 */
	private static final class LazyHolder { // NOCS
		static final RBridgeControl INSTANCE;
		static {
			INSTANCE = new RBridgeControl();
		}
	}
}
