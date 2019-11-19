import React from 'react';
import Map from './components/Map/LeafletMap';
import Sidebar from './components/Sidebar/Sidebar';
import './App.css';
import 'primereact/resources/themes/nova-light/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
class App extends React.Component {
  constructor(props){
    super(props);
    this.state = {
      state: null,
      view: null,
      election: "PRES16",
      demo: {},
    }
  }

  changeState = (k,val) => {
    this.setState({[k]:val})
  }

  render(){
    return (
      <div className="App flex" >
        <Sidebar election={this.state.election} demo={this.state.demo} state={this.state.state} view={this.state.view} changeState={this.changeState}></Sidebar>
        
        <Map election={this.state.election} state={this.state.state} view={this.state.view} changeState={this.changeState}></Map>
      </div>
    );
  }
}

export default App;
