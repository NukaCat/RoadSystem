import java.awt.*;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.*;


public class City {
  public City(int layerCount, int nodePerLayerCount) {
    this.layerCount = layerCount;
    this.nodePerLayerCount = nodePerLayerCount;
    int nodeCount = layerCount * nodePerLayerCount;
    Random random = new Random();
    nodes = new Node[nodeCount];
    double[] radiuses = new double[nodeCount];
    double[] angles = new double[nodeCount];
    graph = new RoadMap(nodeCount);

    double dr = 1. / layerCount;
    double da = 2. * PI / nodePerLayerCount;
    for (int i = 0; i < nodeCount; i++) {
      radiuses[i] = dr * (i / nodePerLayerCount + 0.5) + random.nextDouble() * dr / 2;
      angles[i] = da * (i % nodePerLayerCount) + random.nextDouble() * da / 2;
      if (random.nextInt() % 10 < 6) {
        graph.set(i, i - 1, 1);
        graph.set(i - 1, i, 1);
      }
      if (random.nextInt() % 10 < 6) {
        graph.set(i, i - nodePerLayerCount, 1);
        graph.set(i - nodePerLayerCount, i, 1);
      }
    }
    for (int i = 0; i < nodeCount; i++) {
      nodes[i] = new Node();
      nodes[i].x = radiuses[i] * cos(angles[i]);
      nodes[i].y = radiuses[i] * sin(angles[i]);
    }

    roadViews = new HashMap();
    for (int i = 0; i < nodeCount; i++) {
      for (int j = 0; j < nodeCount; j++) {
        if (graph.get(i, j) > 0) {
          roadViews.put(i * nodeCount + j, new RoadView(20));
          double dx = nodes[i].x - nodes[j].x;
          double dy = nodes[i].y - nodes[j].y;
          graph.set(i, j, max(1, (int) (sqrt(dx * dx + dy * dy) * 20)) );
        }
      }
    }
  }

  public RoadView getRoadView(int i, int j) {
    return roadViews.get(i * nodes.length + j);
  }

  public int getBorder(double r){
    return ((int)(r*layerCount))*nodePerLayerCount;
  }

  private Node[] nodes;
  private RoadMap graph;
  private HashMap<Integer, RoadView> roadViews;
  int layerCount;
  int nodePerLayerCount;

  public static City instance() {
    return instance;
  }

  private static final City instance = new City(10, 16);

  public void draw(Graphics g, int h, int w) {
    //draw nodes
    for (Node node : nodes) {
      int x = (int) (node.x * w / 2) + w / 2;
      int y = (int) (node.y * h / 2) + h / 2;
      g.drawRect(x - 2, y - 2, 4, 4);
    }
    //draw lines
    for (int i = 0; i < nodes.length; i++) {
      for (int j = 0; j < i; j++) {
        if (graph.get(i, j) > 0) {
          int x = (int) (nodes[i].x * w / 2) + w / 2;
          int y = (int) (nodes[i].y * h / 2) + h / 2;
          int u = (int) (nodes[j].x * w / 2) + w / 2;
          int v = (int) (nodes[j].y * h / 2) + h / 2;
          g.drawLine(x, y, u, v);
        }
      }
    }
    //draw cars
    g.setColor(Color.RED);
    roadViews.forEach((node, view) -> {
      int i = node / nodes.length;
      int j = node % nodes.length;
      double dy = nodes[i].y - nodes[j].y;
      double dx = nodes[i].x - nodes[j].x;
      double ab = sqrt(dx * dx + dy * dy);
      int x = toInt((nodes[i].x + nodes[j].x) / 2, w) + (int) (dy / ab * 5);
      int y = toInt((nodes[i].y + nodes[j].y) / 2, h) + (int) (-dx / ab * 5);
      if(view.carCount() > 0) {
        g.drawString(Integer.toString(view.carCount()), x - 4, y + 4);
      }
    });

  }

  public RoadMap getMap() {
    return graph;
  }

  class Node {
    public double x;
    public double y;
  }

  private int toInt(double x, int n) {
    return (int) (x * n / 2) + n / 2;
  }
}
