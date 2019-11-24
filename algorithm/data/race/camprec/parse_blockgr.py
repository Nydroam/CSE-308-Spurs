from csv import reader
import pickle

block_races = ["Total","Not Hispanic or Latino","American Indian or Alaska Native Alone","Asian Alone","Black or African American Alone","Native Hawaiian or Other Pacific Islander Alone", "White Alone","American Indian or Alaska Native and White","Asian and White","Black or African American and White","American Indian or Alaska Native and Black or African American","Remainder of Two or More Race Responses","Hispanic or Latino"]

races = ["Total","Hispanic or Latino","American Indian or Alaska Native", "Asian", "Black", "White", "Native Hawaiian or Pacific Islander"]

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
  f2 = open("blk_mprec_state_p16_v01.csv", "r")
  outf1 = open("mprec_to_race.csv","w")
  outf2 = open("mprec_to_race_dic.csv","w")
  if not indic:
    racelines=[]
    progress = 0
    count = {}
    for line in reader(f1):
      progress+=1
      if not progress % 100:
        print "progress : "+ str(progress) +"/" + str(301756) + " or " + str((0.0+progress)/301756)
      racelines.append(line)
      if len(racelines) == len(block_races):
        blockgr = line[2][line[2].index("US")+2:]
        f2.seek(0)
        race_breakdown = get_races(racelines)
        for pct in reader(f2):
          if blockgr in pct[6][:12]:
            if pct[5] not in count:
              count[pct[5]] = [ 1.0/39 * float(pct[-1]) * race_breakdown[i] for i in xrange(len(race_breakdown)) ]
            else:
              count[pct[5]] = [ count[pct[5]][i] + 1.0/39 * float(pct[-1]) * race_breakdown[i] for i in xrange(len(race_breakdown)) ]
        racelines=[]
  else:
    count = {}
  for key in count:
    outf1.write(key+","+",".join([str(i) for i in count[key]])+"\n")
  pickle.dump(count, outf2)
  f1.close()
  f2.close()
  outf1.close()
  outf2.close()

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

isolate_cali_blockdata()
convert_blockgr_to_race()
