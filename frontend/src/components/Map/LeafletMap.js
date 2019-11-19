import React from 'react';
import {Map, TileLayer, GeoJSON, FeatureGroup} from 'react-leaflet';
import './Map.css';
import * as padist from '../../data/pa_district_clean.json';
import * as ridist from '../../data/ri_district_clean.json';
import * as cadist from '../../data/ca_district_clean.json';
import * as ripre from '../../data/ri_precinct_clean.json';

const position = [38, -98]
class LeafletMap extends React.Component{

    constructor(props) {
      super(props);
      this.mapRef = React.createRef();
      this.groupRef = React.createRef();
      this.state = {
        currState: null,
        currHover: null,
      }
    }
    handleClick = (e)=>{
      console.log(e)
      this.mapRef.current.leafletElement.fitBounds(e.layer.getBounds())
      let val = e.layer.feature.properties.STATE;
      if (val === "PENNSYLVANIA")
        val = "PA"
      if (val === "RHODE ISLAND")
        val = "RI"
      if (val === "CALIFORNIA")
        val = "CA"
      this.props.changeState("state",val)
      this.setState({currState:val})
    }

    onHover = (e)=>{
      let properties = e.layer.feature.properties;
      if(this.state.currHover !== properties.NAME){
        this.props.changeState("demo",properties)
        this.setState({currHover:properties.name})

      }
      
      e.layer.setStyle({
        fillColor: 'cyan',
        fillOpacity:1,
        color:'yellow',
        weight:0.5,
        opacity:1,
      })
    }

    onHoverOff = (e)=>{
      e.layer.setStyle({
        fillColor: 'blue',
        fillOpacity:1,
        color:'white',
        weight:0.5,
        opacity:1,
      })
    }
    setStyle = (feature) =>{
      let {election} = this.props;
      let properties = feature.properties;
      let c = 'blue'
      let rvotes = 0;
      let dvotes = 0;
      if(properties[election+"R"] || properties[election+"D"]){
      if (properties[election+"R"])
        rvotes = properties[election+"R"]
      if (properties[election+"D"])
        dvotes = properties[election+"D"]
      let total = (rvotes + dvotes)?rvotes+dvotes:1;
      c = "rgb("+rvotes/total * 255+", 0, " + dvotes/total*255 + ")";
      }
      return{
        fillColor: c,
        fillOpacity:1,
        color:'white',
        weight:0.5,
        opacity:1,
      }
    }

    setStyleHover = (feature) =>{
      return{
        fillColor: 'cyan',
        fillOpacity:1,
        color:'yellow',
        weight:0.5,
        opacity:1,
      }
    } 
    componentDidUpdate(){
      if(this.mapRef.current && this.groupRef.current && this.props.state != null && this.props.state!==this.state.currState)
        this.mapRef.current.leafletElement.fitBounds(this.groupRef.current.leafletElement.getBounds())
      if(this.groupRef.current){
        let feature = this.groupRef.current.leafletElement;
        feature.eachLayer( layer => {
          layer.off()
          layer.on("click",e=>this.handleClick(e))
          layer.on("mouseover",e=>this.onHover(e))
          layer.on("mouseout",e=>this.onHoverOff(e))
        })
      }
      if(this.props.state!==this.state.currState)   
        this.setState({currState:this.props.state})
    }
    render(){
        let features = 
         <React.Fragment>
        <GeoJSON data={cadist['default']} key={1} style={this.setStyle} onClick={(e)=>this.handleClick(e)}></GeoJSON>
        <GeoJSON data={padist['default']} key={2} style={this.setStyle} onClick={(e)=>this.handleClick(e)} ></GeoJSON>
        <GeoJSON data={ridist['default']} key={3} style={this.setStyle} onClick={(e)=>this.handleClick(e)}></GeoJSON>
        </React.Fragment> ;
        if (this.props.state === "RI"){
          if (this.props.view !== "VP"){
            features = <GeoJSON data={ridist['default']} key={3} style={this.setStyle} ></GeoJSON>
          }
          else{
            
            features = <GeoJSON data={ripre['default']} key={6} style={this.setStyle} ></GeoJSON>

          }
        }
        if (this.props.state === "PA"){
          features = <GeoJSON data={padist['default']} key={2} style={this.setStyle} ></GeoJSON>
        }
        if (this.props.state === "CA"){
          features = <GeoJSON data={cadist['default']} key={1} style={this.setStyle} ></GeoJSON>
        }

        return(
          <Map center={position} ref={this.mapRef} zoom={5}id="map">
              <TileLayer
      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      attribution="&copy; <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
    />        <FeatureGroup ref={this.groupRef}>{features}</FeatureGroup>
          </Map>
        );
    }
}

export default LeafletMap;