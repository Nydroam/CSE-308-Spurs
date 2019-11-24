import json

f = open("ACS_17_5YR_B02001.csv","r")
header = f.readline()[:-2].split(",")

data = [line[:-2].split(",") for line in f]
dic = {}
for line in data:
    dic[line[1]]={}
    for h in range(len(header)):
        dic[line[1]][header[h]] = line[h]

print header
print data[0]

def get_dist_json(state, header, dat):
    with open("boundary/%s/district.json"%state, "r") as x:
        district_json = json.load(x)
    for district in district_json["features"]:
        for key in dic[district["properties"]["GEOID"]]:
            district["properties"][key]=dic[district["properties"]["GEOID"]][key]
    with open("race/%s/district.json"%state, "w") as y:
        json.dump(district_json, y)

get_dist_json("ri", header, data)
get_dist_json("ca", header, data)
get_dist_json("pa", header, data)
