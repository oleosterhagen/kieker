package kieker.analysisteetime.util.graph.traversal;

import java.util.List;

import kieker.analysisteetime.util.graph.Edge;
import kieker.analysisteetime.util.graph.Graph;
import kieker.analysisteetime.util.graph.Vertex;

/**
 * @author S�ren Henning
 *
 * @since 1.13
 */
public class FlatGraphTraverser extends AbstractGraphTraverser {

	public FlatGraphTraverser() {
		super();
	}

	public FlatGraphTraverser(final List<VertexVisitor> vertexVisitors, final List<EdgeVisitor> edgeVisitors) {
		super(vertexVisitors, edgeVisitors);
	}

	public FlatGraphTraverser(final VertexVisitor vertexVisitor, final EdgeVisitor edgeVisitor) {
		super(vertexVisitor, edgeVisitor);
	}

	@Override
	public void traverse(final Graph graph) {

		for (final Vertex vertex : graph.getVertices()) {
			for (final VertexVisitor visitor : this.vertexVisitors) {
				visitor.visitVertex(vertex);
			}
		}

		for (final Edge edge : graph.getEdges()) {
			for (final EdgeVisitor visitor : this.edgeVisitors) {
				visitor.visitEdge(edge);
			}
		}

	}

}
