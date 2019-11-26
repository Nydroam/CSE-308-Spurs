import json
from csv import reader
import pickle
import re
from multiprocessing import Pool
from fuzzywuzzy import process


def load_data():
    prec_names = []
    prec_ids = []
    with open("2018.csv","r") as f:
        for i in reader(f):
            prec_names.append(i[1])
    prec_names = [u.decode('utf-8') for u in prec_names]

    with open("precinct.json","r") as f:
        dat = json.load(f)
        for prec in dat["features"]:
            prec_ids.append(str(prec["properties"]["NAME10"]))

    return prec_names, prec_ids

def clean(x):
    y= x.replace(" township"," township")\
            .replace(" hl "," hills ")\
            .replace("1st","1")\
            .replace("2nd","2")\
            .replace("3rd","3")\
            .replace("4th","4")\
            .replace("5th","5")\
            .replace("w.","west")\
            .replace("n.","north")\
            .replace("e.","east")\
            .replace("s.","south")\
            .replace("#", "")\
            .replace("casl ", "castle ")\
            .replace("mc ", "mc")\
            .replace(" ht", "heights ")\
            .replace(" pk ", " park ")\
            .replace("pct ", "precinct ")\
            .replace(" p-", " precinct ")

    y=re.sub(r"\btwp\b","township",y)
    y=re.sub(r"\bwd\b","ward",y)
    y=re.sub(r"\bwrd\b","ward",y)
    y=re.sub(r"\bw-\b","ward",y)
    y=re.sub(r"\bdst\b","district",y)
    y=re.sub(r"\bdist\b","district",y)
    y=re.sub(r"\bpct\b","precinct",y)

    y=re.sub(r"(\d+)-(\d+)",r"ward \1 precinct\2",y)
    y=re.sub(r"(\d+)~(\d+)",r"philadelphia ward\1 precinct\2",y)

    y=re.sub("^\d+.\d+ ","",y)
    y=re.sub("^\d+ ","",y)
    y=re.sub("0+(?!$)","",y)
    y=re.sub(" i$"," 1",y)
    y=re.sub(" ii$"," 2",y)
    y=re.sub(" iii$"," 3",y)
    y=re.sub(" iv$"," 4",y)
    x=re.sub(r"first ([a-z]+)",r"\1 1",x)
    x=re.sub(r"second ([a-z]+)",r"\1 2",x)

    y=re.sub("^n ","north ",y)
    y=re.sub("^s ","south ",y)
    y=re.sub("^e ","east ",y)
    y=re.sub("^w ","west ",y)
    y=re.sub(" n "," north ",y)
    y=re.sub(" s "," south ",y)
    y=re.sub(" e "," east ",y)
    y=re.sub(" w "," west ",y)
    y=re.sub(r" d(\d+)$",r" district\1",y)
    y=re.sub(" w(\d+)"," ward\1",y)

    return y

old_prec_names = []
prec_names = []
prec_ids = []

def extractor(pid):
    precinct_shape_name = prec_ids[pid]
    matched_name = process.extractOne(precinct_shape_name, prec_names)[0]
    prec_name_index = prec_names.index(matched_name)
    return (pid, precinct_shape_name, prec_name_index, old_prec_names[prec_name_index])

def run_string_mapping():
    global prec_names
    global prec_ids
    global old_prec_names

    with open("tempa","rb") as f:
        old_prec_names = pickle.load(f)
        old_prec_names[0] = ""
    with open("tempb","rb") as f:
        prec_ids = pickle.load(f)
    prec_names=[clean(p.lower()) for p in old_prec_names]
    prec_ids=[clean(p.lower()) for p in prec_ids]

    p = Pool(4)
    matcher = p.map(extractor, range(len(prec_ids)))

    with open("2018_to_precinct","w") as f:
        for key in matcher:
            f.write("" + str(key[0]) + ", " + key[1] +  ", " + str(key[2]) + ", " + key[3] +"\n")

def map_precincts_to_election(prec_names_2018):
    mapping = []
    naming = []

    with open("2018_to_precinct","r") as map_f:
        for line in reader(map_f):
            mapping.append(line[2])
            naming.append(line[1])

    pname_to_votes = {}
    with open("2018.csv","r") as elec_f:
        for prec in reader(elec_f):
            if prec[1] not in pname_to_votes:
                pname_to_votes[prec[1]] = {}
            if "U.S. Senate" in prec[2] or "United States Senator" in prec[2]:
                if prec[5]=='':
                    if prec[4].upper() == "Bob Casey Jr.":
                        prec[5] = "DEM"
                    if prec[4].upper() == "LOU BARLETTA":
                        prec[5] = "REP"
                if prec[5].upper() not in pname_to_votes[prec[1]]:
                    pname_to_votes[prec[1]][prec[5].upper()] = 0
                pname_to_votes[prec[1]][prec[5].upper()] += int(prec[6].replace(",","")) if prec[6] != '' else 0

    ind_to_pname = prec_names_2018

    precincts = {}
    with open("precinct.json","r") as precf:
        precincts = json.load(precf)
        for ind in xrange(len(mapping)):
            from_2018 = mapping[ind]
            elec_results = pname_to_votes[ind_to_pname[int(from_2018)]]
            precincts["features"][ind]["properties"]["SEN18D"]=0
            precincts["features"][ind]["properties"]["SEN18R"]=0
            precincts["features"][ind]["properties"]["SEN18O"]=0
            for key in elec_results:
                if key in ["DEM","DEMOCRATIC","D","DEMOCRAT","DEMOCRATS"]:
                    precincts["features"][ind]["properties"]["SEN18D"] += elec_results[key]
                elif key in ["REP","REPUBLICANS","REPUBLICAN","GOP","R"]:
                    precincts["features"][ind]["properties"]["SEN18R"] += elec_results[key]
                else:
                    precincts["features"][ind]["properties"]["SEN18O"] += elec_results[key]
    with open("precinct_final.json","w") as out:
        json.dump(precincts, out)

if __name__=="__main__":
    prec_names, prec_ids = load_data()
    run_string_mapping()
    map_precincts_to_election(prec_names)