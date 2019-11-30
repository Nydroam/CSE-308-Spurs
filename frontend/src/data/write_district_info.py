import json

dists = {}

vote_keys = ['SEN18R','SEN18D','SEN16R','SEN16D','PRES16R','PRES16D']
demo_keys = ['WHITE','AMIN','NHPI','ASIAN','HISP','BLACK']
totpop = 0
with open('pa_precinct_clean.json') as json_file:
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
                totvote+=p[k]
                
        for k in demo_keys:
            if k not in dists[d]:
                dists[d][k] = 0
            if k in p:
                dists[d][k] += p[k]
                totpop+=p[k]
print(totpop)
with open('pa_district_clean.json') as json_file:
    data = json.load(json_file)
    for feature in data['features']:
        p = feature['properties']
        info = dists[p["NAME"]]
        for key in info:
            p[key] = info[key]
    with open('pa_district_new.json','w') as out_file:
        json.dump(data,out_file)
