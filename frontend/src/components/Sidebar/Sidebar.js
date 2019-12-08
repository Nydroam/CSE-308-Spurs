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
import './Sidebar.css';
const states=[
    {label:"California", value:"CA"},
    {label:"Pennsylvania", value:"PA"},
    {label:"Rhode Island", value:"RI"},
]
const views=[
    {label:"Original Districts", value:"OD"},
    {label:"Voting Precincts", value:"VP"},
    {label:"New Districts", value:"ND"},
]
const ethnics= [
    {label:"American Indian/Alaskan Native", value:"AI"},
    {label:"Asian", value:"A"},
    {label:"Black or African American", value:"AA"},
    {label:"Hispanic", value:"HIS"},
    {label:"Hawaiian/Pacific Islander", value:"HPI"},
]
const elections=[
    {label:"2016 Presidential Election", value: "PRES16"},
    {label:"2016 Congressional Election", value: "SEN16"},
    {label:"2018 Congressional Election", value: "SEN18"}
]
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
            blocInfo: "",
            resultInfo: null,
            ethnic: null,
            rangeValues:[0,100],
            tab:0,
            election: "2016P",
            allowStep : false,
            running : false,
            runningStep : false,
            firstStep : true,
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
        fetch("localhost:8080/spurs/state/runPhase0",
         {method:"POST", body: JSON.stringify(
             {stateId:this.props.state,
            electionType:this.props.election,
            popThresh:this.state.popThresh,
            voteThresh:this.state.voteThresh})}
         ).then( (res) => res.json())
         .then( (data) => this.setState({blockInfo:data}))
         .catch( (err) => {console.log(err); this.setState({blockInfo:"Data Retrieval Failed"});});
        console.log("Fetching took " + (new Date().getTime()-seconds) + "ms");
    }
    onSubmitPhase1 = () => {
        this.props.changeState("view","ND")
        let seconds = new Date().getTime();
        if(this.state.allowStep){
            if(!this.state.firstStep){
                this.setState({runningStep:true});
                fetch("localhost:8080/spurs/state/runPhase1step",
                    {method:"POST",}).then( (res) => res.json())
                    .then( (data) => {
                        if(data["finished"]){
                            this.setState({firstStep:true});
                        }
                        this.setState({resultInfo:data,runningStep:false});
                    })
                    .catch( (err) => {console.log(err); this.setState({resultInfo:"Data Retrieval Failed",runningStep:false,firstStep:true});});}
            else{
                this.setState({runningStep:true,firstStep:false});
                fetch("localhost:8080/spurs/state/runPhase1step",
                {method:"POST", body: JSON.stringify(
                    {stateId:this.props.state,
                    mDistNum:this.state.mDistNum,
                    distNum:this.state.distNum,
                    ethnic:this.state.ethnic,
                    rangeMin:this.state.rangeValues[0],
                    rangeMax:this.state.rangeValues[1]})}
                ).then( (res) => res.json())
                .then( (data) => this.setState({resultInfo:data,runningStep:false}))
                .catch( (err) => {console.log(err); this.setState({resultInfo:"Data Retrieval Failed",runningStep:false,firstStep:true});});
            }
        }
        else{
        this.setState({running:true});
        fetch("localhost:8080/spurs/state/runPhase1",
             {method:"POST", body: JSON.stringify(
                {stateId:this.props.state,
                mDistNum:this.state.mDistNum,
                distNum:this.state.distNum,
                ethnic:this.state.ethnic,
                rangeMin:this.state.rangeValues[0],
                rangeMax:this.state.rangeValues[1]})}
         ).then( (res) => res.json())
         .then( (data) => this.setState({resultInfo:data,running:false}))
         .catch( (err) => {console.log(err); this.setState({resultInfo:"Data Retrieval Failed",running:false});});}
         console.log("Fetching took " + (new Date().getTime()-seconds) + "ms");
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

    render(){
        let {election,demo} = this.props;

        let precinctVotes = null;
        Object.keys(partyMap).forEach(party => precinctVotes += demo[election+party]?demo[election+party]:0 )

        let precinctPop = null;
        Object.keys(demoMap).forEach(key=>{precinctPop += demo[key]?demo[key]:0})

        let stateKey = this.props.state? this.props.state.toLowerCase() +"state" : null;
        let stateVotes = 0;
        Object.keys(partyMap).forEach(party => stateVotes += this.state[stateKey] && this.state[stateKey][election+party]?this.state[stateKey][election+party]:0);
        
        let statePop = 0;
        Object.keys(demoMap).forEach(key=>{statePop += this.state[stateKey] && this.state[stateKey][key]?this.state[stateKey][key]:0})

        return(
            <div id="sidebar">
                
                <Dropdown placeholder="Select State" value={this.props.state} options={states} onChange={(e) => {this.props.changeState("state",e.value);this.props.changeState("demo",{});this.setState({resultInfo:null})}} disabled={this.state.running || this.state.runningStep}></Dropdown>
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
                        <Button label="Submit" onClick={this.onSubmitPhase0}></Button>
                        <Fieldset className="fieldset" legend="Phase 0 Data">
                            {this.state.blocInfo}
                        </Fieldset>
                        </div>
                    </TabPanel>
                    
                    <TabPanel  disabled={this.props.state===null}  contentClassName="content" header={this.state.tab===3?" Phase 1/2":""} leftIcon="pi pi-angle-double-right">
                        <div className="center">
                        <div>Number of Districts Required: {this.state.distNum}</div>
                        <Slider name="distNum"value={this.state.distNum} disabled={this.state.running} onChange={(e)=>this.onChangeSlider("distNum",e)} style={{width: '90%'}} />
                        
                        <div>Number of Majority-Minority Districts Required: {this.state.mDistNum}</div>
                        <Slider name="mDistNum"value={this.state.mDistNum} disabled={this.state.running} onChange={(e)=>this.onChangeSlider("mDistNum",e)} style={{width: '90%'}} />
                        
                        <div>Choose Minorities</div>
                        <ListBox style={{display:'inline-block',width:'100%'}} disabled={this.state.running}value={this.state.ethnic} options={ethnics} onChange={(e) => this.setState({ethnic: e.value})} multiple={true}/>
                        <div>Min, Max (%): {this.state.rangeValues[0]},{this.state.rangeValues[1]}</div>
                        <Slider value={this.state.rangeValues} disabled={this.state.running}onChange={this.onChangeRangeSlider} range={true} style={{width: '90%'}} />
                        <div>
                        <Checkbox id="cb1" disabled={this.state.running} onChange={e => this.setState({allowStep: e.checked})} checked={this.state.allowStep}></Checkbox>
                        <label htmlFor="cb1"> Update every iteration</label>
                        <br></br>
                        <Button label="Run Algorithm"style={{marginRight:"10px",marginTop:"10px"}} disabled={this.state.running || this.state.runningStep}onClick={this.onSubmitPhase1}></Button>
                        
                        <Button label="View Results" disabled={!this.state.resultInfo} onClick={e=>this.setState({resultsVisible:true})}></Button>
                        {(this.state.firstStep&&this.state.resultInfo&&this.state.resultInfo!=="Data Retrieval Failed")?<div>Algorithm has finished</div>:null}
                        </div>
                                              
                    </div>
                    </TabPanel>
                    
                </TabView>
                <Dialog header="Results" visible={this.state.resultsVisible} style={{width:"100vw"}} modal={true} onHide={()=>this.setState({resultsVisible:false})}>
                            </Dialog>  
            </div>
        )
    }
}

export default Sidebar;