    import sys, os, json
import loader

#current state:
# Precincts PA and RI -> CD,

def geographic_relationship_graph(dirpath,state):
    district_dat_raw=loader.load_geometry(os.path.join(dirpath,"district.json"))
    district_adjacency, district_dat=loader.load_graph(district_dat_raw["features"], False)

    precinct_dat_raw=loader.load_geometry(os.path.join(dirpath,"precinct.json"))
    if state == "pa":
        for precinct in precinct_dat_raw["features"]:
            precinct["properties"]["NAME"]=precinct["properties"]["NAME10"]
            precinct["properties"]["GEOID"]=precinct["properties"]["GEOID10"]
    precinct_adjacency, precinct_dat=loader.load_graph(precinct_dat_raw["features"])

    contains = {}
    within = {}

    for precinct in precinct_dat:
        intersects=[]
        for district in district_dat:
            if district[1].intersects(precinct[1]):
                intersects.append((district[1].intersection(precinct[1]).area, district))
        top_intersection=max(intersects)
        if top_intersection[1][0] not in contains:
            contains[top_intersection[1][0]]=[]
            contains[top_intersection[1][0]].append(precinct[0])
        within[precinct[0]]=top_intersection[1][0]

    none=[]
    for precinct in precinct_dat_raw["features"]:
        precinct["properties"]["DISTRICT_NAME"]=within[precinct["properties"]["NAME"]]

    # Geography
    with open(os.path.join(dirpath,"district_mapped.json"),"w") as f:
        json.dump(precinct_dat_raw, f)
    # Electoral
    if state == "ri":
        keys=["REGVOT16","TOTVOT16","PRES16D","PRES16R","REGVOT18","TOTVOT18","GOV18D","GOV18R","SEN18D","SEN18R","TOTPOP"]
    elif state == "pa":
        keys=["ATG12D","ATG12R","F2014GOVD","F2014GOVR","GOV10D","GOV10R","PRES12D","PRES12O","PRES12R","SEN10D","SEN10R","T16ATGD","T16ATGR","T16PRESD","T16PRESOTH","T16PRESR","T16SEND","T16SENR","USS12D","USS12R"]
    else:
        keys=[]
    dist_to_votes={}
    for precinct in precinct_dat_raw["features"]:
        dist=precinct["properties"]["DISTRICT_NAME"]
        if dist not in dist_to_votes:
            dist_to_votes[dist]={}
        for key in keys:
            if key not in dist_to_votes[dist]:
                dist_to_votes[dist][key]=0
            dist_to_votes[dist][key] += precinct["properties"][key]
    for district in district_dat_raw["features"]:
        for key in keys:
            district["properties"][key]=dist_to_votes[district["properties"]["NAMELSAD"]]
    #print dist_to_votes
    # Demographic
    if state == "ri":
        keys=["TOTPOP","NH_WHITE","NH_BLACK","NH_AMIN","NH_ASIAN","NH_NHPI","NH_OTHER","NH_2MORE","HISP","VAP","HVAP","WVAP","BVAP","AMINVAP","ASIANVAP","NHPIVAP","OTHERVAP","2MOREVAP"]
    elif state == "pa":
        keys=["NH_WHITE","NH_BLACK","NH_AMIN","NH_ASIAN","NH_NHPI","NH_OTHER","NH_2MORE","HISP","H_WHITE","H_BLACK","H_AMIN","H_ASIAN","H_NHPI","H_OTHER","H_2MORE","VAP","HVAP","WVAP","BVAP","AMINVAP","ASIANVAP","NHPIVAP","OTHERVAP","2MOREVAP"]
    else:
        keys=[]
    dist_to_dem={}
    for precinct in precinct_dat_raw["features"]:
        dist=precinct["properties"]["DISTRICT_NAME"]
        if dist not in dist_to_dem:
            dist_to_dem[dist]={}
        for key in keys:
            if key not in dist_to_dem[dist]:
                dist_to_dem[dist][key]=0
            dist_to_dem[dist][key] += precinct["properties"][key]
    for district in district_dat_raw["features"]:
        for key in keys:
            district["properties"][key]=dist_to_dem[district["properties"]["NAMELSAD"]]
    #print dist_to_dem
    with open(os.path.join("data/combined/%s"%state,"district.json"),"w") as f:
        json.dump(district_dat_raw,f)

    with open(os.path.join("data/combined/%s"%state,"precinct.json"),"w") as f:
        json.dump(precinct_dat_raw,f)

if __name__=="__main__":
    if sys.argv[1].upper() in ["RI","RHODEISLAND","RHODE_ISLAND"]:
        geographic_relationship_graph("data/boundary/ri","ri")
    if sys.argv[1].upper() in ["PA","PENNSYLVANIA","RHODE_ISLAND"]:
        geographic_relationship_graph("data/boundary/pa","pa")
