/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.analysisteetime.util.timeseries;

import java.time.Duration;
import java.time.Instant;

/**
 *
 *
 * @author S�ren Henning
 *
 */
public class BoundedTimeSeries<T extends TimeSeriesPoint> extends TimeSeries<T> {

	private final Duration capacity;

	public BoundedTimeSeries(final Duration capacity) {
		super();
		this.capacity = capacity;
	}

	public BoundedTimeSeries(final Duration capacity, final TimeSeries<T> timeSeries) {
		super(timeSeries);
		this.capacity = capacity;
		removeOverflow();
	}

	public BoundedTimeSeries(final BoundedTimeSeries<T> timeSeries) {
		super(timeSeries);
		this.capacity = timeSeries.getCapacity();
	}

	@Override
	public void appendBegin(final T timeSeriesPoint) {
		super.appendBegin(timeSeriesPoint);
		removeOverflow();
	}

	@Override
	public void appendEnd(final T timeSeriesPoint) {
		super.appendEnd(timeSeriesPoint);
		removeOverflow();
	}

	public Duration getCapacity() {
		return this.capacity;
	}

	private void removeOverflow() {
		if (size() == 0) {
			return;
		}
		final Instant limit = super.getEnd().getTime().minus(this.capacity);
		while (size() > 0 && super.getBegin().getTime().isBefore(limit)) {
			super.removeBegin();
		}
	}
}
