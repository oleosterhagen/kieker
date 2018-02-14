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
package kieker.monitoring.writer.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.io.DefaultValueSerializer;
import kieker.common.util.filesystem.FSUtil;
import kieker.monitoring.registry.GetIdAdapter;
import kieker.monitoring.registry.WriterRegistry;
import kieker.monitoring.writer.WriterUtil;
import kieker.monitoring.writer.filesystem.compression.ICompressionFilter;

/**
 * Binary log stream handler.
 *
 * @author Reiner Jung
 *
 * @since 1.14
 *
 */
public class BinaryLogStreamHandler extends AbstractLogStreamHandler {

	private static final Log LOGGER = LogFactory.getLog(BinaryLogStreamHandler.class);

	private final ByteBuffer buffer;

	/**
	 * Create a binary log stream handler.
	 *
	 * @param flushLogFile
	 *            flush log file
	 * @param bufferSize
	 *            buffer size
	 * @param charset
	 *            charset, presently not used in binary serialization
	 * @param compressionFilter
	 *            compression filter
	 * @param writerRegistry
	 *            writer registry.
	 */
	public BinaryLogStreamHandler(final Boolean flushLogFile, final Integer bufferSize, final Charset charset, // NOPMD charset not used in binary
			final ICompressionFilter compressionFilter, final WriterRegistry writerRegistry) {
		super(flushLogFile, bufferSize, charset, compressionFilter, writerRegistry);
		this.buffer = ByteBuffer.allocateDirect(bufferSize);
		this.serializer = DefaultValueSerializer.create(this.buffer, new GetIdAdapter<>(writerRegistry));
		this.extension = FSUtil.BINARY_FILE_EXTENSION;
	}

	@Override
	public void serialize(final IMonitoringRecord record, final int id) throws IOException {
		this.buffer.putInt(id);
		this.buffer.putLong(record.getLoggingTimestamp());

		record.serialize(this.serializer);

		this.buffer.flip();

		try {
			while (this.buffer.hasRemaining()) {
				this.numOfBytes += this.outputChannel.write(this.buffer);
			}
			this.numOfEntries++;

			this.buffer.clear();
			if (this.flushLogFile) {
				this.serializedStream.flush();
			}
		} catch (final IOException e) {
			LOGGER.error("Caught exception while writing to the channel.", e);
			WriterUtil.close(this.outputChannel, LOGGER);
		}
	}

}
