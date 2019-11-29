import json
from shapely.geometry import shape, GeometryCollection
import sys

def precincts_to_region(region_type, foreign_key_id, foreign_key_name):
    with open(sys.argv[2], "r") as precinct_f:
        precincts = json.load(precinct_f)
    with open(sys.argv[3], "r") as district_f:
        districts = json.load(district_f)
    dist_shapes = [{"geoid": d["properties"][foreign_key_id], "NAME": d["properties"][foreign_key_name], "shape": shape(d["geometry"])} for d in districts["features"]]
    for p in precincts["features"]:
        max_intersection = None
        max_intersection_area = 0
        for dist in dist_shapes:
            p_shape = shape(p["geometry"])
            if p_shape.intersects(dist["shape"]) and p_shape.intersection(dist["shape"]).area > max_intersection_area:
                max_intersection = dist
                max_intersection_area = p_shape.intersection(dist["shape"]).area
        p["properties"][region_type.upper()] = max_intersection["NAME"]
        p["properties"][region_type+"_geoid"] = max_intersection["geoid"]
    with open("precincts_with_%s.json"%region_type, "w") as f:
        json.dump(precincts, f)

def aggregate_state_data():
    with open(sys.argv[2], "r") as precinct_f:
        precincts = json.load(precinct_f)
    relevant_keys = ["BLACK","WHITE","ASIAN","HISP","AMIN","NHPI",\
                    "SEN18D", "SEN18R", "SEN18O", "GOV18D", "GOV18R", "GOV18O", \
                    "PRES16D", "PRES16R", "PRES16O", "SEN16D", "SEN16R", "SEN16O", \
                    "SEN14D", "SEN14R", "SEN14O", "GOV14D", "GOV14R", "GOV14O"]
    state = {}
    for precinct in precincts["features"]:
        for key in precinct["properties"]:
            if key in relevant_keys:
                if key not in state:
                    state[key] = 0.0
                state[key] += precinct["properties"][key]
    with open(sys.argv[3], "w") as state_f:
        json.dump(state, state_f)

if __name__ == "__main__":
    if sys.argv[1] == "ptod":
        precincts_to_region("district", "geoid", "NAME")
    elif sys.argv[1] == "ptoc":
        precincts_to_region("county", "GEOID", "NAME")
    elif sys.argv[1] == "ptos":
        aggregate_state_data()