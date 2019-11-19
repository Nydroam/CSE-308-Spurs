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
    {label:"2016 Presidential Election", value: "2016P"},
    {label:"2016 Congressional Election", value: "2016C"},
    {label:"2018 Congressional Election", value: "2018C"}
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
    
    onSubmitPhase0 = async () => {
        let seconds = new Date().getTime();
        let response = await fetch("localhost:5000/phase0",
         {method:"POST", body: JSON.stringify([this.state.popThresh,this.state.voteThresh])}

         ).then( (res) => res.json())
         .catch( (err) => {console.log(err); return "Data Retrieval Failed";});
        console.log("Fetching took " + (new Date().getTime()-seconds) + "ms");
         this.setState({blocInfo:[response]})
    }

    onChangeRangeSlider = (e) => {
        this.setState({ rangeValues: e.value});
    }
    render(){
        return(
            <div id="sidebar">
                <Dropdown placeholder="Select State" value={this.props.state} options={states} onChange={(e) => {this.props.changeState("state",e.value)}}></Dropdown>
                <Dropdown placeholder="Select View" value={this.props.view} options={views} onChange={(e) => {this.props.changeState("view",e.value)}}></Dropdown>
                <TabView activeIndex={this.state.tab} onTabChange={(e) => this.setState({tab: e.index})}>
                    <TabPanel contentClassName="content" header={this.state.tab===0?" Vote Data":""} leftIcon="pi pi-check-circle" >
                        <Dropdown placeholder="Select Election" value={this.state.election} options={elections} onChange={(e) => {this.setState({election: e.value})}}></Dropdown>
                        <Fieldset className="fieldset"legend="State Statistics">
                            <div>Population:</div>
                            <div>Democrat Votes:</div>
                            <div>Republican Votes:</div>
                        </Fieldset>
                        <Fieldset className="fieldset" legend="Selected Region Data">
                            
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
                        <div>Name: {this.props.demo["NAME"]}</div>
                            {this.props.demo["AMIN"]?<div>{"American Indian or Alaska Native: " + this.props.demo["AMIN"]}</div>:null}
                            {this.props.demo["ASIAN"]?<div>{"Asian: " + this.props.demo["ASIAN"]}</div>:null}
                            {this.props.demo["BLACK"]?<div>{"Black or African American: " + this.props.demo["BLACK"]}</div>:null}
                            {this.props.demo["NHPI"]?<div>{"Hawaiian or Pacific Islander: " + this.props.demo["NHPI"]}</div>:null}
                            {this.props.demo["HISP"]?<div>{"Hispanic: " + this.props.demo["HISP"]}</div>:null}
                            {this.props.demo["WHITE"]?<div>{"White: " + this.props.demo["WHITE"]}</div>:null}
                        </Fieldset>
                    </TabPanel>
                    <TabPanel contentClassName="content" header={this.state.tab===2?" Phase 0":""} leftIcon="pi pi-angle-right">
                        <div className="center">
                        <div>Vote Threshold (%)</div>
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