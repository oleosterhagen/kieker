/***************************************************************************
 * Copyright 2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.analysisteetime.util;

import com.google.common.base.Objects;

/**
 * @author S�ren Henning
 *
 * @since 1.13
 */
public final class ComposedKey<F, S> {

	private final F first;
	private final S second;

	private ComposedKey(final F first, final S second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof ComposedKey)) {
			return false;
		}
		final ComposedKey<?, ?> p = (ComposedKey<?, ?>) o;
		return Objects.equal(p.first, this.first) && Objects.equal(p.second, this.second);
	}

	@Override
	public int hashCode() {
		return (this.first == null ? 0 : this.first.hashCode()) ^ (this.second == null ? 0 : this.second.hashCode());
	}

	public static <F, S> ComposedKey<F, S> of(final F first, final S second) { // NOPMD (method name is declarative)
		return new ComposedKey<>(first, second);
	}

}
