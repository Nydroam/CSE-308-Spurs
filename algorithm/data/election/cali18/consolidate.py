from csv import reader
import json

fields_18 = ["2018_SEN_D", "2018_SEN_R", "2018_SEN_O", "2018_GOV_D", "2018_GOV_R", "2018_GOV_O"]
fields_16 = ["2016_PRES_D", "2016_PRES_R", "2016_PRES_O", "2016_SEN_D", "2016_SEN_R", "2016_SEN_O"]
fields_14 = ["2014_SEN_D", "2014_SEN_R", "2014_SEN_O", "2014_GOV_D", "2014_GOV_R", "2014_GOV_O"]

def load_14():
    blah = 1
    dic = {"CNTYTOT" : {}}
    dat_14 = open("state_g14_sov_data_by_g14_srprec.csv","r")
    dat_14.readline()
    for line in reader(dat_14):
        if not line[5] == "CNTYTOT":
            dic[line[5]] = []
            dic[line[5]].append(int(line[69])+int(line[70]))
            dic[line[5]].append(int(line[71])+int(line[72]))
            dic[line[5]].append(0)

            dic[line[5]].append(int(line[51]))
            dic[line[5]].append(int(line[52]))
            dic[line[5]].append(0)
        else:
            dic["CNTYTOT"][line[2]]=[]
            dic["CNTYTOT"][line[2]].append(int(line[21]))
            dic["CNTYTOT"][line[2]].append(int(line[22]))
            dic["CNTYTOT"][line[2]].append(int(line[23])+int(line[24])+int(line[25])+int(line[26]))
    dat_14.close()
    return dic

def load_16():
    dic = {"CNTYTOT" : {}}
    blah = 1
    dat_16 = open("state_g16_sov_data_by_g16_srprec.csv","r")
    dat_16.readline()
    for line in reader(dat_16):
        if not line[1] == "CNTYTOT":
            dic[line[1]] = []
            dic[line[1]].append(int(line[-11]))
            dic[line[1]].append(int(line[-7]))
            dic[line[1]].append(int(line[-10]) + int(line[-9]) + int(line[-8]))

            dic[line[1]].append(int(line[-6]) + int(line[-5]))
            dic[line[1]].append(int(line[-3]))
            dic[line[1]].append(int(line[-4]))
        else:
            dic["CNTYTOT"][line[0]]=[]
            dic["CNTYTOT"][line[0]].append(int(line[17]))
            dic["CNTYTOT"][line[0]].append(int(line[18]))
            dic["CNTYTOT"][line[0]].append(int(line[22])+int(line[19])+int(line[20])+int(line[21]))
    #print dic["CNTYTOT"]
    dat_16.close()
    return dic

def load_18():
    dic = {"CNTYTOT" : {}}
    blah = 1
    dat_18 = open("state_g18_sov_data_by_g18_srprec.csv","r")
    dat_18.readline()
    for line in reader(dat_18):
        if not line[1] == "CNTYTOT":
            dic[line[1]] = []
            dic[line[1]].append(int(line[-12])+int(line[-11]))
            dic[line[1]].append(int(line[-9]))
            dic[line[1]].append(int(line[-10]))

            dic[line[1]].append(int(line[45]))
            dic[line[1]].append(int(line[46]))
            dic[line[1]].append(0)
        else:
            dic["CNTYTOT"][line[0]]=[]
            dic["CNTYTOT"][line[0]].append(int(line[17]))
            dic["CNTYTOT"][line[0]].append(int(line[18]))
            dic["CNTYTOT"][line[0]].append(int(line[22])+int(line[19])+int(line[20])+int(line[21]))
    dat_18.close()
    return dic

w = load_14()
x = load_16()
y = load_18()

def update(a,b,c):
    success = 0
    missing = 0
    missing_l = []
    missing_l_14 = []
    missing_l_16 = []
    missing_l_18 = []
    r=0
    with open("precinct.json","r") as pdata:
        precincts = json.load(pdata)
        for p in precincts["features"]:
            if p["properties"]["ELECTION"] != "p16":
                r+=1
            success +=1
            if not (p["properties"]["SRPREC"] in a or p["properties"]["SRPREC"] in b or p["properties"]["SRPREC"] in c):
                missing_l.append(p)
                #missing_l.append((p["properties"]["SRPREC"],p["properties"]["SRPREC_KEY"]))
                missing += 1

            if p["properties"]["SRPREC"] in a:
                for i in xrange(len(fields_16)):
                    p["properties"][fields_16[i]] = a[p["properties"]["SRPREC"]][i]
                a.pop(p["properties"]["SRPREC"], None)
            else:
                if p["properties"]["COUNTY"] not in a["CNTYTOT"]:
                    missing_l_16.append(p)

            if p["properties"]["SRPREC"] in b:
                for i in xrange(len(fields_18)):
                    p["properties"][fields_18[i]] = b[p["properties"]["SRPREC"]][i]
                b.pop(p["properties"]["SRPREC"], None)
            else:
                #print p["properties"]["COUNTY"], b["CNTYTOT"]
                if p["properties"]["COUNTY"] not in b["CNTYTOT"]:
                    missing_l_18.append(p)

            if p["properties"]["SRPREC"] in c:
                for i in xrange(len(fields_14)):
                    p["properties"][fields_14[i]] = c[p["properties"]["SRPREC"]][i]
                c.pop(p["properties"]["SRPREC"], None)
            else:
                if p["properties"]["COUNTY"] not in c["CNTYTOT"]:
                    missing_l_14.append(p)
        with open("precinct_with_votes.json","w") as out:
            json.dump(precincts, out)
    print success
    print missing
    print len(missing_l_14)
    print len(missing_l_16)
    print len(missing_l_18)

    with open("tempa","w") as t:
        t.write(str(sorted(missing_l)))
    with open("tempb","w") as t:
        t.write(str(sorted(a.keys())))
    with open("missing.json","w") as t:
        json.dump({"type":"FeatureCollection", "features":missing_l}, t)

update(x,y,w)