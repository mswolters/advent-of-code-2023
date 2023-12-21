import java.util.*
import kotlin.math.sign

data class Path<N>(val nodes: List<N>, val length: Double)
data class PathData<N>(var visitedNodes: Map<N, Path<N>> = mapOf())

fun <N> findPath(start: N, isEnd: (node: N, pathToNode: Path<N>) -> Boolean, data: PathData<N> = PathData(mapOf()), edgesForNode: (node: N, withPath: Path<N>) -> List<Pair<N, Double>>): Path<N>? {
    val distancesToNodes = mutableMapOf(start to Path(listOf(start), 0.0))
    val nodesToVisit: SortedSet<N> = TreeSet { left, right ->
        if (left == right) {
            return@TreeSet 0
        }

        val comparison = (distancesToNodes[left]!!.length - distancesToNodes[right]!!.length).toInt()
        if (comparison == 0) {
            if (left.hashCode() > right.hashCode()) 1 else -1
        } else {
            comparison
        }
    }
    var (shortestNode, shortestPath) = distancesToNodes.minBy { (_, path) -> path.length }
    while (!(isEnd(shortestNode, shortestPath))) {
        val edges = edgesForNode(shortestNode, shortestPath)
        edges.forEach { (node, length) ->
            val pathForNode = distancesToNodes[node]
            if (pathForNode == null || pathForNode.length > shortestPath.length + length) {
                distancesToNodes[node] = Path(shortestPath.nodes + node, shortestPath.length + length)
                nodesToVisit.add(node)
            } else {
                //println("Skipping")
            }
        }
        if (nodesToVisit.isEmpty()) {
            data.visitedNodes = distancesToNodes
            return null
        }
        shortestNode = nodesToVisit.first()
        nodesToVisit.remove(shortestNode)
        shortestPath = distancesToNodes[shortestNode]!!
    }
    data.visitedNodes = distancesToNodes
    return shortestPath
}

fun <N> findPathAStar(start: N, isEnd: (node: N, pathToNode: Path<N>) -> Boolean, data: PathData<N> = PathData(mapOf()), edgesForNode: (node: N, withPath: Path<N>) -> List<Pair<N, Double>>, potential: (node: N) -> Double): Path<N>? {
    val modifiedEdgesForNode = { forNode: N, withPath: Path<N> ->
        edgesForNode(forNode, withPath)
            .map { (node, length) -> Pair(node, length + potential(node) - potential(forNode)) }
    }

    return findPath(start, isEnd, data, modifiedEdgesForNode)
}