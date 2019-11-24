import json

keys_ri=["TOTPOP","NH_WHITE","NH_BLACK","NH_AMIN","NH_ASIAN","NH_NHPI","NH_OTHER","NH_2MORE","HISP","VAP","HVAP","WVAP","BVAP","AMINVAP","ASIANVAP","NHPIVAP","OTHERVAP","2MOREVAP"]
keys_pa=["NH_WHITE","NH_BLACK","NH_AMIN","NH_ASIAN","NH_NHPI","NH_OTHER","NH_2MORE","HISP","H_WHITE","H_BLACK","H_AMIN","H_ASIAN","H_NHPI","H_OTHER","H_2MORE","VAP","HVAP","WVAP","BVAP","AMINVAP","ASIANVAP","NHPIVAP","OTHERVAP","2MOREVAP"]

races_shortform = ["TOTPOP","HISP", "AMIN", "ASIAN", "BLACK", "WHITE", "NHPI"]

#pa
with open("ri/district.json","r") as f:
    districts = json.load(f)
    for d in districts["features"]:
        d["properties"]["TOTPOP"]=d["properties"]["VAP"]
        d["properties"]["HISP"]=d["properties"]["HVAP"]
        d["properties"]["AMIN"]=d["properties"]["AMINVAP"]
        d["properties"]["ASIAN"]=d["properties"]["ASIANVAP"]
        d["properties"]["BLACK"]=d["properties"]["BVAP"]
        d["properties"]["WHITE"]=d["properties"]["WVAP"]
        d["properties"]["NHPI"]=d["properties"]["NHPIVAP"]

    with open("ri_cvap/district.json","w") as g:
        json.dump(districts,g)

with open("ri/precinct.json","r") as f:
    districts = json.load(f)
    for d in districts["features"]:
        d["properties"]["TOTPOP"]=d["properties"]["VAP"]
        d["properties"]["HISP"]=d["properties"]["HVAP"]
        d["properties"]["AMIN"]=d["properties"]["AMINVAP"]
        d["properties"]["ASIAN"]=d["properties"]["ASIANVAP"]
        d["properties"]["BLACK"]=d["properties"]["BVAP"]
        d["properties"]["WHITE"]=d["properties"]["WVAP"]
        d["properties"]["NHPI"]=d["properties"]["NHPIVAP"]

    with open("ri_cvap/precinct.json","w") as g:
        json.dump(districts,g)

#pa
with open("pa/district.json","r") as f:
    districts = json.load(f)
    for d in districts["features"]:
        d["properties"]["TOTPOP"]=d["properties"]["VAP"]
        d["properties"]["HISP"]=d["properties"]["HVAP"]
        d["properties"]["AMIN"]=d["properties"]["AMINVAP"]
        d["properties"]["ASIAN"]=d["properties"]["ASIANVAP"]
        d["properties"]["BLACK"]=d["properties"]["BVAP"]
        d["properties"]["WHITE"]=d["properties"]["WVAP"]
        d["properties"]["NHPI"]=d["properties"]["NHPIVAP"]

    with open("pa_cvap/district.json","w") as g:
        json.dump(districts,g)

with open("pa/precinct.json","r") as f:
    districts = json.load(f)
    for d in districts["features"]:
        d["properties"]["TOTPOP"]=d["properties"]["VAP"]
        d["properties"]["HISP"]=d["properties"]["HVAP"]
        d["properties"]["AMIN"]=d["properties"]["AMINVAP"]
        d["properties"]["ASIAN"]=d["properties"]["ASIANVAP"]
        d["properties"]["BLACK"]=d["properties"]["BVAP"]
        d["properties"]["WHITE"]=d["properties"]["WVAP"]
        d["properties"]["NHPI"]=d["properties"]["NHPIVAP"]
    with open("pa_cvap/precinct.json","w") as g:
        json.dump(districts,g)
