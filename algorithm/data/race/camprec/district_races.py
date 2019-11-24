import json
from csv import reader

races = ["TOTPOP","HISP", "AMIN", "ASIAN", "BLACK", "WHITE", "NHPI"]

racedic={}

total=0

with open("mprec_to_race.csv","r") as f:
    for datum in f:
        total +=1
        dat = datum[:-1].split(",")
        racedic[dat[0]]={}
        for ind in xrange(len(races)):
            racedic[dat[0]][races[ind]] = int(float(dat[1+ind]))

def get_dist_json():
    successes=0
    fail=0
    failures = {"features":[]}
    with open("mprec3/mprec_state_g16_v01.json", "r") as x:
    #with open("mprec2/mprec_state_g18_v01.json", "r") as x:
    #with open("mprec/mprec_state_g12_v01.json", "r") as x:
    #with open("mprec1/mprec_g10.json", "r") as x:
        prec_json = json.load(x)
    for prec in prec_json["features"]:
        if prec["properties"]["PRECINCT"] not in racedic:
            fail +=1
            failures["features"].append(prec)
            for key in races:
                prec["properties"][key]=0
        else:
            successes +=1
            for key in racedic[prec["properties"]["PRECINCT"]]:
                prec["properties"][key]=racedic[prec["properties"]["PRECINCT"]][key]
    with open("mprec/mprec_with_race.json", "w") as y:
        json.dump(prec_json, y)
    with open("mprec3/missing.json", "w") as z:
        json.dump(failures, z)
    return successes, fail

successes, fail = get_dist_json()

print "total: " + str(total)
print "successes: " + str(successes)
print "fail: " + str(fail)