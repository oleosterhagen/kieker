package kieker.analysis.behavior.mtree;

import kieker.analysis.behavior.mtree.DistanceFunctions.EuclideanCoordinate;

class Data implements EuclideanCoordinate, Comparable<Data> {

	private final int[] values;
	private final int hashCode;

	Data(final int... values) {
		this.values = values;

		int hashCode = 1;
		for (final int value : values) {
			hashCode = 31 * hashCode + value;
		}
		this.hashCode = hashCode;
	}

	@Override
	public int dimensions() {
		return this.values.length;
	}

	@Override
	public double get(final int index) {
		return this.values[index];
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Data) {
			final Data that = (Data) obj;
			if (this.dimensions() != that.dimensions()) {
				return false;
			}
			for (int i = 0; i < this.dimensions(); i++) {
				if (this.values[i] != that.values[i]) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(final Data that) {
		final int dimensions = Math.min(this.dimensions(), that.dimensions());
		for (int i = 0; i < dimensions; i++) {
			final int v1 = this.values[i];
			final int v2 = that.values[i];
			if (v1 > v2) {
				return +1;
			}
			if (v1 < v2) {
				return -1;
			}
		}

		if (this.dimensions() > dimensions) {
			return +1;
		}

		if (that.dimensions() > dimensions) {
			return -1;
		}

		return 0;
	}

}
