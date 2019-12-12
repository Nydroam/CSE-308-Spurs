import React from 'react';
import {Map, TileLayer, GeoJSON, FeatureGroup} from 'react-leaflet';
import './Map.css';

const stateMap = {
  "PENNSYLVANIA":"PA",
  "CALIFORNIA":"CA",
  "RHODE ISLAND":"RI",
}

//position of center of US for initial load
const position = [38, -98]
class LeafletMap extends React.PureComponent{

    constructor(props) {
      super(props);
      this.mapRef = React.createRef();
      this.groupRef = React.createRef();
      this.state = {
        currState: null,
        currHover: null,
        currView: null,
      }
    }

    handleClick = (e)=>{
      this.mapRef.current.leafletElement.fitBounds(e.layer.getBounds())
      let val = e.layer.feature.properties.STATE;
      val = stateMap[val];
      this.props.changeState("state",val)
      this.setState({currState:val})
    }

    onHover = (e)=>{
      let properties = e.layer.feature.properties;
      this.props.changeState("demo",properties)
      console.log(properties)
      e.layer.setStyle({
        fillColor: 'cyan',
        fillOpacity:1,
        color:'yellow',
        weight:0.5,
        opacity:1,
      })
    }

    onHoverOff = (e)=>{
      e.layer.setStyle(this.setStyle(e.layer.feature))
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
      }else{
        c = 'gray';
      }
      return{
        fillColor: c,
        fillOpacity:1,
        color:'white',
        weight:0.5,
        opacity:1,
      }
    }

    newStyle = (feature) =>{
      return{
        fillColor: this.props.newdistrict?this.props.newdistrict[feature.properties.NAME]:"hsl(0, 0%, 10%)",
        fillOpacity:1,
        weight:1,
        color:this.props.newdistrict?this.props.newdistrict[feature.properties.NAME]:"hsl(0, 0%, 10%)",
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
    componentDidMount(){
      let data_server = "http://localhost:5000"
      fetch(data_server+"/files")
      .then(
        res=>res.json()
      ).then(
        data=>{
          data.forEach(
            name=>{
              let split = name.split("_");
              let key = split[0] + split[1];
              fetch(data_server+"/geojson/"+name).then(res=>res.json()).then(result => this.setState({[key]:result}));
            }
          )
        }
      )
    }
    componentDidUpdate(){
      console.log("UPDATE")
      if(this.mapRef.current && this.groupRef.current && this.props.state != null && this.props.state!==this.state.currState){
        try{
        this.mapRef.current.leafletElement.fitBounds(this.groupRef.current.leafletElement.getBounds())
        }
        catch{
        }
      }
      if(this.groupRef.current && (this.state.currView !== this.props.view || this.state.currState !== this.props.state)){
        let feature = this.groupRef.current.leafletElement;
        feature.eachLayer( layer => {
          layer.off()
          layer.on("click",e=>this.handleClick(e))
          if(this.props.view !== "ND"){
          layer.on("mouseover",e=>this.onHover(e))
          layer.on("mouseout",e=>this.onHoverOff(e))
          }
        })
      }
      if(this.props.state!==this.state.currState)   
        this.setState({currState:this.props.state})
      if(this.props.view!=this.state.currView)
        this.setState({currView:this.props.view})
    }
    render(){
        let features = 
         <React.Fragment>
           {Object.values(stateMap).map(val=>{return (this.state[val.toLowerCase()+"district"]?<GeoJSON data={this.state[val.toLowerCase()+"district"]} style={this.setStyle} onClick={e=>this.handleClick(e)}></GeoJSON>:null)})}
        </React.Fragment> ;

        if(this.props.state){
          let mapkey = null;
          let style = this.setStyle;
          if(this.props.view === "OD") {
            mapkey = this.props.state.toLowerCase()+"district";
          }else{
            mapkey = this.props.state.toLowerCase()+"precinct";
            if(this.props.view === "ND"){
              style = this.newStyle;
            }
          }
          features = <GeoJSON data={this.state[[mapkey]]} key={mapkey} style={style}></GeoJSON>
      }

        return(
          <Map center={position} ref={this.mapRef} zoom={5}id="map">
              <TileLayer
      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      attribution="&copy; <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
    /> 
           <FeatureGroup ref={this.groupRef}>{features}</FeatureGroup>
          </Map>
        );
    }
}

export default LeafletMap;