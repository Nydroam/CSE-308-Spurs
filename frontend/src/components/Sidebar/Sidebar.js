import React from 'react';
import './Sidebar.css';
import {Dropdown} from 'primereact/dropdown';
import {TabView,TabPanel} from 'primereact/tabview';
import {Fieldset} from 'primereact/fieldset';
import {InputText} from 'primereact/inputtext';
import {Slider} from 'primereact/slider';
const lorum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mi neque, ornare at tincidunt id, sollicitudin nec neque. Morbi vitae efficitur dui. Vivamus sodales vitae quam in aliquet. Fusce laoreet eleifend sodales. Nam eleifend lorem quis vehicula imperdiet. Aliquam pulvinar dapibus tellus, et convallis odio suscipit a. Morbi ex orci, dictum at lectus quis, placerat blandit erat. Nunc sit amet tempor leo. Donec vitae arcu a lectus luctus sollicitudin at in mauris. Vivamus ligula ante, auctor sit amet iaculis in, efficitur eu nibh. Suspendisse nec nunc semper, hendrerit nunc fermentum, sagittis nisl. Mauris interdum eros sollicitudin massa aliquet tempus. Praesent mattis fringilla dolor, vitae auctor sem congue vitae.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mi neque, ornare at tincidunt id, sollicitudin nec neque. Morbi vitae efficitur dui. Vivamus sodales vitae quam in aliquet. Fusce laoreet eleifend sodales. Nam eleifend lorem quis vehicula imperdiet. Aliquam pulvinar dapibus tellus, et convallis odio suscipit a. Morbi ex orci, dictum at lectus quis, placerat blandit erat. Nunc sit amet tempor leo. Donec vitae arcu a lectus luctus sollicitudin at in mauris. Vivamus ligula ante, auctor sit amet iaculis in, efficitur eu nibh. Suspendisse nec nunc semper, hendrerit nunc fermentum, sagittis nisl. Mauris interdum eros sollicitudin massa aliquet tempus. Praesent mattis fringilla dolor, vitae auctor sem congue vitae."

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
    {label:"American Indian or Alaskan Native", value:"AI"},
    {label:"Asian", value:"A"},
    {label:"Black or African American", value:"AA"},
    {label:"Hawaiian or Pacific Islander", value:"HPI"},
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
            state: null,
            view: null,
            distNum: null,
            mDistNum: null,
            ethnic: "AI",
            rangeValues:{"AI":[0,100],
            "A":[0,100],
            "AA":[0,100],
            "HPI":[0,100],},
            tab:0,
            election: "2016P",
        }
    }
    onChangeSlider2 = (e) => {
        var newValue;
        if (e.target && e.target.name === "distNum") {
            newValue = e.target.value;
        }
        else {
            newValue = e.value;
        }

        this.setState({ distNum: newValue });
    }
    onChangeSlider3 = (e) => {
        var newValue;
        if (e.target && e.target.name === "mDistNum") {
            newValue = e.target.value;
        }
        else {
            newValue = e.value;
        }

        this.setState({ distNum: newValue });
    }
    onChangeRangeSlider = (e) => {
        var tempList = this.state.rangeValues;
        tempList[this.state.ethnic] = e.value;
        this.setState({ rangeValues: tempList });
    }
    render(){
        return(
            <div id="sidebar">
                <Dropdown placeholder="Select State" value={this.state.state} options={states} onChange={(e) => {this.setState({state: e.value})}}></Dropdown>
                <Dropdown placeholder="Select View" value={this.state.view} options={views} onChange={(e) => {this.setState({view: e.value})}}></Dropdown>
                <TabView activeIndex={this.state.tab} onTabChange={(e) => this.setState({tab: e.index})}>
                    <TabPanel contentClassName="content" header={this.state.tab===0?" Voting Data":""} leftIcon="pi pi-check-circle" >
                        <Dropdown placeholder="Select Election" value={this.state.election} options={elections} onChange={(e) => {this.setState({election: e.value})}}></Dropdown>
                        <Fieldset className="fieldset"legend="State Statistics">
                            <div>Population:</div>
                            <div>Democrat Votes:</div>
                            <div>Republican Votes:</div>
                        </Fieldset>
                        <Fieldset className="fieldset" legend="Selected Region Statistics">
                            <div>Population:</div>
                            <div>Democrat Votes:</div>
                            <div>Republican Votes:</div>
                        </Fieldset>
                    </TabPanel>
                    <TabPanel contentClassName="content" header={this.state.tab===1?" Demographics":""} leftIcon="pi pi-users">
                    <Fieldset className="fieldset"legend="State Statistics">
                            <div>American Indian or Alaska Native:</div>
                            <div>Asian:</div>
                            <div>Black or African American:</div>
                            <div>Hawaiian or Pacific Islander:</div>
                            <div>White:</div>
                        </Fieldset>
                        <Fieldset className="fieldset" legend="Selected Region Statistics">
                        <div>American Indian or Alaska Native:</div>
                            <div>Asian:</div>
                            <div>Black or African American:</div>
                            <div>Hawaiian or Pacific Islander:</div>
                            <div>White:</div>
                        </Fieldset>
                    </TabPanel>
                    <TabPanel contentClassName="content" header={this.state.tab===2?" Redistrict":""} leftIcon="pi pi-caret-right">
                        <div className="center play"><i className="pi pi-step-backward" style={{'fontSize':'15px'}}></i>
                        <i className="pi pi-caret-right" style={{'fontSize':'30px'}}></i>
                        <i className="pi pi-step-forward" style={{'fontSize':'15px'}}></i></div>
                        <div className="center">
                        <div>Number of Districts:</div>
                        <InputText name="distNum"value={this.state.distNum} style={{width: '100%'}} type="number" onChange={this.onChangeSlider2} />
                        <Slider name="distNum"value={this.state.distNum} onChange={this.onChangeSlider2} style={{width: '14em'}} />
                        <div>Number of Maj-min Districts:</div>
                        <InputText name="mDistNum"value={this.state.mDistNum} style={{width: '100%'}} type="number" onChange={this.onChangeSlider3} />
                        <Slider name="mDistNum"value={this.state.mDistNum} onChange={this.onChangeSlider3} style={{width: '14em'}} />
                        <Dropdown placeholder="Select Ethnic Group" value={this.state.ethnic} options={ethnics} onChange={(e) => {this.setState({ethnic: e.value})}}></Dropdown>
                        <div>Min,Max: {this.state.rangeValues[this.state.ethnic][0]},{this.state.rangeValues[this.state.ethnic][1]}</div>
                        <Slider value={this.state.rangeValues[this.state.ethnic]} onChange={this.onChangeRangeSlider} range={true} style={{width: '14em'}} />
                        <Fieldset className="fieldset" legend="Selected Region Statistics">
                        <div>American Indian or Alaska Native:</div>
                            <div>Asian:</div>
                            <div>Black or African American:</div>
                            <div>Hawaiian or Pacific Islander:</div>
                            <div>White:</div>
                        </Fieldset>
                    </div>
                    </TabPanel>
                </TabView>

            </div>
        )
    }
}

export default Sidebar;