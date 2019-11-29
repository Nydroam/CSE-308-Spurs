from csv import reader
import pickle
import parse_blockgr
import precinct_races


SRPREC_INDEX = 4
SRPREC_KEY_INDEX = 2
SRPREC_BLOCK_KEY_INDEX = 2
PERCENT_OVERLAP_INDEX = -1
BLOCKS_TO_BLOCKGROUP = 39

block_races = ["Total","Not Hispanic or Latino","American Indian or Alaska Native Alone",\
                "Asian Alone","Black or African American Alone","Native Hawaiian or Other Pacific Islander Alone", \
                "White Alone","American Indian or Alaska Native and White","Asian and White","Black or African American and White", \
                "American Indian or Alaska Native and Black or African American","Remainder of Two or More Race Responses","Hispanic or Latino"]

races = ["Total","Hispanic or Latino","American Indian or Alaska Native", "Asian", "Black", "White", "Native Hawaiian or Pacific Islander"]
race_keys = ["TOTPOP","HISP", "AMIN", "ASIAN", "BLACK", "WHITE", "NHPI"]

def get_percent_overlap(pct):
  return pct[PERCENT_OVERLAP_INDEX]/100/BLOCKS_TO_BLOCKGROUP

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

#block group is on average 39 blocks
def convert_blockgr_to_race(indic=None):
  f1 = open("CaliBlockGr.csv", "r")
  f2 = open("state_p16_sr_blk_map.csv", "r")
  outf1 = open("srprec_to_race.csv","w")
  racelines=[]
  count = {}
  for line in reader(f1):
    racelines.append(line)
    if len(racelines) == len(block_races):
      blockgr = line[SRPREC_KEY_INDEX][line[SRPREC_KEY_INDEX].index("US")+2:]
      f2.seek(0)
      race_breakdown = get_races(racelines)
      for pct in reader(f2):
        if blockgr in pct[SRPREC_BLOCK_KEY_INDEX][:12]:
          if pct[SRPREC_INDEX] not in count:
            count[pct[SRPREC_INDEX]] = [ get_percent_overlap(pct) * race_breakdown[i] for i in xrange(len(race_breakdown)) ]
          else:
            count[pct[SRPREC_INDEX]] = [ count[pct[SRPREC_INDEX]][i] + get_percent_overlap(pct) * race_breakdown[i] for i in xrange(len(race_breakdown)) ]
      racelines=[]
  f1.close()
  f2.close()
  outf1.close()

def isolate_cali_blockdata():
  f1 = open("BlockGr.csv","r")
  headers = f1.readline().split()
  f2 = open("CaliBlockGr.csv","w")
  done_with_cali = False
  for l in f1:
    if "California" in l:
      f2.write(l)
      done_with_cali = True
    else:
      if done_with_cali:
        break
  f1.close()
  f2.close()

def get_race_breakdown():
    precinct_to_race={}
    with open("srprec_to_race.csv","r") as f:
        for line in f:
            total +=1
            data = line[:-1].split(",")
            precinct_to_race[data[0]]={}
            for ind in xrange(len(race_keys)):
                precinct_to_race[data[0]][race_keys[ind]] = int(float(data[1+ind]))
    return precinct_to_race

def get_dist_json():
    with open("srprec_state_p16_v01.json", "r") as x:
        prec_json = json.load(x)
    for prec in prec_json["features"]:
        if prec["properties"]["SRPREC_KEY"] not in precinct_to_race:
            failures["features"].append(prec)
            for key in race_keys:
                prec["properties"][key]=0
        else:
            for key in precinct_to_race[prec["properties"]["SRPREC_KEY"]]:
                prec["properties"][key]=precinct_to_race[prec["properties"]["SRPREC_KEY"]][key]
    with open("srprec_with_race.json", "w") as y:
        json.dump(prec_json, y)

if __name__ == "__main__":
  isolate_cali_blockdata()
  convert_blockgr_to_race()
  precinct_to_race = get_race_breakdown()
  get_dist_json(precinct_to_race)