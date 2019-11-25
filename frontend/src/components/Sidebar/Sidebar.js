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
const demomap={
    "AMIN":"American Indian or Alaskan Native",
    "ASIAN":"Asian",
    "BLACK":"Black or African American",
    "NHPI":"Hawaiian or Pacific Islander",
    "HISP":"Hispanic",
    "WHITE":"White",   
}
class Sidebar extends React.PureComponent{
   
    constructor(){
        super()
        this.state = {
            distNum: 0,
            mDistNum: 0,
            voteThresh: 0,
            popThresh: 0,
            blocInfo: "",
            ethnic: null,
            rangeValues:[0,100],
            tab:0,
            election: "2016P",
            allowStep : false,
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
              console.log(name)
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
        console.log(this.state)
        let {election,demo} = this.props;
        let rvotes = null;
        if (demo[election+"R"]!=null)
            rvotes = <div>Republican Votes: {this.convertNumber(demo[election+"R"])}</div>
        let dvotes = null;
        if (demo[election+"D"]!=null)
            dvotes = <div>Democratic Votes: {this.convertNumber(demo[election+"D"])}</div>
        let ovotes = null;
        if (demo[election+"O"]!=null)
            ovotes = <div>Other Votes: {this.convertNumber(demo[election+"O"])}</div>

        let totalvotes = null;
        if (demo[election+"R"])
            totalvotes += demo[election+"R"]
        if (demo[election+"D"])
            totalvotes += demo[election+"D"]
        if (demo[election+"O"])
            totalvotes += demo[election+"O"]

        let totalpop = null;
        Object.keys(demomap).forEach(key=>{totalpop += demo[key]?demo[key]:0})

        let statekey = this.props.state? this.props.state.toLowerCase() +"state" : null;
        let statevotes = 0;
        statevotes += this.state[statekey] && this.state[statekey][election+"R"]?this.state[statekey][election+"R"]:0;
        statevotes += this.state[statekey] && this.state[statekey][election+"D"]?this.state[statekey][election+"D"]:0;
        statevotes += this.state[statekey] && this.state[statekey][election+"O"]?this.state[statekey][election+"O"]:0;
        
        let statepop = 0;
        Object.keys(demomap).forEach(key=>{statepop += this.state[statekey] && this.state[statekey][key]?this.state[statekey][key]:0})

        return(
            <div id="sidebar">
                
                <Dropdown placeholder="Select State" value={this.props.state} options={states} onChange={(e) => {this.props.changeState("state",e.value)}}></Dropdown>
                <Dropdown placeholder="Select View" disabled={this.props.state===null} value={this.props.view} options={views} onChange={(e) => {this.props.changeState("view",e.value)}}></Dropdown>
               
                <TabView activeIndex={this.state.tab} onTabChange={(e) => this.setState({tab: e.index})}>
                    
                    <TabPanel disabled={this.props.state===null} contentClassName="content" header={this.state.tab===0?" Vote Data":""} leftIcon="pi pi-check-circle" >
                        <Dropdown placeholder="Select Election" disabled={this.props.state===null} value={this.props.election} options={elections} onChange={(e) => {this.props.changeState("election",e.value)}}></Dropdown>
                        <Fieldset className="fieldset"legend="State Statistics">
                            {this.state[statekey] && this.state[statekey][election+"R"]? <div>Republican Votes: {this.convertNumber(this.state[statekey][election+"R"])}</div>:null}
                            {this.state[statekey] && this.state[statekey][election+"R"]? <ProgressBar value={Math.round(this.state[statekey][election+"R"]/statevotes*100)}/>:null}
                            {this.state[statekey] && this.state[statekey][election+"D"]? <div>Democratic Votes: {this.convertNumber(this.state[statekey][election+"D"])}</div>:null}
                            {this.state[statekey] && this.state[statekey][election+"D"]? <ProgressBar value={Math.round(this.state[statekey][election+"D"]/statevotes*100)}/>:null}
                            {this.state[statekey] && this.state[statekey][election+"O"]? <div>Other Votes: {this.convertNumber(this.state[statekey][election+"O"])}</div>:null}
                            {this.state[statekey] && this.state[statekey][election+"O"]? <ProgressBar value={Math.round(this.state[statekey][election+"O"]/statevotes*100)}/>:null}
                        </Fieldset>
                        <Fieldset className="fieldset" legend="Selected Votes">
                            {demo["NAME"]?<div><b>{demo["NAME"]}</b></div>:null}
                            {rvotes}
                            {rvotes && totalvotes?<ProgressBar value={Math.round(demo[election+"R"]/totalvotes*100)}/>:null}
                            {dvotes}
                            {dvotes && totalvotes?<ProgressBar value={Math.round(demo[election+"D"]/totalvotes*100)}/>:null}
                            {ovotes}
                            {ovotes && totalvotes?<ProgressBar value={Math.round(demo[election+"O"]/totalvotes*100)}/>:null}
                        </Fieldset>
                    </TabPanel>
                    
                    <TabPanel disabled={this.props.state===null} contentClassName="content" header={this.state.tab===1?" Info":""} leftIcon="pi pi-users">
                        <Fieldset className="fieldset"legend="State Statistics">
                            {Object.keys(demomap).map(key=>
                                <React.Fragment>
                                    {this.state[statekey] && this.state[statekey][key]?<div>{demomap[key] + ": " + this.convertNumber(this.state[statekey][key])} </div>:null}
                                    {this.state[statekey] && this.state[statekey][key]?<ProgressBar value={Math.round(this.state[statekey][key]/statepop*100)}/>:null}
                                </React.Fragment>
                            )
                            }
                        </Fieldset>
                        <Fieldset className="fieldset" legend="Selected Demographics">
                            {demo["NAME"]!=null?<div><b>{demo["NAME"]}</b></div>:null}
                            {Object.keys(demomap).map(key=>
                                <React.Fragment>
                                {demo[key]!=null?<div>{demomap[key]+": " + this.convertNumber(demo[key])} </div>:null}
                                {demo[key]!=null?<ProgressBar value={Math.round(demo[key]/totalpop*100)}/>:null}
                                </React.Fragment>)
                            }
                        </Fieldset>
                    </TabPanel>
                    
                    <TabPanel disabled={this.props.state===null}  contentClassName="content" header={this.state.tab===2?" Phase 0":""} leftIcon="pi pi-angle-right">
                        <div className="center">
                        <Dropdown placeholder="Select Election" disabled={this.props.state===null} value={this.props.election} options={elections} onChange={(e) => {this.props.changeState("election",e.value)}}></Dropdown>
                        <div className="top-margin">Vote Threshold (%)</div>
                        <InputText name="voteThresh" value={this.state.voteThresh} style={{width: '100%'}} type="number" onChange={(e)=>this.onChangeSlider("voteThresh",e)}/>
                        <Slider name="voteThresh"value={this.state.voteThresh} onChange={(e)=>this.onChangeSlider("voteThresh",e)} style={{width: '14em'}} />
                        <div>Population Threshold (%)</div>
                        <InputText name="popThresh" value={this.state.popThresh} style={{width: '100%'}} type="number" onChange={(e)=>this.onChangeSlider("popThresh",e)}/>
                        <Slider name="popThresh"value={this.state.popThresh} onChange={(e)=>this.onChangeSlider("popThresh",e)} style={{width: '14em'}} />
                        <Button label="Submit" onClick={this.onSubmitPhase0}></Button>
                        <Fieldset className="fieldset" legend="Phase 0 Data">
                            {this.state.blocInfo}
                        </Fieldset>
                        </div>
                    </TabPanel>
                    
                    <TabPanel  disabled={this.props.state===null}  contentClassName="content" header={this.state.tab===3?" Phase 1/2":""} leftIcon="pi pi-angle-double-right">
                        <div className="center play"><i className="pi pi-step-backward" style={{'fontSize':'15px'}}></i>
                        <i className="pi pi-caret-right" style={{'fontSize':'30px'}}></i>
                        <i className="pi pi-step-forward" style={{'fontSize':'15px'}}></i></div>
                        <div className="center">
                        <div>Number of Districts Required:</div>
                        <InputText name="distNum"value={this.state.distNum} style={{width: '100%'}} type="number" onChange={(e)=>this.onChangeSlider("distNum",e)} />
                        <Slider name="distNum"value={this.state.distNum} onChange={(e)=>this.onChangeSlider("distNum",e)} style={{width: '14em'}} />
                        
                        <div>Number of Majority-Minority Districts Required:</div>
                        <InputText name="mDistNum"value={this.state.mDistNum} style={{width: '100%'}} type="number" onChange={(e)=>this.onChangeSlider("mDistNum",e)} />
                        <Slider name="mDistNum"value={this.state.mDistNum} onChange={(e)=>this.onChangeSlider("mDistNum",e)} style={{width: '14em'}} />
                        
                        <div>Choose Minorities</div>
                        <ListBox style={{display:'inline-block',width:'100%'}} value={this.state.ethnic} options={ethnics} onChange={(e) => this.setState({ethnic: e.value})} multiple={true}/>
                        <div>Min, Max (%): {this.state.rangeValues[0]},{this.state.rangeValues[1]}</div>
                        <Slider value={this.state.rangeValues} onChange={this.onChangeRangeSlider} range={true} style={{width: '14em'}} />
                        <div>
                        <Checkbox id="cb1" onChange={e => this.setState({allowStep: e.checked})} checked={this.state.allowStep}></Checkbox>
                        <label htmlFor="cb1"> Update every iteration</label>
                        </div>                        
                    </div>
                    </TabPanel>
                    
                </TabView>

            </div>
        )
    }
}

export default Sidebar;