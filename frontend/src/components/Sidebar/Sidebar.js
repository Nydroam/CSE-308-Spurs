import React from 'react';
import {Dropdown} from 'primereact/dropdown';
import {TabView,TabPanel} from 'primereact/tabview';
import {Fieldset} from 'primereact/fieldset';
import {InputText} from 'primereact/inputtext';
import {Slider} from 'primereact/slider';
import {Button} from 'primereact/button';
import {ListBox} from 'primereact/listbox';
import {Checkbox} from 'primereact/checkbox';
import {ProgressBar} from 'primereact/progressbar';
import {Dialog} from 'primereact/dialog';
import {ProgressSpinner} from 'primereact/progressspinner';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {Accordion,AccordionTab} from 'primereact/accordion';

import * as reps from './Representatives.json';
import './Sidebar.css';
const states=[
    {label:"California", value:"CA"},
    {label:"Pennsylvania", value:"PA"},
    {label:"Rhode Island", value:"RI"},
]
const precinctNumbers={
    "CA": 25000,
    "RI": 419,
    "PA": 9255,
}
const statesMap = {
    "RI":40,
    "PA":39,
    "CA":5,
}
const tables = [
    {label:"Majority-Minority Districts",value:"Majority-Minority Districts"},
    {label:"Partisan Fairness",value:"Partisan Fairness"},
]
const views=[
    {label:"Original Districts", value:"OD"},
    {label:"Voting Precincts", value:"VP"},
    {label:"New Districts", value:"ND"},
]
const ethnics= [
    {label:"American Indian/Alaskan Native", value:"AMIN"},
    {label:"Asian", value:"ASIAN"},
    {label:"Black or African American", value:"BLACK"},
    {label:"Hispanic", value:"HISP"},
    {label:"Hawaiian/Pacific Islander", value:"NHPI"},
]
const elections=[
    {label:"2016 Presidential Election", value: "PRES16"},
    {label:"2016 Congressional Election", value: "SEN16"},
    {label:"2018 Congressional Election", value: "SEN18"}
]
const electionMap={
    "PRES16":"PRESIDENTIAL2016",
    "SEN16":"SENATE2016",
    "SEN18":"SENATE2018",
}
const demoMap={
    "AMIN":"American Indian or Alaskan Native",
    "ASIAN":"Asian",
    "BLACK":"Black or African American",
    "NHPI":"Hawaiian or Pacific Islander",
    "HISP":"Hispanic",
    "WHITE":"White",   
}
const partyMap={
    "R":"Republican",
    "D":"Democratic",
    "O":"Other",
}
class Sidebar extends React.PureComponent{
   
    constructor(){
        super()
        this.state = {
            distNum: 0,
            mDistNum: 0,
            voteThresh: 50,
            popThresh: 50,
            resultInfo: null,
            ethnic: null,
            rangeValues:[0,100],
            tab:0,
            election: "2016P",
            allowStep : false,
            running : false,
            phase0Summary : null,
            rules: false,
            phase0loading: false,
            phase0Data : null,
            phase0Visible:false,
            numSteps:1,
            tableView:"Partisan Fairness",
            compactnessWeight:1,
            fairnessWeight:1,
            countyWeight:1,
            mmWeight:1,
            populationWeight:1,
        }
    }

    convertNumber = (num) => {
        let result = "";
        let counter = 0;
        if (num === 0)
            return 0;
        while(num > 0){
            if(counter%3===0 && counter > 0)
                result = "," + result;
            result = num%10 + result;
            num = Math.floor(num/10);
            counter+=1;
        }
        return result;
    }

    onChangeSlider = (name, e) => {
        let newValue;

        if (e.target) {
            newValue = e.target.value;
        }
        else {
            newValue = e.value;
        }
        this.setState({ [name]: newValue });
    }
    
    onSubmitPhase0 = () => {
        let seconds = new Date().getTime();
        this.setState({phase0loading:true});
        fetch("http://localhost:8080/spurs/state/runPhase0",
         {method:"POST", body: JSON.stringify(
             {stateId: statesMap[this.props.state],
            electionType: this.props.election,
            raceThresh:this.state.popThresh/100,
            voteThresh:this.state.voteThresh/100})}
         ).then( (res) => res.json())
         .then( (data) => { console.log("Fetching took " + (new Date().getTime()-seconds) + "ms");
         console.log(data);this.parsePhase0(data);})
         .catch( (err) => { console.log("Fetching took " + (new Date().getTime()-seconds) + "ms");
         console.log(err); this.setState({blockInfo:"Data Retrieval Failed",loading:false});});
       
    }
    parsePhase0 = (data) => {
        let phase0sum = {};
        phase0sum['eligibleP'] = data.length;
        phase0sum['WHITE']=0;
        phase0sum['AMIN']=0;
        phase0sum['HISP']=0;
        phase0sum['NHPI']=0;
        phase0sum['ASIAN']=0;
        phase0sum['BLACK']=0;
        phase0sum['totalP'] = precinctNumbers[this.props.state];
        let phase0data= [];
        for(let i = 0; i < data.length; i++){
            let item = {};
            item['name'] = data[i]['precinct']['name'];
            item['popPercent'] = data[i]['demographic']['population']/data[i]['precinct']['population'];
            item['demographic'] = data[i]['demographic']['demographicKey']['race'];
            item['demographicPop'] = data[i]['demographic']['population'];
            item['party'] = data[i]['party'];
            item['partyVotes'] = data[i]['votes'];
            item['votePercent'] = data[i]['votes']/data[i]['totalVotes'];
            phase0sum[data[i]['demographic']['demographicKey']['race']] += 1;
            phase0data.push(item);
        }
        this.setState({phase0Summary:phase0sum,phase0Data:phase0data,phase0loading:false});
    }
    onSubmitPhase1 = () => {
        this.props.changeState("view","ND")
        let seconds = new Date().getTime();
        
        this.setState({running:true,phase1Done:false});
        fetch("http://localhost:8080/spurs/state/runPhase1",
             {method:"POST", body: JSON.stringify(
                {stateId:statesMap[this.props.state],
                mDistNum:this.state.mDistNum,
                distNum:this.state.distNum,
                races:this.state.ethnic,
                rangeMin:this.state.rangeValues[0],
                rangeMax:this.state.rangeValues[1],
                step:this.state.allowStep,
                numSteps:this.state.numSteps,
            weights:{
                countyWeight:this.state.countyWeight,
                populationWeight: this.state.populationWeight,
                fairnessWeight: this.state.fairnessWeight,
                compactnessWeight: this.state.compactnessWeight,
                mmWeight: this.state.mmWeight,
            }})}
         ).then( (res) => res.json())
         
         .then( (data) => {
         console.log("Fetching took " + (new Date().getTime()-seconds) + "ms");
         if(data){
             
             if(this.state.allowStep){
                if(data["finished"]){
                    this.setState({phase1Done:true})
                }
                data = data["results"];
             }else{
                 this.setState({phase1Done:true})
             }
             this.props.changeState("p1data",data);
             this.props.getNewdistricts();
             let len = data.length;
             let map = {}
             for (let i = 0; i < len; i++){
                 let p = data[i]['precincts']
                 for (let j = 0; j < p.length; j++){
                     map[p[j]['name']] = "hsl(" + (i * (360 / len) % 360) + ",100%,50%)";
                 }
             }
             console.log("set color data " + (new Date().getTime()-seconds) + "ms");
             this.props.changeState("newdistrict",map)
         }
         this.setState({resultInfo:data,running:false})} )
         .catch( (err) => {console.log(err); this.setState({resultInfo:"Data Retrieval Failed",running:false});});
    }
    onChangeRangeSlider = (e) => {
        this.setState({ rangeValues: e.value});
    }

    componentDidMount() {
        let data_server = "http://localhost:5000"
      fetch(data_server+"/files")
      .then(
        res=>res.json()
      ).then(
        data=>{
          data.forEach(
            name=>{
              let split = name.split("_");
              if(split[1]==="state.json"){
                  
                let key = split[0] + "state";
                fetch(data_server+"/geojson/"+name).then(res=>res.json()).then(result => this.setState({[key]:result}));
              }
            }
          )
        }
      )
    }
    componentDidUpdate(){

    console.log("SIDEBAR UPDATE")
    }
    render(){
        let {election,demo,state} = this.props;
        let {phase0Summary} = this.state;
        let precinctVotes = null;
        Object.keys(partyMap).forEach(party => precinctVotes += demo[election+party]?demo[election+party]:0 )
        let precinctPop = null;
        Object.keys(demoMap).forEach(key=>{precinctPop += demo[key]?demo[key]:0})

        let stateKey = this.props.state? this.props.state.toLowerCase() +"state" : null;
        let stateVotes = 0;
        Object.keys(partyMap).forEach(party => stateVotes += this.state[stateKey] && this.state[stateKey][election+party]?this.state[stateKey][election+party]:0);
        
        let statePop = 0;
        Object.keys(demoMap).forEach(key=>{statePop += this.state[stateKey] && this.state[stateKey][key]?this.state[stateKey][key]:0})
        
        let v = null;
        let m = null;
        if (stateKey == "pastate"){
            v = this.props.gerrymander.pa;
            m = this.props.mmDistricts.MMPdistricts;
        }
        else if (stateKey == "castate"){
            v = this.props.gerrymander.ca;
            m = this.props.mmDistricts.MMCdistricts;
        }
        else if (stateKey == "ristate"){
            v = this.props.gerrymander.ri;
            m = this.props.mmDistricts.MMRdistricts;
        }
        return(
            <div id="sidebar">
                
                <Dropdown placeholder="Select State" value={this.props.state} options={states} onChange={(e) => {this.props.changeState("state",e.value);this.props.changeState("demo",{});this.setState({resultInfo:null})}} disabled={this.state.running}></Dropdown>
                <Dropdown placeholder="Select View" disabled={this.props.state===null} value={this.props.view} options={views} onChange={(e) => {this.props.changeState("view",e.value)}}></Dropdown>
               
                <TabView activeIndex={this.state.tab} onTabChange={(e) => this.setState({tab: e.index})}>
                    
                    <TabPanel disabled={this.props.state===null} contentClassName="content" header={this.state.tab===0?" Vote Data":""} leftIcon="pi pi-check-circle" >
                        <Dropdown placeholder="Select Election" disabled={this.props.state===null} value={this.props.election} options={elections} onChange={(e) => {this.props.changeState("election",e.value)}}></Dropdown>
                        <Fieldset className="fieldset"legend="State Statistics">
                            {Object.keys(partyMap).map(party => 
                                <React.Fragment>
                                    {this.state[stateKey] && this.state[stateKey][election+party]? <div>{partyMap[party]} Votes: {this.convertNumber(this.state[stateKey][election+party])}</div>:null}
                                    {this.state[stateKey] && this.state[stateKey][election+party]? <ProgressBar value={Math.round(this.state[stateKey][election+party]/stateVotes*100)}/>:null}
                                </React.Fragment>
                            )}
                            <br></br>
                            {this.props.state&&reps["default"][state]?<div><b>Democratic Representatives: {reps["default"][state]["Dems"]}</b></div>:null}
                            {this.props.state&&reps["default"][state]?<div><b>Republican Representatives: {reps["default"][state]["Repubs"]}</b></div>:null}
                        </Fieldset>
                        <Fieldset className="fieldset" legend="Selected Votes">
                            {demo["NAME"]?<div><b>{demo["NAME"]}</b></div>:null}
                            {Object.keys(partyMap).map(party =>
                                <React.Fragment>
                                {demo[election+party]?<div>{partyMap[party]} Votes: {this.convertNumber(demo[election+party])}</div>:null}
                                {demo[election+party]?<ProgressBar value={Math.round(demo[election+party]/precinctVotes*100)} />:null}
                                </React.Fragment>
                            )}
                        </Fieldset>
                        <Fieldset className="fieldset" legend="Selected Representative">
                            {this.props.view==="OD"?demo["NAME"]?<div><b>{demo["NAME"]}</b></div>:null:null}
                            {this.props.view==="OD"&&reps["default"][state]?demo["NAME"]?<div><b>{reps["default"][state][demo["NAME"]]}</b></div>:null:null}
                        </Fieldset>
                    </TabPanel>
                    
                    <TabPanel disabled={this.props.state===null} contentClassName="content" header={this.state.tab===1?" Info":""} leftIcon="pi pi-users">
                        <Fieldset className="fieldset"legend="State Statistics">
                            {Object.keys(demoMap).map(key=>
                                <React.Fragment>
                                    {this.state[stateKey] && this.state[stateKey][key]!=null?<div>{demoMap[key] + ": " + this.convertNumber(this.state[stateKey][key])} </div>:null}
                                    {this.state[stateKey] && this.state[stateKey][key]!=null?<ProgressBar value={Math.round(this.state[stateKey][key]/statePop*100)}/>:null}
                                </React.Fragment>
                            )
                            }
                        </Fieldset>
                        <Fieldset className="fieldset" legend="Selected Demographics">
                            {demo["NAME"]!=null?<div><b>{demo["NAME"]}</b></div>:null}
                            {Object.keys(demoMap).map(key=>
                                <React.Fragment>
                                {demo[key]!=null?<div>{demoMap[key]+": " + this.convertNumber(demo[key])} </div>:null}
                                {demo[key]!=null?<ProgressBar value={Math.round(demo[key]/precinctPop*100)}/>:null}
                                </React.Fragment>)
                            }
                        </Fieldset>
                    </TabPanel>
                    
                    <TabPanel disabled={this.props.state===null}  contentClassName="content" header={this.state.tab===2?" Phase 0":""} leftIcon="pi pi-angle-right">
                        <div className="center">
                        <Dropdown placeholder="Select Election" disabled={this.props.state===null} value={this.props.election} options={elections} onChange={(e) => {this.props.changeState("election",e.value)}}></Dropdown>
                        <div className="top-margin">Vote Threshold: {this.state.voteThresh}%</div>
                        <Slider min={50}name="voteThresh"value={this.state.voteThresh} onChange={(e)=>this.onChangeSlider("voteThresh",e)} style={{width: '90%'}} />
                        <div>Population Threshold: {this.state.popThresh}%</div>
                        <Slider min={50}name="popThresh"value={this.state.popThresh} onChange={(e)=>this.onChangeSlider("popThresh",e)} style={{width: '90%'}} />
                        <div style={{position:"relative"}}><Button label="Submit" onClick={this.onSubmitPhase0}></Button>
                        {this.state.phase0loading?<ProgressSpinner style={{height:"30px",right:"20px", position:"absolute"}}/>:null}</div>
                        <Fieldset style={{textAlign:"left"}} className="fieldset" legend="Phase 0 Data">
                            {this.state.phase0Summary?<React.Fragment><div>Eligible Precincts: {phase0Summary.eligibleP}</div>
                            <div>Total Precincts: {phase0Summary.totalP}</div>
                            <ProgressBar value={Math.round(phase0Summary.eligibleP/phase0Summary.totalP*100)} />
                            <div>WHITE: {phase0Summary.WHITE}</div>
                            <ProgressBar value={Math.round(phase0Summary.WHITE/phase0Summary.eligibleP*100)} />
                            <div>AMIN: {phase0Summary.AMIN}</div>
                            <ProgressBar value={Math.round(phase0Summary.AMIN/phase0Summary.eligibleP*100)} />
                            <div>NHPI: {phase0Summary.NHPI}</div>
                            <ProgressBar value={Math.round(phase0Summary.NHPI/phase0Summary.eligibleP*100)} />
                            <div>BLACK: {phase0Summary.BLACK}</div>
                            <ProgressBar value={Math.round(phase0Summary.BLACK/phase0Summary.eligibleP*100)} />
                            <div>ASIAN: {phase0Summary.ASIAN}</div>
                            <ProgressBar value={Math.round(phase0Summary.ASIAN/phase0Summary.eligibleP*100)} />
                            <div>HISP: {phase0Summary.HISP}</div>
                            <ProgressBar value={Math.round(phase0Summary.HISP/phase0Summary.eligibleP*100)} /></React.Fragment>
                        :null}
                           <div style={{textAlign:"center"}}>
                           <Button label="Details" style={{marginTop:"5px"}} disabled={!this.state.phase0Data} onClick={e => this.setState({phase0Visible:true})}></Button>

                               </div> 
                        </Fieldset>

                        </div>
                    </TabPanel>
                    
                    <TabPanel  disabled={this.props.state===null}  contentClassName="content" header={this.state.tab===3?" Phase 1/2":""} leftIcon="pi pi-angle-double-right">
                        <div className="center">
                        <div>Number of Districts Required</div>
                        <InputText name="distNum"value={this.state.distNum} disabled={this.state.running} keyfilter="pint" onChange={(e)=>this.onChangeSlider("distNum",e)} style={{width: '90%'}} />
                        <Accordion style={{textAlign:"left"}}>
                            <AccordionTab header="Weights">\
                            <div style={{marginTop:'5px'}}>Compactness: {this.state.compactnessWeight}</div>

                                
                                <Slider value={this.state.compactnessWeight} disabled={this.state.running}style={{width:'90%',marginBottom:'5px'}} onChange={(e)=>this.onChangeSlider("compactnessWeight",e)}/>
                                <div>County: {this.state.countyWeight}</div>
                                <Slider value={this.state.countyWeight} disabled={this.state.running}style={{width:'90%',marginBottom:'5px'}} onChange={(e)=>this.onChangeSlider("countyWeight",e)}/>
                                <div>Fairness: {this.state.fairnessWeight}</div>
                                <Slider value={this.state.fairnessWeight} disabled={this.state.running}style={{width:'90%',marginBottom:'5px'}} onChange={(e)=>this.onChangeSlider("fairnessWeight",e)}/>
                                <div>Population: {this.state.populationWeight}</div>
                                <Slider value={this.state.populationWeight} disabled={this.state.running}style={{width:'90%',marginBottom:'5px'}} onChange={(e)=>this.onChangeSlider("populationWeight",e)}/>
                                <div>Majority Minority: {this.state.mmWeight}</div>
                                <Slider value={this.state.mmWeight} disabled={this.state.running} style={{width:'90%',marginBottom:'5px'}} onChange={(e)=>this.onChangeSlider("mmWeight",e)}/>
                               
                            </AccordionTab>
                            <AccordionTab header="Choose Minorities">
                        <ListBox style={{display:'inline-block',width:'100%'}} disabled={this.state.running}value={this.state.ethnic} options={ethnics} onChange={(e) => this.setState({ethnic: e.value})} multiple={true}/>
                        <div>Min, Max (%): {this.state.rangeValues[0]},{this.state.rangeValues[1]}</div>
                        <Slider value={this.state.rangeValues} disabled={this.state.running}onChange={this.onChangeRangeSlider} range={true} style={{width: '90%'}} />
                        
                            </AccordionTab>
                        </Accordion>
                        <div>
                        <Checkbox id="cb1" disabled={this.state.running} onChange={e => this.setState({allowStep: e.checked})} checked={this.state.allowStep}></Checkbox>
                        <label htmlFor="cb1"> Update every iteration</label>
        
                        {this.state.allowStep?
                        <React.Fragment>
                            <div style={{marginTop:"10px"}}>Number of Steps</div>
                
                            <InputText name="numSteps"value={this.state.numSteps} disabled={this.state.running} keyfilter="pint" onChange={(e)=>this.onChangeSlider("numSteps",e)} style={{width: '90%'}} />
                        </React.Fragment>:null}
                        
                        <br></br>
                        <Button label="Run Algorithm"style={{marginRight:"10px",marginTop:"10px"}} disabled={this.state.running }onClick={this.onSubmitPhase1}></Button>
                        
                        <Button label="View Results" disabled={!this.state.resultInfo} onClick={e=>this.setState({resultsVisible:true})}></Button>
                        </div>
                        {this.state.phase1Done? <div>Phase 1 Finished</div>:null}
                        <br></br>
                        <Button label="View Redistricting Rules" onClick={e=>this.setState({rules:true})}></Button>
                        {stateKey==="ristate"?<Dialog header="Rules"visible={this.state.rules} modal={true} onHide={()=>this.setState({rules:false})}>
                        Like all states, Rhode Island must comply with constitutional equal population requirements, and further requires that its state legislative districts be as nearly equal in population "as possible." [R.I. Const. art. VII, § 1; art. VIII, § 1; 2011 R.I. Laws ch. 106, § 2(c); 2011 R.I. Laws ch. 100, § 2(c)]

                        Rhode Island must also, like all states, abide by section 2 of the Voting Rights Act.

                        The Rhode Island Constitution further requires that state legislative districts be as compact in territory "as possible." State law does the same for congressional districts. [R.I. Const. art. VII, § 1; art. VIII, § 1; 2011 R.I. Laws ch. 106, § 2(d); 2011 R.I. Laws ch. 100, § 2(d)]

                        Rhode Island statutes establish additional criteria for both state legislative and congressional districts; the legislature may modify these statutes at any time. Currently, the law asks that districts be contiguous to the extent practicable, and also that they, to the extent practicable, reflect natural, historical, geographical and municipal and other political lines, "as well as the right of all Rhode Islanders to fair representation and equal access to the political process." Statutes also ask that, to the extent practicable, the lines of state House, state Senate, and congressional districts coincide -- or at least, if they do not overlap completely, they should avoid creating voting precincts with distinct ballot options where the precinct has fewer than 100 people. [2011 R.I. Laws ch. 106, § 2; 2011 R.I. Laws ch. 100, § 2; Parella v. Montalbano, 899 A.2d 1226, 1243-45 (R.I. 2006)]
                        </Dialog>:null}
                        {stateKey==="pastate"?<Dialog header="Rules"visible={this.state.rules} modal={true} onHide={()=>this.setState({rules:false})}>
                        Like all states, Pennsylvania must comply with constitutional equal population requirements; for its state legislative lines, Pennsylvania further asks that districts be drawn that are as "nearly equal in population as practicable." [Pa. Const. art. II, § 16]

                        Pennsylvania must also, like all states, abide by section 2 of the Voting Rights Act.

                        For its state legislative lines, the Pennsylvania constitution further requires that districts be contiguous and compact, and that they respect county, city, incorporated town, borough, township, and ward boundaries "unless absolutely necessary." [Pa. Const. art. II, § 16]
                        </Dialog>:null}
                        {stateKey==="castate"?<Dialog header="Rules"visible={this.state.rules} modal={true} onHide={()=>this.setState({rules:false})}>
                        Like all states, California must comply with constitutional equal population requirements. [Cal. Const. art. XXI, § 2(d)(1)]

                        California must also, like all states, abide by section 2 of the Voting Rights Act. Because four California counties (Kings, Merced, Monterey, and Yuba) are considered "covered jurisdictions" under section 5 of the Voting Rights Act, California has an obligation to submit redistricting plans to the Department of Justice or to the U.S. District Court for the District of Columbia, to ensure that the plans do not discriminate against minority communities in those counties.

                        The California constitution further requires that districts be contiguous. To the extent possible, they must also preserve the geographic integrity of cities, counties, neighborhoods, and communities of interest. To the extent practicable, and where so doing does not violate higher-priority constraints, districts must also encourage compactness, defined by lines that do not bypass nearby population in favor of more distant population. Finally, where practicable, and where not in conflict with the criteria above, state Senate and Assembly districts must be nested within each other. [Cal. Const. art. XXI, § 2(d)]

                        In drawing maps, the commission may not consider candidate residences, and districts may not be drawn to favor or discriminate against a candidate or party. [Cal. Const. art. XXI, § 2(e)]
                        </Dialog>:null}                 
                    </div>
                    </TabPanel>
                    
                </TabView>
                {this.state.resultsVisible?<Dialog header={this.state.tableView} visible={this.state.resultsVisible} style={{width:"100vw"}} modal={true} onHide={()=>this.setState({resultsVisible:false})}>
                <Dropdown value={this.state.tableView} options={tables} onChange={(e)=>{this.setState({tableView:e.value})}}/>
                {this.state.tableView==="Partisan Fairness"?<div style={{justifyContent:"space-around",display:"flex",width:"100%"}}>
                <DataTable header="Old Districts" value={v} scrollable={true} style={{width:"50%"}} scrollHeight={"calc(100vh - 300px)"}>
                    <Column field="NAME" header="District"/>
                    <Column field="SEN16" header="Congressional 2016"/>
                    <Column field="PRES16" header="Presidential 2016"/>
                    <Column field="SEN18" header="Congressional 2018"/>
                </DataTable>
                <DataTable header="New Districts" value={this.props.newGerrymander} scrollable={true} style={{width:"50%"}} scrollHeight={"calc(100vh - 300px)"}>
                    <Column field="NAME" header="District"/>
                    <Column field="SEN16" header="Congressional 2016"/>
                    <Column field="PRES16" header="Presidential 2016"/>
                    <Column field="SEN18" header="Congressional 2018"/>
                </DataTable>
                </div>:null}
                {this.state.tableView==="Majority-Minority Districts"?<div style={{justifyContent:"space-around",display:"flex",width:"100%"}}>
                    <DataTable header="Old Districts" value={m} scrollable={true} style={{width:"50%"}} scrollHeight={"calc(100vh - 300px)"}>
                        <Column field="NAME" header="District"/>
                        <Column field="maxMinority" header="Dominant Race"/>
                        <Column field="WHITE" header="White"/>
                        <Column field="ASIAN" header="Asian"/>
                        <Column field="BLACK" header="Black"/>
                        <Column field="NHPI" header="NHPI"/>
                        <Column field="HISP" header="Hispanic"/>
                        <Column field="AMIN" header="American Indian"/>
                    </DataTable>
                    <DataTable header="New Districts" value={this.props.newMMdistricts} style={{width:"50%"}} scrollable={true} scrollHeight={"calc(100vh - 300px)"}>
                        <Column field="NAME" header="District"/>
                        <Column field="maxMinority" header="Dominant Race"/>
                        <Column field="WHITE" header="White"/>
                        <Column field="ASIAN" header="Asian"/>
                        <Column field="BLACK" header="Black"/>
                        <Column field="NHPI" header="NHPI"/>
                        <Column field="HISP" header="Hispanic"/>
                        <Column field="AMIN" header="American Indian"/>
                    </DataTable>
                    </div>:null}
                </Dialog>:null}
                {this.state.phase0Visible?<Dialog header="Results" visible={this.state.phase0Visible} style={{width:"100vw"}}  modal={true} onHide={()=>this.setState({phase0Visible:false})}>
                <DataTable value={this.state.phase0Data} scrollable={true} scrollHeight={"calc(100vh - 300px)"}>
                        <Column field="name" header="Precinct" />
                        <Column field="demographic" header="Race" />
                        <Column field="demographicPop" header="Race Population"/>
                        <Column field="popPercent" header="Race %"/>
                        <Column field="party" header="Winning Party"/>
                        <Column field="partyVotes" header="Party Votes"/>
                        <Column field="votePercent" header="Vote %"/>
                </DataTable>
                    </Dialog>  :null}
                
            </div>
        )
    }
}

export default Sidebar;