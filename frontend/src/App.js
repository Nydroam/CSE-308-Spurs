import React from 'react';
import Map from './components/Map/LeafletMap';
import Sidebar from './components/Sidebar/Sidebar';
import './App.css';
import 'primereact/resources/themes/nova-light/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
const demoMap={
  "AMIN":"American Indian or Alaskan Native",
  "ASIAN":"Asian",
  "BLACK":"Black or African American",
  "NHPI":"Hawaiian or Pacific Islander",
  "HISP":"Hispanic", 
}
class App extends React.Component {

  

  constructor(props){
    super(props);
    this.state = {
      state: null,
      newdistrict:null,
      view: "OD",
      election: "PRES16",
      demo: {},
      properties: {},
      gerrymander: {},
      mmDistricts: {},
      p1data:[],
      newMMdistricts:{},
      newGerrymander:{},
      paprecincts:{},
      caprecincts:{},
      riprecincts:{},
    }
  }

  changeState = (k,val) => {
    this.setState({[k]:val})
  }
  getNewdistricts=() =>{
    let distlist = [];
    let count = 1;
    let seconds = new Date().getTime();
    this.state.p1data.forEach(l =>{
      let newdist = {};
      newdist.WHITE = l.populationByRace.WHITE;
      newdist.ASIAN = l.populationByRace.ASIAN;
      newdist.BLACK = l.populationByRace.BLACK;
      newdist.HISP = l.populationByRace.HISP;
      newdist.AMIN = l.populationByRace.AMIN;
      newdist.NHPI = l.populationByRace.NHPI;
      newdist.NAME = "New District " + count;
      count+=1;
      let s16d = 0;
      let s16r = 0;
      let s18d = 0;
      let s18r = 0;
      let p16d = 0;
      let p16r = 0;
      l.precincts.forEach(e=>{
        e.elections.forEach(f=>{
          if (f.electionKey.electionType === "SEN16"){
            s16d += f.votesByParty[0].numVotes;
            s16r += f.votesByParty[1].numVotes;
          }
          else if (f.electionKey.electionType === "SEN18"){
            s18d += f.votesByParty[0].numVotes;
            s18r += f.votesByParty[1].numVotes;
          }
          else if (f.electionKey.electionType === "PRES16"){

            p16d += f.votesByParty[0].numVotes;
            p16r += f.votesByParty[1].numVotes;
          }
        });
      });
      newdist.SEN16D = s16d;
      newdist.SEN16R = s16r;
      newdist.SEN18D = s18d;
      newdist.SEN18R = s18r;
      newdist.PRES16D = p16d;
      newdist.PRES16R = p16r;
      distlist.push(newdist);
    });
    let newMM =  this.getMMdistricts(distlist);
    console.log("MMdists " + (new Date().getTime()-seconds) + "ms");
    let newGerry = this.calcGerrymander(distlist);
    console.log("GerryMandering " + (new Date().getTime()-seconds) + "ms");
    count = 1;
    newGerry.forEach(e=>{
      e.NAME = "New District " + count;
      count+=1;
    })
    console.log("Added Names " + (new Date().getTime()-seconds) + "ms");
    this.setState({newMMdistricts:newMM,newGerrymander:newGerry});
    console.log("Finished Tables " + (new Date().getTime()-seconds) + "ms");
  }
  
  getMMdistricts=(list) => {
    let MMPdistricts = list.filter(d =>{
      let popMap = Object.keys(demoMap).map(demo => ([demo,d[demo]]))
      let totPop = popMap.map(entry=>entry[1]).reduce( (a,b) => a + b) + d["WHITE"];
      let maxEntry = popMap.reduce( (a,b) => a[1] > b[1] ? a:b);
      let maxPop = maxEntry[1];
      return maxPop >= 0.5 * totPop;
    }).map(d =>{ 
      let maxEntry = Object.keys(demoMap).map(demo => ([demo,d[demo]])).reduce( (a,b) => a[1] > b[1] ? a:b);
      let maxMinority = maxEntry[0];
      return ({
      ...d,
      "maxMinority":maxMinority,
    })})
    return MMPdistricts;
  }
  getOlddistricts=() => {
    this.state.properties.riprecinct.features.forEach(e=>{
      let n = e.properties.NAME;
      this.state.riprecincts[n]=e.properties;
    });
    this.state.properties.paprecinct.features.forEach(e=>{
      let n = e.properties.NAME;
      this.state.paprecincts[n]=e.properties;
    });
    this.state.properties.caprecinct.features.forEach(e=>{
      let n = e.properties.NAME;
      this.state.caprecincts[n]=e.properties;
    });
    let padistricts = [];
    for (let i = 0; i < this.state.properties.padistrict.features.length;i++){
      padistricts.push(this.state.properties.padistrict.features[i].properties);
    }
    let cadistricts = [];
    for (let i = 0; i < this.state.properties.cadistrict.features.length;i++){
      cadistricts.push(this.state.properties.cadistrict.features[i].properties);
    }
    let ridistricts = [];
    for (let i = 0; i < this.state.properties.ridistrict.features.length;i++){
      ridistricts.push(this.state.properties.ridistrict.features[i].properties);
    }
    let MMPdistricts = this.getMMdistricts(padistricts);
    let MMCdistricts = this.getMMdistricts(cadistricts);
    let MMRdistricts = this.getMMdistricts(ridistricts);
    let mmdist = {};
    mmdist.MMPdistricts = MMPdistricts;
    mmdist.MMCdistricts = MMCdistricts;
    mmdist.MMRdistricts = MMRdistricts;
    this.setState({mmDistricts:mmdist});
    let gerryData = {};
    gerryData.pa = this.calcGerrymander(padistricts);
    gerryData.ca = this.calcGerrymander(cadistricts);
    gerryData.ri = this.calcGerrymander(ridistricts);
    let count = 1;
    gerryData.pa.forEach(e =>{
      e["NAME"] = "Congress District " + count;
      count+=1;
    });
    count = 1;
    gerryData.ca.forEach(e =>{
      e["NAME"] = "Congress District " + count;
      count+=1;
    });
    count = 1;
    gerryData.ri.forEach(e =>{
      e["NAME"] = "Congress District " + count;
      count+=1;
    });
    this.setState({gerrymander:gerryData});
  }
  calcGerrymander = (list) => {
    let seconds = new Date().getTime();
    let totDist = list.length;
    let elections = ["SEN16","SEN18","PRES16"];
    let distGerry = [];
    let idealDistrictChange = [];
    elections.forEach(e => {
      let totVot = 0;
      let totGOPvot = 0;
      let totGOPdist = 0;
      let dTag = e + "D";
      let rTag = e + "R";
      list.forEach(element => {
      if (element[dTag] === "No Data"){
        return;
      }
        totVot += element[dTag];
        totVot += element[rTag];
        totGOPvot += element[rTag];
        if (element[rTag] > element[dTag]){
          totGOPdist += 1;
        }
      });
      if (totVot == 0){
        return;
      }
      else{
        idealDistrictChange.push((Math.round(totDist * ((1.0 * totGOPvot) / totVot))) - totGOPdist);
      }
    });
    //For each district, for each election, calculate the idealDistrictChange
    //Calculate the value for result, after storing 3 values in result,
    //Store in distGerry
    list.forEach(l => {
      let results = {};
      let ind = 0;
      elections.forEach(e => {
        if (idealDistrictChange[ind] == 0){
          results[e] = 1.0;
        }
        else{
          let dTag = e + "D";
          let rTag = e + "R";
          //Calc efficiency gap
          let gv = l[rTag];
          let dv = l[dTag];
          let tv = gv+dv;
          let margin = gv-dv;
          if (tv == 0){
            results[e] = 1.0;
          }
          else{
            let win_v = Math.max(gv,dv);
            let loss_v = Math.min(gv,dv);
            let inefficient_v = 0;
            if (idealDistrictChange[ind] * margin > 0){
              inefficient_v =win_v - loss_v;
            }
            else{
              inefficient_v = loss_v;
            }
            let fin = 1.0 - ((inefficient_v*1.0)/tv);
            if (fin == null){
              results[e] = "No Data";
            }
            else{
              fin = fin * 100;
              fin = fin.toFixed(2);
              fin = fin + "%";
              results[e] =fin;
            }
          }
        }
        ind+=1;
        });
      distGerry.push(results);
    });
    return distGerry;
  }
  updateState = (k,val) => {
    this.setState(prevState=>({
      properties:{
        ...prevState.properties,
        [k]:val
      }
    }));
    if (Object.keys(this.state.properties).length == 6){
      this.getOlddistricts();}
  }
  render(){
    return (
      <div className="App flex" >
        <Sidebar election={this.state.election} demo={this.state.demo} state={this.state.state} view={this.state.view} changeState={this.changeState} properties={this.state.properties} gerrymander={this.state.gerrymander} mmDistricts={this.state.mmDistricts} p1data={this.state.p1data} newMMdistricts={this.state.newMMdistricts} newGerrymander={this.state.newGerrymander} getNewdistricts={this.getNewdistricts}></Sidebar>
        <Map election={this.state.election} state={this.state.state} newdistrict={this.state.newdistrict} view={this.state.view} changeState={this.changeState} updateState={this.updateState}></Map>
      </div>
    );
  }
}

export default App;
