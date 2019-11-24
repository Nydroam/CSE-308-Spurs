import sys
import json
from shapely.geometry import shape, GeometryCollection

class Edge:
  def __init__(self, n1, n2, weight):
    self.nodes=[n1,n2]
    self.weight=weight

class Graph:
  def __init__(self,name):
    self.name=name
    self.subgraphs=[]
    self.inner_edges=[]

  def add_node(self, component):
    if not self.get(component):
      self.subgraphs.append(component)

  def add_edge(self, edge):
    self.inner_edges.append(edge)

  def remove(self, component):
    self.subgraphs.remove(component)

  def get(self, target):
    for s in self.subgraphs:
      if s.name is target:
        return s
    return None

  def min_edge():
    cur_min = None
    for e in inner_edges:
      if not cur_min or e.weight < cur_min.weight:
        cur_min = e
    return cur_min

  def naivemerge_step(self):
    merged_nodes=[]
    while len(self.inner_edges):
      subgraph=Graph("2 Districts: ")
      min_edge=self.min_edge()
      if min_edge:
        self.inner_edges.remove(min_edge)
      subgraph.add_node(min_edge.nodes[0])
      subgraph.add_node(min_edge.nodes[1])
      subgraph.add_edge(min_edge)
      merged_nodes.append(subgraph)
    self.subgraphs = merged_nodes

  def __str__(self):
    out=self.name+"\n"
    for subgraph in self.subgraphs:
      out+=str(subgraph)+"\n"
    return out

class Node:
  def __init__(self,name):
    self.name=name
    self.neighbors={}
  def add_neighbor(self, neighbor_node, dist):
    self.neighbors[neighbor_node] = dist
  def __str__(self):
    out=""
    out+=("Node "+str(self.name)+"\n\t")
    for neighbor in self.neighbors:
      out+=(neighbor.name + " - " + str(self.neighbors[neighbor]) + ", ")
    return out

def load_geometry(filepath):
  with open(filepath,"r") as f:
    return json.load(f)
  return None

'''
{type:FeatureCollection
 features:[
  {
    geometry:{type:, coordinates:int[1][n][2]}
    type:'Polygon'
    properties: dict of arbitrarily set properties
  }]
}
'''
def load_graph(geojson_data, isPrecinct=True):
  intersections=[]
  graph={}

  g = Graph("Main Graph")

  #print geojson_data["features"][0]["geometry"]
  #shape_collection = GeometryCollection([shape(f["geometry"]) for f in geojson_data])
  if isPrecinct:
    shape_collection = [(f["properties"]["NAME10"], shape(f["geometry"])) for f in geojson_data]
  else:
    shape_collection = [(f["properties"]["NAMELSAD"], shape(f["geometry"])) for f in geojson_data]

  for s1 in range(len(shape_collection)):
    for s2 in range(s1+1,len(shape_collection)):
      if shape_collection[s1][1].intersects(shape_collection[s2][1]):
        if "NAMELSAD" in geojson_data[s1]["properties"]: # ID for congressional districts
          s1_name=geojson_data[s1]["properties"]["NAMELSAD"]
          s2_name=geojson_data[s2]["properties"]["NAMELSAD"]
        else:
          s1_name=geojson_data[s1]["properties"]["NAME10"]
          s2_name=geojson_data[s2]["properties"]["NAME10"]
        intersections.append((s1_name,s2_name))
        if s1_name not in graph:
          graph[s1_name]=[]
        if s2_name not in graph:
          graph[s2_name]=[]

        n1=g.get(s1_name) if g.get(s1_name) else Node(s1_name)
        n2=g.get(s2_name) if g.get(s2_name) else Node(s2_name)

        n1.add_neighbor(n2,1)
        n2.add_neighbor(n1,1)
        g.add_node(n1)
        g.add_node(n2)

        graph[s1_name].append(s2_name)
        graph[s2_name].append(s1_name)
  return g, shape_collection

def save(precincts, name):
  with open("data/boundary/ri/precinct.json","r") as f:
    template = json.load(f)
    updated = []
    for i in template["features"]:
      if i["properties"]["NAME"] in precincts:
        updated.append(i)
    template["features"] = updated
    with open(name,"w") as newf:
      json.dump(template, newf)

if __name__=="__main__":
  dat = load_geometry(sys.argv[1])
  adj_mat = load_graph(dat["features"])
