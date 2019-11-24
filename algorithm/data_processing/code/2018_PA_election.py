import json
from csv import reader
import pickle
import re
from multiprocessing import Pool
from fuzzywuzzy import process


def loader():
    prec_names = []
    prec_ids = []
    with open("2018.csv","r") as f:
        for i in reader(f):
            if i[1] not in prec_names:
                prec_names.append(i[1])
    prec_names = [u.decode('utf-8') for u in prec_names]


    with open("precinct.json","r") as f:
        dat = json.load(f)
        for prec in dat["features"]:
            if prec["properties"]["NAME10"] not in prec_ids:
                prec_ids.append(str(prec["properties"]["NAME10"]))

    with open("tempa","wb") as f:
        pickle.dump(prec_names, f)
    with open("tempb","wb") as f:
        pickle.dump(prec_ids, f)

def clean(x):

    x=re.sub(r"first ([a-z]+)",r"\1 1",x)
    x=re.sub(r"second ([a-z]+)",r"\1 2",x)

    y= x.replace(" township"," twp")\
            .replace(" voting district","")\
            .replace(" hl "," hills ")\
            .replace(" district "," district")\
            .replace(" dist "," district")\
            .replace(" twp"," ")\
            .replace(" vtd","")\
            .replace(" 1st","1")\
            .replace(" 2nd","2")\
            .replace(" 3rd","3")\
            .replace(" 4th","4")\
            .replace(" 5th","5")\
            .replace("--"," ")\
            .replace(" -"," ")\
            .replace("- "," ")\
            .replace("w.","west")\
            .replace("n.","north")\
            .replace("e.","east")\
            .replace("s.","south")\
            .replace(" wd ", " ward")\
            .replace(" wrd ", " ward")\
            .replace(" ward ", " ward")\
            .replace(" w-", " ward")\
            .replace("#", "")\
            .replace(" br ", " ")\
            .replace("casl ", "castle ")\
            .replace("mc ", "mc")\
            .replace("no.", "")\
            .replace(" ht", "heights")\
            .replace(" tp ", " ")\
            .replace(" pk ", " park ")\
            .replace("pct ", "precinct")\
            .replace(" p-", " precinct")#\
#            .replace("borough","")\
#            .replace("boro","")\
#            .replace("mt.", "")\
#            .replace("mckees","mc kees")\
#            .replace("mt oliver", "oliver")\
#            .replace("o hara", "ohara")\
#            .replace("mc sherrystown", "mcsherrystown")

    a = re.findall("(\d+)-(\d+)",y)
    if len(a)>0:
        a=a[0]
        y = y.replace( re.findall("\d-\d",y)[0], " ward"+a[0]+" precinct"+a[1] )
    y=re.sub(r"(\d+)~(\d+)",r"philadelphia ward\1 precinct\2",y)

    y=re.sub("^\d+.\d+ ","",y)
    y=re.sub("^\d+ ","",y)

    y=re.sub("0+(?!$)","",y)
    y=re.sub(" i$"," 1",y)
    y=re.sub(" ii$"," 2",y)
    y=re.sub(" iii$"," 3",y)
    y=re.sub(" iv$"," 4",y)

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

    y=y.replace(" district ","")
    y=y.replace(" ward ","")
    y=y.replace(" boro ","")
    y=y.replace(" borough ","")
    y=re.sub(" district$","",y)

    return y

old_prec_names = []
prec_names = []
prec_ids = []
l = 0
def extractor(pid):
    a = prec_ids[pid]
    b = process.extractOne(a, prec_names)[0]
    c = prec_names.index(b)
    print 1.0*pid/l
    return (pid, a, c, old_prec_names[c])

def run_fuzzy_wuzzy():
    global prec_names
    global prec_ids
    global l
    global old_prec_names

    with open("tempa","rb") as f:
        old_prec_names = pickle.load(f)
    with open("tempb","rb") as f:
        prec_ids = pickle.load(f)
    prec_names=[clean(p.lower()) for p in old_prec_names]
    prec_ids=[clean(p.lower()) for p in prec_ids]

    print "NAMES:", len(prec_names)
    print "IDS:", len(prec_ids)

    l = len(prec_ids)

    p = Pool(4)
    matcher = p.map(extractor, range(len(prec_ids)))

    #for pid in prec_ids:
        #matcher[pid] = old_prec_names[prec_names.index(process.extractOne(pid, prec_names)[0])]

    print "NAMES:", len(prec_names)
    print "IDS:", len(prec_ids)

    with open("out_fuzz","w") as f:
        for key in matcher:
            f.write("" + str(key[0]) + ", " + key[1] +  ", " + str(key[2]) + ", " + key[3] +"\n")

def run():
    with open("tempa","rb") as f:
        prec_names = pickle.load(f)
    with open("tempb","rb") as f:
        prec_ids = pickle.load(f)
    prec_names=[set(clean(p.lower()).split()) for p in prec_names]
    prec_ids=[set(clean(p.lower()).split()) for p in prec_ids]

    print "NAMES:", len(prec_names)
    print "IDS:", len(prec_ids)

    temp = []
    for name in prec_ids:
        if name in prec_names:
            prec_ids.remove(name)
            prec_names.remove(name)
        else:
            temp.append(name)
    prec_ids = temp

    print "NAMES:", len(prec_names)
    print "IDS:", len(prec_ids)

def map_precs():
    mapping = []
    with open("out_fuzz","r") as map_f:
        for line in reader(map_f):
            mapping.append(line[2])
    pname_to_votes = {}
    with open("2018.csv","r") as elec_f:
        for prec in reader(elec_f):
            if prec[1] not in pname_to_votes:
                pname_to_votes[prec[1]] = {}
            if "Senate" in prec[2]:
                if prec[5].upper() not in pname_to_votes[prec[1]]:
                    pname_to_votes[prec[1]][prec[5].upper()] = 0
                pname_to_votes[prec[1]][prec[5].upper()] += int(prec[6].replace(",","")) if prec[6] != '' else 0
    ind_to_pname = []
    with open("tempa","rb") as ind_to_pname_f:
        ind_to_pname = pickle.load(ind_to_pname_f)
    ind_to_pname = [p.replace("Philadelphia ","") for p in ind_to_pname]

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
    print precincts["features"][0]["properties"]
    with open("precinct_final.json","w") as out:
        json.dump(precincts, out)

if __name__=="__main__":
    #loader()
    #run()
    #run_fuzzy_wuzzy()
    map_precs()