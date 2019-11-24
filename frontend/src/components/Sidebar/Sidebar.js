import React from 'react';
import './Sidebar.css';
import {Dropdown} from 'primereact/dropdown';
import {TabView,TabPanel} from 'primereact/tabview';
import {Fieldset} from 'primereact/fieldset';
import {InputText} from 'primereact/inputtext';
import {Slider} from 'primereact/slider';
import {Button} from 'primereact/button';
import {ListBox} from 'primereact/listbox';
import {Checkbox} from 'primereact/checkbox';
import {ProgressBar} from 'primereact/progressbar';
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
class Sidebar extends React.Component{
   
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
        if(num===0)
            return 0;
        while(num >= 1000){
            result = ","+ num%1000 + result;
            num = Math.floor(num/1000);
        }
        if(num>0)
            result = num + result;
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

    render(){
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
        totalpop += demo["AMIN"]?demo["AMIN"]:0;
        totalpop += demo["ASIAN"]?demo["ASIAN"]:0;
        totalpop += demo["BLACK"]?demo["BLACK"]:0;
        totalpop += demo["NHPI"]?demo["NHPI"]:0;
        totalpop += demo["HISP"]?demo["HISP"]:0;
        totalpop += demo["WHITE"]?demo["WHITE"]:0;

        return(
            <div id="sidebar">
                
                <Dropdown placeholder="Select State" value={this.props.state} options={states} onChange={(e) => {this.props.changeState("state",e.value)}}></Dropdown>
                <Dropdown placeholder="Select View" disabled={this.props.state===null} value={this.props.view} options={views} onChange={(e) => {this.props.changeState("view",e.value)}}></Dropdown>
               
                <TabView activeIndex={this.state.tab} onTabChange={(e) => this.setState({tab: e.index})}>
                    
                    <TabPanel contentClassName="content" header={this.state.tab===0?" Vote Data":""} leftIcon="pi pi-check-circle" >
                        <Dropdown placeholder="Select Election" disabled={this.props.state===null} value={this.props.election} options={elections} onChange={(e) => {this.props.changeState("election",e.value)}}></Dropdown>
                        <Fieldset className="fieldset"legend="State Statistics">
                        </Fieldset>
                        <Fieldset className="fieldset" legend="Selected Votes">
                            {demo["NAME"]?<div>Name: {demo["NAME"]}</div>:null}
                            {rvotes}
                            {rvotes && totalvotes?<ProgressBar value={Math.round(demo[election+"R"]/totalvotes*100)}/>:null}
                            {dvotes}
                            {dvotes && totalvotes?<ProgressBar value={Math.round(demo[election+"D"]/totalvotes*100)}/>:null}
                            {ovotes}
                            {ovotes && totalvotes?<ProgressBar value={Math.round(demo[election+"O"]/totalvotes*100)}/>:null}
                        </Fieldset>
                    </TabPanel>
                    
                    <TabPanel contentClassName="content" header={this.state.tab===1?" Info":""} leftIcon="pi pi-users">
                    <Fieldset className="fieldset"legend="State Statistics">
                            <div>American Indian or Alaska Native:</div>
                            <div>Asian:</div>
                            <div>Black or African American:</div>
                            <div>Hawaiian or Pacific Islander:</div>
                            <div>Hispanic</div>
                            <div>White:</div>
                        </Fieldset>
                        <Fieldset className="fieldset" legend="Selected Demographics">
                            {demo["NAME"]!=null?<div>{"Name: " + demo["NAME"]}</div>:null}
                            {demo["AMIN"]!=null?<div>{"American Indian or Alaska Native: " + this.convertNumber(demo["AMIN"])}</div>:null}
                            {demo["AMIN"]!=null?<ProgressBar value={Math.round(demo["AMIN"]/totalpop*100)}/>:null}
                            {demo["ASIAN"]!=null?<div>{"Asian: " + this.convertNumber(demo["ASIAN"])}</div>:null}
                            {demo["ASIAN"]!=null?<ProgressBar value={Math.round(demo["ASIAN"]/totalpop*100)}/>:null}
                            {demo["BLACK"]!=null?<div>{"Black or African American: " + this.convertNumber(demo["BLACK"])}</div>:null}
                            {demo["BLACK"]!=null?<ProgressBar value={Math.round(demo["BLACK"]/totalpop*100)}/>:null}
                            {demo["NHPI"]!=null?<div>{"Hawaiian or Pacific Islander: " + this.convertNumber(demo["NHPI"])}</div>:null}
                            {demo["NHPI"]!=null?<ProgressBar value={Math.round(demo["NHPI"]/totalpop*100)}/>:null}
                            {demo["HISP"]!=null?<div>{"Hispanic: " + this.convertNumber(demo["HISP"])}</div>:null}
                            {demo["HISP"]!=null?<ProgressBar value={Math.round(demo["HISP"]/totalpop*100)}/>:null}
                            {demo["WHITE"]!=null?<div>{"White: " + this.convertNumber(demo["WHITE"])}</div>:null}
                            {demo["WHITE"]!=null?<ProgressBar value={Math.round(demo["WHITE"]/totalpop*100)}/>:null}
                        </Fieldset>
                    </TabPanel>
                    
                    <TabPanel contentClassName="content" header={this.state.tab===2?" Phase 0":""} leftIcon="pi pi-angle-right">
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
                    
                    <TabPanel contentClassName="content" header={this.state.tab===3?" Phase 1/2":""} leftIcon="pi pi-angle-double-right">
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