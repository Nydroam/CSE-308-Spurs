from shapely.geometry import shape
from shapely.ops import nearest_points
from shapely.geometry.linestring import LineString
from shapely.geometry.multilinestring import MultiLineString
from shapely.geometry.polygon import Polygon
from shapely.geometry.multipolygon import MultiPolygon
from shapely.geometry.collection import GeometryCollection
from shapely.strtree import STRtree

import pyproj
import json

MIN_BOUNDARY = 100

geod = pyproj.Geod(ellps="GRS80")

def get_name(precinct):
    return precinct["properties"]["NAME"]

def get_geo_by_name(collection, name):
    for geo in collection["features"]:
        if geo["properties"]["NAME"] == name:
            return geo
    return None

def get_linestring_distance(line_coordinates):
    previous = None
    total_dist = 0.0
    for coordinate in line_coordinates.coords:
        if previous != None:
            angle1,angle2,distance = geod.inv(previous[0], previous[1], coordinate[0], coordinate[1])
            total_dist += distance #meters
        previous = coordinate
    return total_dist

def get_polygon_distance(polygon_coordinates):
    previous = None
    total_dist = 0.0
    for coordinate in polygon_coordinates.exterior.coords:
        if previous != None:
            angle1,angle2,distance = geod.inv(previous[0], previous[1], coordinate[0], coordinate[1])
            total_dist += distance #meters
        previous = coordinate
    return total_dist / 2

def get_distance(line_coordinates):
    if isinstance(line_coordinates, LineString):
        return get_linestring_distance(line_coordinates)
    elif isinstance(line_coordinates, MultiLineString):
        total_dist = 0.0
        for linestring in line_coordinates:
            total_dist += get_linestring_distance(linestring)
        return total_dist
    elif isinstance(line_coordinates, Polygon):
        return get_polygon_distance(line_coordinates)
    elif isinstance(line_coordinates, MultiPolygon):
        total_dist = 0.0
        for linestring in line_coordinates:
            total_dist += get_polygon_distance(linestring)
        return total_dist
    elif isinstance(line_coordinates, GeometryCollection):
        total_dist = 0
        for linecoord in line_coordinates:
            total_dist += get_distance(linecoord)
        return total_dist
    else:
        return 0

def find_nearest_neighbors(strtree, target):
    distance = 0.1
    neighbor_candidates = strtree.query(target.buffer(distance))
    while len(neighbor_candidates) <= 1:
        distance *= 1.5
        neighbor_candidates = strtree.query(target.buffer(distance))
    return neighbor_candidates

def develop_neighbor_map(inf, outf):
    with open(inf, "r") as f:
        prec_data = json.load(f)

    prec_collection = []
    for prec in prec_data["features"]:
        cur_prec = shape(prec["geometry"])
        cur_prec.original = prec
        prec_collection.append(cur_prec)

    intersections = {}
    islands = {}
    prec_tree = STRtree(prec_collection)

    for prec in prec_collection:
        intersections[get_name(prec.original)] = \
            [neighbor.original for neighbor in prec_tree.query(prec.buffer(0.0))]
        if len(intersections[get_name(prec.original)]) <= 1:
            islands[get_name(prec.original)] = [neighbor.original for neighbor in find_nearest_neighbors(prec_tree, prec)]

    neighbor_map = {}
    for prec_name in intersections:
        neighbor_map[prec_name] = []
        prec = get_geo_by_name(prec_data, prec_name)
        for neighbor in intersections[prec_name]:
            intersect = shape(prec["geometry"]).intersection(shape(neighbor["geometry"]))
            if get_distance(intersect) >= MIN_BOUNDARY:
                neighbor_map[get_name(prec)].append(get_name(neighbor))
    for prec_name in islands:
        neighbor_map[prec_name] = [get_name(nearest_neighbor) for nearest_neighbor in islands[prec_name]]

    with open(outf, "w") as f:
        json.dump(neighbor_map, f)

if __name__ == "__main__":
    develop_neighbor_map("../ri_cvap/precinct_clean.json","../ri_cvap/precinct_neighbors.json")
    develop_neighbor_map("../pa_cvap/precinct_clean.json","../pa_cvap/precinct_neighbors.json")
    develop_neighbor_map("../ca_cvap/precincts_with_district.json","../ca_cvap/precinct_neighbors.json")