import json



vote_keys = ['SEN18R','SEN18D','SEN18O','SEN16R','SEN16D','SEN16O','PRES16O','PRES16R','PRES16D']
demo_keys = ['WHITE','AMIN','NHPI','ASIAN','HISP','BLACK']

states = ['pa','ca','ri']
totpop = 0
for state in states:
    dists = {}
    with open(state+'_precinct_clean.json') as json_file:
        data = json.load(json_file)
        print(len(data['features']))
        for feature in data['features']:
            p = feature['properties']
            d = p['DISTRICT']
            if d not in dists:
                dists[d] = {}
            
            for k in vote_keys:
                if k not in dists[d]:
                    dists[d][k] = 0
                if k in p:
                    dists[d][k] += p[k]
                    
            for k in demo_keys:
                if k not in dists[d]:
                    dists[d][k] = 0
                if k in p:
                    dists[d][k] += p[k]
                    totpop+=p[k]
    print(totpop)
    with open(state+'_district_clean.json') as json_file:
        data = json.load(json_file)
        for feature in data['features']:
            p = feature['properties']
            info = dists[p["NAME"]]
            for key in info:
                p[key] = info[key]
        with open(state+'_district_clean.json','w') as out_file:
            json.dump(data,out_file)
