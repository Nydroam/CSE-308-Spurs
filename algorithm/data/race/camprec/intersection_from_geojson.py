import json, pickle
from csv import reader
from shapely.geometry import shape

block_races = ["Total","Not Hispanic or Latino","American Indian or Alaska Native Alone","Asian Alone","Black or African American Alone","Native Hawaiian or Other Pacific Islander Alone", "White Alone","American Indian or Alaska Native and White","Asian and White","Black or African American and White","American Indian or Alaska Native and Black or African American","Remainder of Two or More Race Responses","Hispanic or Latino"]
races = ["Total","Hispanic or Latino","American Indian or Alaska Native", "Asian", "Black", "White", "Native Hawaiian or Pacific Islander"]
races_shortform = ["TOTPOP","HISP", "AMIN", "ASIAN", "BLACK", "WHITE", "NHPI"]

def get_races(racelist):
  dem=[]
  dem.append(int(racelist[0][-2]))
  dem.append(int(racelist[12][-2]))
  dem.append(int(racelist[2][-2]) + int(racelist[7][-2]) + int(racelist[10][-2]))
  dem.append(int(racelist[3][-2]) + int(racelist[8][-2]))
  dem.append(int(racelist[4][-2]) + int(racelist[9][-2]) + int(racelist[10][-2]))
  dem.append(int(racelist[6][-2]) + int(racelist[7][-2]) + int(racelist[8][-2]) + int(racelist[9][-2]))
  dem.append(int(racelist[5][-2]))
  return dem


def final_eval(j):
    count=0
    dead_prec = []
    for i in j["features"]:
        if "AMIN" not in i["properties"]:
            count += 1
            dead_prec.append(i)
    print count
    with open("missing_final.json","w") as f:
        json.dump(dead_prec,f)

#block group is on average 39 blocks
def convert_blockgr_to_race(indic=None):
  '''
  geos = {}
  with open("tl_2019_06_bg.json", "r") as blockgeo:
    geos = json.load(blockgeo) #block data
  overlaps = []

  print "begin"

  with open("mprec3/missing.json","r") as x:
    missing = json.load(x)
    for m in missing["features"]: # precinct data
      for g in geos["features"]:
        a = shape(g["geometry"])
        b = shape(m["geometry"]) # precinct without race data
        if a.intersects(b): # 0 - blockgroup # m - precinct
          overlaps.append((g["properties"]["GEOID"],m["properties"]["PREC_KEY"], a.intersection(b).area, 1.0*a.intersection(b).area/a.area))

  with open("temp.json","w") as temp:
    json.dump(overlaps, temp)

  print "halfway"

  racedic={}
  f1 = open("CaliBlockGr.csv", "r")
  racelines=[]
  for line in reader(f1):
    racelines.append(line)
    if len(racelines) == len(block_races):
      blockgr = line[2][line[2].index("US")+2:]
      race_breakdown = get_races(racelines)
      if blockgr in racedic:
        racedic[blockgr] = [racedic[blockgr][i] + race_breakdown[i] for i in xrange(len(races))]
      else:
        racedic[blockgr] = race_breakdown
      racelines = []

  print len(racedic)
  with open("tmp_racedic.json","w") as t:
    json.dump(racedic, t)
  '''
  overlaps=[]
  racedic={}
  with open("temp.json","r") as t:
    overlaps=json.load(t)
  with open("tmp_racedic.json","r") as t:
    racedic=json.load(t)
  #print len(overlaps)
  p_to_race = {}
  print "almost there"
  for f in overlaps:
    #print [f[0],f[1]]
    for key in racedic:
        if f[0] in key:
            #print "match - ", [f[0],f[1],f[3]]
            if f[1] in p_to_race:
                p_to_race[f[1]] = [p_to_race[f[1]][i] + racedic[key][i] * f[3] for i in xrange(len(races))]
            else:
                p_to_race[f[1]] = [racedic[key][i] * f[3] for i in xrange(len(races))]
  #print "hi"
  #print p_to_race
  with open("tmp_final.json","w") as f:
    json.dump(p_to_race,f)


#convert_blockgr_to_race()

def update_json():
    current = {}
    missing = {}
    with open("tmp_final.json","r") as f:
        missing = json.load(f)
    with open("mprec/mprec_with_race.json", "r") as y:
        current = json.load(y)
    for mprec in current["features"]:
        if mprec["properties"]["PREC_KEY"] in missing:
            #print mprec["properties"]["PRECINCT"] in missing
            #print mprec["properties"]["PREC_KEY"] in missing
            for i in xrange(len(races_shortform)):
                mprec["properties"][races_shortform[i]] = int(missing[mprec["properties"]["PREC_KEY"]][i])
            #print mprec
    with open("mprec/mprec_with_race_with_missing.json","w") as f:
        json.dump(current, f)

    final_eval(current)

#update_json()

races_shortform = ["TOTPOP","HISP", "AMIN", "ASIAN", "BLACK", "WHITE", "NHPI"]
def make_consistent():
    districts = {}
    precincts = {}
    with open("district.json","r") as f:
        districts = json.load(f)
    with open("precinct.json","r") as f:
        precincts = json.load(f)
    for d in districts["features"]:
        for r in races_shortform:
            d["properties"][r]=0
    i = 0
    leng = len(precincts["features"])
    for p in precincts["features"]:
        i+=1
        if not (i%100):
            print 1.0*i/leng
        for d in districts["features"]:
            if shape(p["geometry"]).intersects(shape(d["geometry"])):
                for r in races_shortform:
                    d["properties"][r] += p["properties"][r]

    with open("district_from_prec.json","w") as f:
        json.dump(districts,f)

make_consistent()