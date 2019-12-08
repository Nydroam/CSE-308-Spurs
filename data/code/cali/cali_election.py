from csv import reader
import json

PRECINCT_ID_INDEX = 5

fields_18 = ["SEN18D", "SEN18R", "SEN18O", "GOV18D", "GOV18R", "GOV18O"]
fields_16 = ["PRES16D", "PRES16R", "PRES16O", "SEN16D", "SEN16R", "SEN16O"]
fields_14 = ["SEN14D", "SEN14R", "SEN14O", "GOV14D", "GOV14R", "GOV14O"]

def load_elections(file, dem1_keys, rep1_keys, oth1_keys):
    prec_to_votes = {}
    data = open(file,"r")
    datafile.readline()
    for line in reader(datafile):
        precname = line[PRECINCT_NAME_INDEX]
        if not precname == "CNTYTOT":
            prec_to_votes[precname] = []
            prec_to_votes[precname].append(sum([line[i] for i in dem1_keys]+[0]))
            prec_to_votes[precname].append(sum([line[i] for i in rep1_keys]+[0]))
            prec_to_votes[precname].append(sum([line[i] for i in oth1_keys]+[0]))

            prec_to_votes[precname].append(sum([line[i] for i in dem2_keys]+[0]))
            prec_to_votes[precname].append(sum([line[i] for i in rep2_keys]+[0]))
            prec_to_votes[precname].append(sum([line[i] for i in oth2_keys]+[0]))
    datafile.close()
    return prec_to_votes

def update(election_data, fields):
    success = 0
    missing_data = []
    with open("precinct.json","r") as pdata:
        precincts = json.load(pdata)
        for p in precincts["features"]:
            if p["properties"]["SRPREC"] in a:
                success += 1
                for i in xrange(len(fields)):
                    p["properties"][fields[i]] = a[p["properties"]["SRPREC"]][i]
                a.pop(p["properties"]["SRPREC"], None)
            else:
                missing_data.append(p)
        with open("precinct_with_votes.json","w") as out:
            json.dump(precincts, out)

    with open("missing.json","w") as t:
        json.dump({"type":"FeatureCollection", "features":missing_l}, t)
    return success, missing_data

if __name__ == "__main__":
    elec_14 = load("state_g14_sov_data_by_g14_srprec.csv",[69,70], [71,72], [], [51], [52], [])
    elec_16 = load("state_g14_sov_data_by_g16_srprec.csv",[-11], [-7], [-10,-9,-8], [-6,-5], [-3], [-4])
    elec_18 = load("state_g14_sov_data_by_g18_srprec.csv",[-12,-11], [-9], [-10], [45], [46], [])
    successes, failures = update(elec_14, fields_14)
    print "" + str(successes) + " successes"
    print "" + str(len(failures)) + " failures for 2014 data"
    successes, failures = update(elec_16, fields_16)
    print "" + str(successes) + " successes"
    print "" + str(len(failures)) + " failures for 2016 data"
    successes, failures = update(elec_18, fields_18)
    print "" + str(successes) + " successes"
    print "" + str(len(failures)) + " failures for 2018 data"