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
    }
  }

  changeState = (k,val) => {
    this.setState({[k]:val})
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
      e["name"] = "Congressional District " + count;
      count+=1;
    });
    count = 1;
    gerryData.ca.forEach(e =>{
      e["name"] = "Congressional District " + count;
      count+=1;
    });
    count = 1;
    gerryData.ri.forEach(e =>{
      e["name"] = "Congressional District " + count;
      count+=1;
    });
    console.log(gerryData);
    this.setState({gerrymander:gerryData});
  }
  calcGerrymander = (list) => {
    let totDist = list.length;
    let elections = ["SEN16","SEN18","PRES16"];
    let distGerry = [];
    //For each district, for each election, calculate the idealDistrictChange
    //Calculate the value for result, after storing 3 values in result,
    //Store in distGerry
    list.forEach(l => {
      let results = {};
      elections.forEach(e => {
        let totVot = 0;
        let totGOPvot = 0;
        let totGOPdist = 0;
        let idealDistrictChange=0;
        let dTag = e + "D";
        let rTag = e + "R";
        list.forEach(element => {
          totVot += element[dTag];
          totVot += element[rTag];
          totGOPvot += element[rTag];
          if (element[rTag] > element[dTag]){
            totGOPdist += 1;
          }
        });
        if (totVot == 0){
          results[e] = 0;
        }
        else{
          idealDistrictChange=(Math.round(totDist * ((1.0 * totGOPvot) / totVot))) - totGOPdist;
          if (idealDistrictChange == 0){
            results[e] = 1.0;
          }
          else{
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
              if (idealDistrictChange * margin > 0){
                inefficient_v =win_v - loss_v;
              }
              else{
                inefficient_v = loss_v;
              }
              results[e] = 1.0 - ((inefficient_v*1.0)/tv);
            } 
        }
      }
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
        <Sidebar election={this.state.election} demo={this.state.demo} state={this.state.state} view={this.state.view} changeState={this.changeState} properties={this.state.properties} gerrymander={this.state.gerrymander} mmDistricts={this.state.mmDistricts}></Sidebar>
        <Map election={this.state.election} state={this.state.state} newdistrict={this.state.newdistrict} view={this.state.view} changeState={this.changeState} updateState={this.updateState}></Map>
      </div>
    );
  }
}

export default App;
