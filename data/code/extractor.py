import sys
import json

RHODE_ISLAND = 0
PENNSYLVANIA = 1
CALIFORNIA = 2

state_codes = {"RHODE ISLAND": "40", "PENNSYLVANIA": "39", "CALIFORNIA": "05"}

# for PA, RI - DISTRICT_NAME is already a property
def cleaner(state, precincts, districts):
    if state is RHODE_ISLAND:
        for district in districts:
            district["properties"]["NAME"] = district["properties"]["NAMELSAD"]
            district["properties"]["geoid"] = district["properties"]["GEOID"]
            district["properties"]["STATE"] = "RHODE ISLAND"
            district["properties"]["state_id"] = state_codes[district["properties"]["STATE"]]
        for precinct in precincts:
            precinct["properties"]["NAME"] = precinct["properties"]["PRENAME"]
            precinct["properties"]["STATE"] = "RHODE ISLAND"
            precinct["properties"]["state_id"] = state_codes[precinct["properties"]["STATE"]]
    elif state is CALIFORNIA:
        for district in districts:
            district["properties"]["NAME"] = district["properties"]["NAMELSAD"]
            district["properties"]["geoid"] = district["properties"]["GEO.id2"]
            district["properties"]["STATE"] = "CALIFORNIA"
            district["properties"]["state_id"] = state_codes[district["properties"]["STATE"]]
        for precinct in precincts:
            precinct["properties"]["NAME"] = "SRPREC " + precinct["properties"]["SRPREC"]
            precinct["properties"]["STATE"] = "CALIFORNIA"
            precinct["properties"]["state_id"] = state_codes[precinct["properties"]["STATE"]]
    else:
        for district in districts:
            district["properties"]["NAME"] = district["properties"]["NAMELSAD"]
            district["properties"]["geoid"] = district["properties"]["GEOID"]
            district["properties"]["STATE"] = "PENNSYLVANIA"
            district["properties"]["state_id"] = state_codes[district["properties"]["STATE"]]
        for precinct in precincts:
            precinct["properties"]["STATE"] = "PENNSYLVANIA"
            precinct["properties"]["state_id"] = state_codes[precinct["properties"]["STATE"]]
            precinct["properties"]["PRES16D"] = precinct["properties"]["T16PRESD"]
            precinct["properties"]["PRES16R"] = precinct["properties"]["T16PRESR"]
            precinct["properties"]["PRES16O"] = precinct["properties"]["T16PRESOTH"]

    if state in [RHODE_ISLAND, PENNSYLVANIA]:
        for precinct in precincts:
            precinct["properties"]["DISTRICT"] = precinct["properties"]["DISTRICT_NAME"]
    else:
        for precinct in precincts:
            precinct["properties"]["DISTRICT"] = 0

def mapper(precincts, districts):
    for p in precincts:
        for d in districts:
            if p["properties"]["DISTRICT"] == d["properties"]["NAME"]:
                p["properties"]["district_geoid"] = d["properties"]["geoid"]

def dic_requires(dic, num_keys, str_keys):
    failures = []
    for key in dic["properties"]:
        if key not in num_keys and key not in str_keys:
            failures.append(key)
        if key in num_keys:
            dic["properties"][key] = int(float(dic["properties"][key]))
    for i in failures:
        dic["properties"].pop(i)

def separate(dic, race_keys, elec_keys, misc_keys):
    elec_types = {}
    elec_types["SEN18"] = ["SEN18D", "SEN18R", "SEN18O"]
    elec_types["GOV18"] = ["GOV18D", "GOV18R", "GOV18O"]
    elec_types["PRES16"] = ["PRES16D", "PRES16R", "PRES16O"]
    elec_types["SEN16"] = ["SEN16D", "SEN16R", "SEN16O"]
    elec_types["SEN14"] = ["SEN14D", "SEN14R", "SEN14O"]
    elec_types["GOV14"] = ["GOV14D", "GOV14R", "GOV14O"]

    extracted = []
    census = {"precinct_id": dic["properties"]["NAME"]}
    elections = [ {"precinct_id": dic["properties"]["NAME"], "type": key} for key in elec_types ]
    for i in dic["properties"]:
        if i not in misc_keys:
            extracted.append(i)
        if i in race_keys:
            census[i] = dic["properties"][i]
        if i in elec_keys:
            for key in elec_types:
                if i in elec_types[key]:
                    for e in elections:
                        if e["type"] == key:
                            e[i] = dic["properties"][i]
    for i in extracted:
        dic["properties"].pop(i)
    return census, elections

if __name__ == "__main__":
    state = ["ri","pa","ca"].index(sys.argv[1])

    #DISTRICT LEVEL
    num_keys = []
    str_keys = ["STATE", "state_id", "NAME", "geoid"]
    with open(sys.argv[2]+"/district.json", "r") as f:
        d_dat = json.load(f)

    cleaner(state, [], d_dat["features"])
    for d in d_dat["features"]:
        dic_requires(d, num_keys, str_keys)

    with open(sys.argv[2]+"/district_clean.json", "w") as f:
        json.dump(d_dat, f)

    #PRECINCT LEVEL
    race_keys = ["WHITE","BLACK","ASIAN","NHPI","AMIN","HISP"]
    elec_keys = ["SEN18D", "SEN18R", "SEN18O", "GOV18D", "GOV18R", "GOV18O"] + \
                ["PRES16D", "PRES16R", "PRES16O", "SEN16D", "SEN16R", "SEN16O"] + \
                ["SEN14D", "SEN14R", "SEN14O", "GOV14D", "GOV14R", "GOV14O"]
    misc_keys = ["NAME", "DISTRICT", "STATE", "state_id", "district_geoid"]

    elections = []
    censuses = []

    with open(sys.argv[2]+"/precinct.json", "r") as f:
        dat = json.load(f)

    cleaner(state, dat["features"], [])

    mapper(dat["features"], d_dat["features"])

    for d in dat["features"]:
        dic_requires(d, race_keys + elec_keys, misc_keys)

    with open(sys.argv[2]+"/precinct_clean.json", "w") as f:
        json.dump(dat, f)

    for d in dat["features"]:
        c,e = separate(d, race_keys, elec_keys, misc_keys)
        censuses.append(c)
        elections+=e

    with open(sys.argv[2]+"/precinct_sparse.json", "w") as f:
        json.dump(dat, f)
    with open(sys.argv[2]+"/census.json", "w") as f:
        json.dump(censuses, f)
    with open(sys.argv[2]+"/elections.json", "w") as f:
        json.dump(elections, f)
