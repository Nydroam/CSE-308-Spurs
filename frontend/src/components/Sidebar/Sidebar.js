import React from 'react';
import './Sidebar.css';
import {Dropdown} from 'primereact/dropdown';
import {TabView,TabPanel} from 'primereact/tabview';
import {Fieldset} from 'primereact/fieldset';
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
            tab:0,
            election: "2016P",
        }
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
                        Content III
                    </TabPanel>
                </TabView>

            </div>
        )
    }
}

export default Sidebar;