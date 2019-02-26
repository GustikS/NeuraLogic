/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ida.utils.hypergraphs;

import ida.utils.Sugar;

import java.util.Set;

/**
 * For the hypergraph-methods to be applicable on general hypergraphs,
 * we would need to remove multiple edges, otherwise it would not work properly
 *
 * Created by ondrejkuzelka on 26/06/14.
 */
public class HypergraphUtils {

    public static boolean isTreelike(Hypergraph h){
        return isTreelike_impl(h.copy());
    }

    private static boolean isTreelike_impl(Hypergraph h){
        Set<Integer> vd, ed;
        int count = 0;
        do {
            vd = Sugar.setFromCollections(h.getVerticesByDegree(0), h.getVerticesByDegree(1));
            ed = Sugar.setFromCollections(h.getEdgesByDegree(0), h.getEdgesByDegree(1));
            for (Integer v : vd){
                h.removeVertex(v);
            }
            for (Integer e : ed){
                h.removeEdge(e);
            }
            if (count++ > 10){
                break;
            }
        } while (vd.size() > 0 || ed.size() > 0);
        return h.countEdges() == 0 || h.countVertices() == 0;
    }

    public static boolean isAcyclic(Hypergraph h){
        return isAcyclic_impl(h.copy());
    }

    private static boolean isAcyclic_impl(Hypergraph h){
        Set<Integer> vd, subedges;
        do {
            vd = Sugar.setFromCollections(h.getVerticesByDegree(0), h.getVerticesByDegree(1));
            subedges = h.getEdgesContainedInAtLeastKEdges(1);
            for (Integer v : vd){
                h.removeVertex(v);
            }
            for (Integer e : subedges){
                h.removeEdge(e);
            }
            if ((subedges.size() > 0 && subedges.equals(h.getEdgesContainedInAtLeastKEdges(1))) ||
                    (vd.size() > 0 && vd.equals(Sugar.setFromCollections(h.getVerticesByDegree(0), h.getVerticesByDegree(1))))){
                throw new RuntimeException("A big problem here");
            }
        } while (vd.size() > 0 || subedges.size() > 0);
        return h.countEdges() == 0 || h.countVertices() == 0;
    }

}
