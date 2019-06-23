package net.hatran;

import java.lang.reflect.Array;
import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import com.rits.cloning.Cloner;

// Paint
public class GCanvas {

    private final static Logger logger = Logger.getLogger(GCanvas.class);  // use log4j to save output message

    // Vetices
    Map<String, PositionInSpace> SpatialDatabase = new HashMap<String, PositionInSpace>();

    // Save triangles
    List<Triangle> triangles = new ArrayList<>();
    List<Triangle> final_triangles = new ArrayList<Triangle>();

    // Create a wire to .csv file of object
    private static final String CSV_SEPARATOR = ",";
    double minX;
    double maxX;
    double minY;
    double maxY;

    // Constructor function
    GCanvas() {
    }

    public static String getMinYPointIndex(Map<String, PositionInSpace> S) {
        double MinY = 0;
        String index = "";
        for(String key: S.keySet())
        {
            MinY = S.get(key).getY();
            index = key;
            break;
        }

        for(String key: S.keySet())
        {
            if (MinY > S.get(key).getY()) {
                MinY = S.get(key).getY();
                index = key;
            }
        }

        return index;
    }

    public static int PartitionByAngle(List<DirectedEdge> edges, int first,
                                       int end) {
        int i = first, j = end;
        while (i < j) {
            while (i < j
                    && edges.get(i).IncludedAngle <= edges.get(j).IncludedAngle)
                j--;
            if (i < j) {
                DirectedEdge temp = edges.get(i);
                edges.set(i, edges.get(j));
                edges.set(j, temp);
                i++;
            }

            while (i < j
                    && edges.get(i).IncludedAngle <= edges.get(j).IncludedAngle)
                i++;
            if (i < j) {
                DirectedEdge temp = edges.get(i);
                edges.set(i, edges.get(j));
                edges.set(j, temp);
                j--;
            }
        }

        return i;
    }

    // Fast sorting
    public static void QuickSortByAngle(List<DirectedEdge> edges, int first,
                                        int end) {
        int pivot;
        if (first < end) {
            pivot = PartitionByAngle(edges, first, end);
            QuickSortByAngle(edges, first, pivot - 1);
            QuickSortByAngle(edges, pivot + 1, end);
        }
    }

    public static int PartitionByLength(List<DirectedEdge> edges, int first,
                                        int end) {
        int i = first, j = end;
        while (i < j) {
            while (i < j && edges.get(i).Length <= edges.get(j).Length)
                j--;
            if (i < j) {
                DirectedEdge temp = edges.get(i);
                edges.set(i, edges.get(j));
                edges.set(j, temp);
                i++;
            }

            while (i < j && edges.get(i).Length <= edges.get(j).Length)
                i++;
            if (i < j) {
                DirectedEdge temp = edges.get(i);
                edges.set(i, edges.get(j));
                edges.set(j, temp);
                j--;
            }
        }

        return i;
    }


    public static void QuickSortByLength(List<DirectedEdge> edges, int first,
                                         int end) {
        int pivot;
        if (first < end) {
            pivot = PartitionByLength(edges, first, end);
            QuickSortByLength(edges, first, pivot - 1);
            QuickSortByLength(edges, pivot + 1, end);
        }
    }

    public static void Sort(List<DirectedEdge> edges) {
        int first = 0;
        int pivot = first;
        int end = first;
        while (first != edges.size()) {
            end++;
            if (end != edges.size()
                    && edges.get(first).IncludedAngle == edges.get(end).IncludedAngle) {
                pivot = end;
            } else if (end != edges.size()
                    && first == (end - 1)
                    && edges.get(first).IncludedAngle != edges.get(end).IncludedAngle) {
                first++;
            } else {
                QuickSortByLength(edges, first, pivot);
                first = end;
            }
        }
    }


    public static double calculateAngle(PositionInSpace left, PositionInSpace middle, PositionInSpace right) {
        double x1 = left.getX() - middle.getX();
        double y1 = left.getY() - middle.getY();
        double x2 = right.getX() - middle.getX();
        double y2 = right.getY() - middle.getY();
        double up = x1 * x2 + y1 * y2;
        double down = Math.sqrt(x1 * x1 + y1 * y1)
                * Math.sqrt(x2 * x2 + y2 * y2);
        return Math.acos(up / down) / Math.PI * 180;
    }


    public static void addDirectedEdgeToQueue(Queue<DirectedEdge> edges_queue, DirectedEdge edge, Map<String, List<DirectedEdge>> PointEdges) {

        edge.UsageCount++;
        int index = 0;
        int size = edges_queue.size();
        int num = 0;
        Queue<DirectedEdge> edges_queue_temp = new LinkedList<DirectedEdge>();
        while (index < size) {
            if (!edges_queue.peek().equal(edge)) {
                edges_queue_temp.offer(edges_queue.poll());
                num++;
            } else {
                DirectedEdge e = edges_queue.poll();
                e.UsageCount++;
            }
            index++;
        }

        if (num == size) {
            edges_queue_temp.offer(edge);
        }
        edges_queue.addAll(edges_queue_temp);

        num = 0;
        for (int i = 0; i < PointEdges.get(edge.StartPtIndex).size(); i++) {
            if (PointEdges.get(edge.StartPtIndex).get(i).equal(edge)) {
                // edge.StartPt.edges.get(i).UsageCount++;
                break;
            } else {
                num++;
            }
        }
        if (num == PointEdges.get(edge.StartPtIndex).size()) {
            PointEdges.get(edge.StartPtIndex).add(edge);
        }

        num = 0;
        for (int i = 0; i < PointEdges.get(edge.EndPtIndex).size(); i++) {
            if (PointEdges.get(edge.EndPtIndex).get(i).equal(edge)) {
                // edge.EndPt.edges.get(i).UsageCount++;
                break;
            } else {
                num++;
            }
        }
        if (num == PointEdges.get(edge.EndPtIndex).size()) {
            PointEdges.get(edge.EndPtIndex).add(edge);
        }
    }


    public double calculateCrossProduct(PositionInSpace StartPt1, PositionInSpace EndPt1, PositionInSpace StartPt2, PositionInSpace EndPt2) {

        return (EndPt1.getX() - StartPt1.getX()) * (EndPt2.getY() - StartPt2.getY())
                - (EndPt1.getY() - StartPt1.getY()) * (EndPt2.getX() - StartPt2.getX());
    }


    public boolean edgeToPointUsage(List<DirectedEdge> edges) {
        boolean moreThanTwo = false;

        if (edges.size() > 0) {
            int num = 0;
            for (DirectedEdge e : edges) {
                if (e.UsageCount >= 2) {
                    num++;
                }
            }
            if (num != 0 && num == edges.size()) {
                moreThanTwo = true;
            }
        }

        return moreThanTwo;
    }


    public boolean newEdgeUsage(String key, DirectedEdge e, List<DirectedEdge> edges)
    {
        boolean judge = false;

        for(DirectedEdge de: edges)
        {
            DirectedEdge new1 = new DirectedEdge(e.StartPtIndex, key);
            DirectedEdge new2 = new DirectedEdge(key, e.EndPtIndex);
            if((de.equal(new1)&&de.UsageCount==2) || (de.equal(new2)&&de.UsageCount==2))
            {
                judge = true;
                break;
            }
        }

        return judge;
    }


    public double calculateIncludedAngle(PositionInSpace startPt, PositionInSpace endPt)
    {
        double x = endPt.getX()-startPt.getX();
        double y = endPt.getY()-startPt.getY();
        double up = x*10+y*0;
        double down = Math.sqrt(x*x+y*y)*10;
        return Math.acos(up/down);
    }

    public double calculateLength(PositionInSpace startPt, PositionInSpace endPt)
    {

        double x = endPt.getX()-startPt.getX();
        double y = endPt.getY()-startPt.getY();
        return Math.sqrt(x*x+y*y);
    }

    public int Partition(Object[] edges, int first, int end)
    {
        int i=first, j=end;
        while(i<j)
        {
            while(i<j && ((DirectedEdge)edges[i]).Length<=((DirectedEdge)edges[j]).Length) j--;
            if(i<j)
            {
                double temp = ((DirectedEdge)edges[i]).Length;
                ((DirectedEdge)edges[i]).Length = ((DirectedEdge)edges[j]).Length;
                ((DirectedEdge)edges[j]).Length = temp;
                i++;
            }

            while(i<j && ((DirectedEdge)edges[i]).Length<=((DirectedEdge)edges[j]).Length) i++;
            if(i<j)
            {
                double temp = ((DirectedEdge)edges[i]).Length;
                ((DirectedEdge)edges[i]).Length = ((DirectedEdge)edges[j]).Length;
                ((DirectedEdge)edges[j]).Length = temp;
                j--;
            }
        }

        return i;
    }


    public void QuickSort(Object[] edges, int first, int end)
    {
        int pivot;
        if(first<end)
        {
            pivot = Partition(edges, first, end);
            QuickSort(edges, first, pivot-1);
            QuickSort(edges, pivot+1, end);
        }
    }


    private double calculateMedianLength(Set<DirectedEdge> edgesTable)
    {
        Object[] edges = edgesTable.toArray();
        QuickSort(edges, 0, edges.length-1);

        System.out.println("size/2 = " + edges.length + "/" + 2 + "=" + edges.length/2);
        return ((DirectedEdge)edges[edges.length/2]).Length;
    }


    private String obtainAnotherPoint(String point, DirectedEdge edge)
    {
        if(point.equals(edge.StartPtIndex))
        {
            return edge.EndPtIndex;
        }
        else
        {
            return edge.StartPtIndex;
        }
    }


    // Definite the write to .csv file function
    public void writeObjectToCSV(List<Triangle> triangles, String writeFilePathName)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeFilePathName), "UTF-8"));
            for (Triangle tri : triangles)
            {
                StringBuffer oneLine = new StringBuffer();
                oneLine.append(tri.Pt1.getFeature());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(tri.Pt1.getInstance());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(tri.Pt2.getFeature());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(tri.Pt2.getInstance());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(tri.Pt3.getFeature());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(tri.Pt3.getInstance());
                bw.write(oneLine.toString());
                bw.newLine();
            }
            bw.flush();
            System.out.println("Save triangle to csv finish!");
            bw.close();
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }

    // Function to remove duplicates from an ArrayList
    public static ArrayList<ArrayList<PositionInSpace>> removeDuplicates(ArrayList<ArrayList<PositionInSpace>> list)
    {

        // Create a new LinkedHashSet
        HashSet<ArrayList<PositionInSpace>> set = new HashSet<>();
        set.addAll(list);
        // Clear the list
        list.clear();
        // add the elements of set
        // with no duplicates to the list
        list.addAll(set);
        // return the list
        return list;
    }

    // Process data set
    public void processData() {
        long start = System.currentTimeMillis();

        // Load input data set
        // Run the last experiment

        String FileName = "./data/spare_instance/spare_instances_20k.csv";
        String json_file = "./out/spare_instance/spare_instances_20k_StarDelaunayTriangle_json.json";


        Cloner cloner = new Cloner();

        ReadTXTOrCSV reader = new ReadTXTOrCSV(FileName);
        this.SpatialDatabase = reader.getSpatialDatabase();
        this.minX = reader.getMinX();
        this.maxX = reader.getMaxX();
        this.minY = reader.getMinY();
        this.maxY = reader.getMaxY();
        Map<String, List<DirectedEdge>> PointEdges = reader.getPointEdges();

        String minYpointIndex = getMinYPointIndex(SpatialDatabase);
        List<DirectedEdge> edges = new ArrayList<DirectedEdge>();
        for (String key: SpatialDatabase.keySet()) {
            if (!minYpointIndex.equals(key)) {
                DirectedEdge edge = new DirectedEdge(minYpointIndex, key);
                edge.IncludedAngle = calculateIncludedAngle(SpatialDatabase.get(minYpointIndex), SpatialDatabase.get(key));
                edge.Length = calculateLength(SpatialDatabase.get(minYpointIndex), SpatialDatabase.get(key));
                edges.add(edge);
            }
        }
        QuickSortByAngle(edges, 0, edges.size() - 1);
        Sort(edges);
        List<String> ps = new ArrayList<String>();
        ps.add(minYpointIndex);
        for (int i = edges.size() - 1; i >= 0; i--) {
            ps.add(edges.get(i).EndPtIndex);
        }

        int flag = 0;
        while (flag != ps.size()) {
            flag = 0;
            for (int i = 0; i < ps.size(); i++, flag++) {
                DirectedEdge front = new DirectedEdge();
                DirectedEdge back = new DirectedEdge();
                if (i == 0) {
                    front.setStartPtAndEndPt(ps.get(i), ps.get(i + 1));
                    back.setStartPtAndEndPt(ps.get(ps.size() - 1), ps.get(i));
                } else if (i == ps.size() - 1) {
                    front.setStartPtAndEndPt(ps.get(i), ps.get(0));
                    back.setStartPtAndEndPt(ps.get(i - 1), ps.get(i));
                } else {
                    front.setStartPtAndEndPt(ps.get(i), ps.get(i + 1));
                    back.setStartPtAndEndPt(ps.get(i - 1), ps.get(i));
                }
                if (calculateCrossProduct(SpatialDatabase.get(back.StartPtIndex), SpatialDatabase.get(back.EndPtIndex), SpatialDatabase.get(front.StartPtIndex), SpatialDatabase.get(front.EndPtIndex)) > 0) {
                    ps.remove(i);
                    i--;
                }
            }
        }

        // Clear array
        edges.clear();
        for (int i = 0; i < ps.size(); i++) {
            DirectedEdge edge = new DirectedEdge(ps.get(i), ps.get((i+1)%ps.size()));
            edge.UsageCount++;
            edge.Length = calculateLength(SpatialDatabase.get(ps.get(i)), SpatialDatabase.get(ps.get((i+1)%ps.size())));
            edges.add(edge);
            PointEdges.get(ps.get(i)).add(edge);
            PointEdges.get(ps.get((i+1)%ps.size())).add(edge);
        }

        Queue<DirectedEdge> edges_queue = new LinkedList<DirectedEdge>();
        for (int i = 0; i < edges.size(); i++) {
            edges_queue.offer(edges.get(i));
        }
        int num = 0;
        int MAX = 0;
        // while (edges_queue.peek() != null && num != points.length) {
        while (edges_queue.peek() != null) {
            double max = 0;
            String point_flag = "";
            num = 0;
            DirectedEdge edge = edges_queue.poll();
            if (edge.UsageCount >= 2) {
                continue;
            }
            for (String key: SpatialDatabase.keySet()) {
                if (!key.equals(edge.StartPtIndex)
                        && !key.equals(edge.EndPtIndex)
                        && calculateCrossProduct(SpatialDatabase.get(key), SpatialDatabase.get(edge.StartPtIndex), SpatialDatabase.get(key), SpatialDatabase.get(edge.EndPtIndex)) < 0
                        && !edgeToPointUsage(PointEdges.get(key))
                        && !newEdgeUsage(key, edge, PointEdges.get(key))) {
                    double angle = calculateAngle(SpatialDatabase.get(edge.StartPtIndex), SpatialDatabase.get(key), SpatialDatabase.get(edge.EndPtIndex));
                    if (angle > max) {
                        max = angle;
                        point_flag = key;
                    }
                }
            }
            edge.UsageCount++;
            DirectedEdge new_edge1 = new DirectedEdge(edge.StartPtIndex, point_flag);
            new_edge1.Length = calculateLength(SpatialDatabase.get(edge.StartPtIndex), SpatialDatabase.get(point_flag));
            DirectedEdge new_edge2 = new DirectedEdge(point_flag, edge.EndPtIndex);
            new_edge2.Length = calculateLength(SpatialDatabase.get(point_flag), SpatialDatabase.get(edge.EndPtIndex));

            triangles.add(new Triangle(SpatialDatabase.get(edge.StartPtIndex), SpatialDatabase.get(edge.EndPtIndex), SpatialDatabase.get(point_flag), edge, new_edge1, new_edge2));
            addDirectedEdgeToQueue(edges_queue, new_edge1, PointEdges);
            addDirectedEdgeToQueue(edges_queue, new_edge2, PointEdges);

            MAX++;

        }
        logger.info("The number of original triangles:" + triangles.size());

        List<Triangle> deleteFeatureTriangles = triangles.parallelStream()
                .filter(tri -> ((tri.Pt1.getFeature() != (tri.Pt2.getFeature()))
                        && (tri.Pt1.getFeature() != tri.Pt3.getFeature())
                        && (tri.Pt2.getFeature() != tri.Pt3.getFeature())))
                .collect(Collectors.toList());

        logger.info("After deleting the same feature: " + deleteFeatureTriangles.size());

        Set<DirectedEdge> EdgesTable = new HashSet<DirectedEdge>();
        for(String key: PointEdges.keySet())
        {
            for(DirectedEdge e: PointEdges.get(key))
            {
                if(e.UsageCount == 2)
                {
                    EdgesTable.add(e);
                }
            }
        }

        double Global_Mean = 0;
        for(int i=0; i<EdgesTable.toArray().length; i++)
        {
            Global_Mean = Global_Mean + ((DirectedEdge)EdgesTable.toArray()[i]).Length;
        }
        Global_Mean = Global_Mean/EdgesTable.toArray().length;

        double Global_Variation = 0;
        for(int i=0; i<EdgesTable.toArray().length; i++)
        {
            Global_Variation = Global_Variation + Math.pow(Global_Mean-((DirectedEdge)EdgesTable.toArray()[i]).Length, 2);
        }
        Global_Variation = Math.sqrt(Global_Variation/(EdgesTable.toArray().length-1));

        Map<String, Double> Constraint = new HashMap<String, Double>();
        for(String key: PointEdges.keySet())
        {
            double FP = 0;
            double Local_Mean = 0;
            int size = 0;
            for(DirectedEdge e: PointEdges.get(key))
            {
                if(e.UsageCount == 2)
                {
                    Local_Mean = Local_Mean + e.Length;
                    size++;
                }
            }
            Local_Mean = Local_Mean/size;
            FP = Global_Mean+Global_Variation*Global_Mean/Local_Mean;
            Constraint.put(key, FP);
        }

        for(String key: PointEdges.keySet())  // key is a instance
        {
//            logger.info("key ------- :" + key);

            for(DirectedEdge e: PointEdges.get(key))
            {
//                if(e.UsageCount==2 && e.Length > Constraint.get(key))
                if(e.Length > Constraint.get(key))
                {
                    e.UsageCount--;
//                    logger.info("afer e----: " + e.UsageCount);
                }
            }
        }

        List<Triangle> deleteGlobalTriangles = deleteFeatureTriangles.parallelStream()
                .filter(tri ->
                                ( tri.edge1.Length <= Constraint.get(tri.Pt1.getFeature() + "." + tri.Pt1.getInstance())
                                        && tri.edge1.Length <= Constraint.get(tri.Pt2.getFeature() + "." + tri.Pt2.getInstance())
                                        && tri.edge2.Length <= Constraint.get(tri.Pt1.getFeature() + "." + tri.Pt1.getInstance())
                                        && tri.edge2.Length <= Constraint.get(tri.Pt3.getFeature() + "." + tri.Pt3.getInstance())
                                        && tri.edge3.Length <= Constraint.get(tri.Pt2.getFeature() + "." + tri.Pt2.getInstance())
                                        && tri.edge3.Length <= Constraint.get(tri.Pt3.getFeature() + "." + tri.Pt3.getInstance())))
                .collect(Collectors.toList());

        logger.info("After deleting global: " + deleteGlobalTriangles.size());

        Map<String, Set<DirectedEdge>> Two_Order_Edges = new HashMap<String, Set<DirectedEdge>>();
        Map<String, Set<String>> Two_Order_Points = new HashMap<String, Set<String>>();
        for(String key: PointEdges.keySet())
        {
            Set<DirectedEdge> edge_set = new HashSet<DirectedEdge>();
            Set<String> point_set = new HashSet<String>();
            for(DirectedEdge one_edge: PointEdges.get(key))
            {
                if(one_edge.UsageCount == 2)
                {
                    edge_set.add(one_edge);
                    String one_point = obtainAnotherPoint(key, one_edge);
                    point_set.add(one_point);
                    for(DirectedEdge two_edge: PointEdges.get(one_point))
                    {
                        if(two_edge.UsageCount==2 && !two_edge.equal(one_edge))
                        {
                            edge_set.add(two_edge);
                            point_set.add(obtainAnotherPoint(one_point, two_edge));
                        }
                    }
                }
            }
            Two_Order_Edges.put(key, edge_set);
            Two_Order_Points.put(key, point_set);
        }

        Constraint.clear();
        double beta = 1;
        for(String key: SpatialDatabase.keySet())
        {
            double two_order_mean = 0;
            double mean_variation = 0;
            if(Two_Order_Edges.get(key).size()!=0 && Two_Order_Points.get(key).size()!=0)
            {
                for(DirectedEdge e: Two_Order_Edges.get(key))
                {
                    two_order_mean = two_order_mean + e.Length;
                }
                two_order_mean = two_order_mean/Two_Order_Edges.get(key).size();
                for(String p: Two_Order_Points.get(key))
                {
                    double local_mean = 0;
                    int size = 0;
                    for(DirectedEdge ee: PointEdges.get(p))
                    {
                        if(ee.UsageCount == 2)
                        {
                            local_mean = local_mean + ee.Length;
                            size++;
                        }
                    }
                    local_mean = local_mean/size;
                    double local_variation = 0;
                    size = 0;
                    for(DirectedEdge ee: PointEdges.get(p))
                    {
                        if(ee.UsageCount == 2)
                        {
                            local_variation = local_variation + Math.pow(ee.Length-local_mean, 2);
                            size++;
                        }
                    }
                    if(size > 1)
                    {
                        local_variation = Math.sqrt(local_variation/(size-1));
                    }
                    mean_variation = mean_variation + local_variation;
                }
                mean_variation = mean_variation/Two_Order_Points.get(key).size();
            }
            Constraint.put(key, two_order_mean+beta*mean_variation);
        }

        for(String key: PointEdges.keySet())
        {
            for(DirectedEdge e: PointEdges.get(key))
            {
                if(e.UsageCount==2 && e.Length>Constraint.get(key))
                {
                    e.UsageCount--;
                }
            }
        }

        // Create a HashMap to store the star Delaunay Triangles
        HashMap<ArrayList<Integer>, ArrayList<ArrayList<PositionInSpace>>> starDelaunayTriangle = new HashMap<>();

        for(Triangle tri: deleteGlobalTriangles){
            if (
                    tri.edge1.Length <= Constraint.get(tri.Pt1.getFeature() + "." + tri.Pt1.getInstance())
                    && tri.edge1.Length <= Constraint.get(tri.Pt2.getFeature() + "." + tri.Pt2.getInstance())
                    && tri.edge2.Length <= Constraint.get(tri.Pt1.getFeature() + "." + tri.Pt1.getInstance())
                    && tri.edge2.Length <= Constraint.get(tri.Pt3.getFeature() + "." + tri.Pt3.getInstance())
                    && tri.edge3.Length <= Constraint.get(tri.Pt2.getFeature() + "." + tri.Pt2.getInstance())
                    && tri.edge3.Length <= Constraint.get(tri.Pt3.getFeature() + "." + tri.Pt3.getInstance())
            )

            {
                final_triangles.add(tri);
                ArrayList<PositionInSpace> keyList = new ArrayList<>(Arrays.asList(tri.Pt1, tri.Pt2, tri.Pt3));

                // Sort keyList
                Collections.sort(keyList, new SortByFeature());
//                // Convert ArrayList to HashMap
                // Store in HashMap
                // First: check table instance HashMap is empty
                if (starDelaunayTriangle.isEmpty()){
                    // Package the value
                    for (PositionInSpace emptyPO: keyList){
                        // Package the key
                        // Put key and value into HashMap
                        starDelaunayTriangle.put(new ArrayList<>(Arrays.asList(emptyPO.getFeature(), emptyPO.getInstance())),
                                new ArrayList<>(Arrays.asList(keyList)));
                    }

                }else {

                    // Second: if star delaunay is not empty
                    for(PositionInSpace existPO: keyList){
                        // Package the key
                        ArrayList<Integer> existKeyInputKey = new ArrayList<>(Arrays.asList(existPO.getFeature(), existPO.getInstance()));
                        //check this key exist?
                        boolean isExistKey = starDelaunayTriangle.containsKey(existKeyInputKey);

                        if (isExistKey){
                            // If the key existed, update the value
                            ArrayList<ArrayList<PositionInSpace>> existValue = new ArrayList<>();
                            existValue.addAll(starDelaunayTriangle.get(existKeyInputKey));
                            existValue.add(keyList);
                            // update HashMap
                            starDelaunayTriangle.put(existKeyInputKey, removeDuplicates(existValue));

                        }else {
                            // this key do not exist, directly put key and value into table instance
                            starDelaunayTriangle.put(existKeyInputKey, new ArrayList<>(Arrays.asList(keyList)));
                        }
                    }
                }
            }
        }
        logger.info("After deleting local: " + final_triangles.size());

       // Convert the key of starDelaunayTriangle from HashSet<Integer> to Integer to conviene future
        HashMap<Integer, ArrayList<ArrayList<PositionInSpace>>> starDelaunayTriangleIntegerKey = new HashMap<>();

        // Save each instance with its star triangles number
//        HashMap<Integer,Integer> instanceNumberTriangle = new HashMap<>();
        // Count the maximal number of triangle in which instance
        ArrayList<Integer> maximalSize = new ArrayList<>();

        int i = 0;
        for (Map.Entry<ArrayList<Integer>, ArrayList<ArrayList<PositionInSpace>>> entry : starDelaunayTriangle.entrySet()) {
            ArrayList<Integer> keyStar = entry.getKey();
            ArrayList<ArrayList<PositionInSpace>> valueStar = entry.getValue();
            starDelaunayTriangleIntegerKey.put(i, valueStar);
            maximalSize.add(valueStar.size());

            i++;
        }

        // Print Statistics
        logger.info("The total number of triangles is:" + final_triangles.size());
        logger.info("The maximal trianle in one instance is: "  + Collections.max(maximalSize));


        // Save the result to json file
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(json_file), starDelaunayTriangleIntegerKey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Total time：" + (System.currentTimeMillis() - start) / 1000.0 + " seconds");

    }

}
