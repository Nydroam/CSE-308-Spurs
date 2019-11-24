import json
from shapely.geometry import shape, GeometryCollection
import sys

def run():
    with open(sys.argv[1], "r") as precinct_f:
        precincts = json.load(precinct_f)
    with open(sys.argv[2], "r") as district_f:
        districts = json.load(district_f)
    dist_shapes = [(d["properties"]["geoid"], d["properties"]["NAME"], shape(d["geometry"])) for d in districts["features"]]

    mapping={}

    l = len(precincts["features"])
    i = 0
    for p in precincts["features"]:
        max_inter = (0, None)
        for d in dist_shapes:
            p_shape = shape(p["geometry"])
            if p_shape.intersects(d[2]) and p_shape.intersection(d[2]).area > max_inter[0]:
                max_inter = (p_shape.intersection(d[2]).area, d)

        d = max_inter[1]
        p["properties"]["DISTRICT"] = d[1]
        p["properties"]["district_geoid"] = d[0]

        mapping[p["properties"]["NAME"]] = d[0]

        i+=1
        if i % 100 == 0:
            print "" + str(i) + "/" + str(l) + ", " + str(1.0 * i / load)

    with open("precincts_with_district.json", "w") as f:
        json.dump(precincts, f)
    with open("mapfile.json", "w") as f:
        json.dump(mapping, f)


if __name__ == "__main__":
    run()