import React from 'react';
import {Map, TileLayer, GeoJSON, FeatureGroup} from 'react-leaflet';
import './Map.css';


const stateMap = {
  "PENNSYLVANIA":"PA",
  "CALIFORNIA":"CA",
  "RHODE ISLAND":"RI",
}

const demoMap={
  "AMIN":"American Indian or Alaskan Native",
  "ASIAN":"Asian",
  "BLACK":"Black or African American",
  "NHPI":"Hawaiian or Pacific Islander",
  "HISP":"Hispanic", 
  "WHITE":"White",
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

    distStyle = (feature) =>{
      if (this.state.hoverDist.precincts.find(p => p['name'] ===feature.properties["NAME"])){
        return this.setStyleHover(feature);
      }
      let style = {
        fillColor: this.props.newdistrict?this.props.newdistrict[feature.properties.NAME]:"hsl(0, 0%, 10%)",
        fillOpacity:1,
        weight:1,
        color:this.props.newdistrict?this.props.newdistrict[feature.properties.NAME]:"hsl(0, 0%, 10%)",
        opacity:1,
      };
              if(this.props.distView === "PF"){
                style = this.pfStyle;
              }else if(this.props.distView && Object.keys(demoMap).find(key=>this.props.distView==key)){
                style = this.demoStyle;
              }
      return style;
    }

    onHover = (e)=>{
      
      if(this.props.view === "ND" && this.props.distList){
        console.log("HI");
        let feature = e.layer.feature;
        let dist = this.props.distList.find(d => d.precincts.find(p => feature.properties["NAME"]===p["name"]) !== undefined);
        if(dist){
          if(this.state.hoverDist == null || this.state.hoverDist.id!==dist.id)
             this.setState({"hoverDist":dist});
          this.props.changeState("demo",dist);
          return;
        }
      }

      let properties = e.layer.feature.properties;
      console.log(properties);
      this.props.changeState("demo",properties)
     
      e.layer.setStyle({
        fillColor: 'cyan',
        fillOpacity:1,
        color:'yellow',
        weight:0.5,
        opacity:1,
      })
    }

    onHoverOff = (e)=>{
      if(this.props.view==="ND")
        return;
      if(this.props.view ==="VPB")
        e.layer.setStyle(this.blocStyle(e.layer.feature))
      else
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
      if(this.props.distList && this.state.hoverDist && this.props.view === "ND"){
        return this.distStyle(feature);
      }
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
        color:'white',
        weight:1.5,
        opacity:1,
      }
    }
    pfStyle = (feature) =>{
      if(this.props.distList && this.state.hoverDist && this.props.view === "ND"){
        return this.distStyle(feature);
      }
      let featureName = feature.properties['NAME'];
      if(this.props.view == "ND"){
        featureName = feature.properties['DISTRICT'];
      }
      let fairnessList = this.props.gerrymander[this.props.state.toLowerCase()];
      let fairness = fairnessList.find( dist => {
        let split = dist["NAME"].split(" ");
        let names = featureName.split(" ");
        return split[1]==names[1] && split[2]== names[2];
      } );
      if(fairness){
        fairness=fairness[this.props.election];
        if (fairness === 1){
          fairness = 0;
        }else{
          fairness = fairness.substring(0,fairness.length-1);
          fairness = parseFloat(fairness);
          fairness = (1-fairness/100) + 0.3;
          console.log("FAIRNESS",fairness);
          
        }
        if(this.props.view == "ND" ){
          if(this.props.distList){
          let dist = this.props.distList.find(d => d.precincts.find(p=>p.name===feature.properties["NAME"])!==undefined);
          console.log(dist);
          if(dist){
            let rvotes = dist[this.props.election+"R"];
            let dvotes = dist[this.props.election+"D"];
            let total = (rvotes + dvotes)?rvotes+dvotes:1;
            let c = total?"rgb("+rvotes/total * 255+", 0, " + dvotes/total*255 + ")":"gray";
            return {
              fillColor: c,
              fillOpacity: fairness,
              color:c,
              weight: 0.5,
              opacity: 1,
            }
          }
          }else{
            return{
              fillColor: "gray",
              fillOpacity:1,
              color:'white',
              weight:0.5,
              opacity:1,
            }
          }
        }
        return {
          ...this.setStyle(feature),
          fillOpacity: fairness,
        }
      }
      return{
        fillColor: "gray",
        fillOpacity:1,
        color:'white',
        weight:0.5,
        opacity:1,
      }
      console.log(this.props.gerrymander)
    }
    blocStyle = (feature) =>{

      let data = this.props.phase0Data?this.props.phase0Data.find(item=>{return item['name']===feature.properties['NAME'];
      
    }):null;
    if(data){
      let votePercent = data['votePercent'];
      let party = data['party'];
      let popPercent = data['popPercent'];
      let demographic = data['demographic'];
      return{
        fillColor: this.props.colorMap[demographic],
        fillOpacity:popPercent,
        color: party==="DEMOCRATIC"?'blue':'red',
        weight:2.5,
        opacity:votePercent,
      }
    }
    else{
      return{
        fillColor: "gray",
        fillOpacity: 1,
        color:"gray",
        weight:0.5,
        opacity:1,
      }
    }
  }

    demoStyle = (feature) =>{
      console.log("DEMOSTYLE");
      let properties = feature.properties;
      if(this.props.view==="OD"){
      
      let popTotal = Object.keys(demoMap).map(key => properties[key]).reduce( (a,b) => a+b);
      let percent = properties[this.props.distView]/popTotal;
      console.log("HERE", percent);
      return{
        fillColor: this.props.colorMap[this.props.distView],
        fillOpacity:percent,
        color:"black",
        weight:1,
        opacity:1,
      }
    }else{
      let dist = this.props.distList.find(d => d.precincts.find(p=>p.name===feature.properties["NAME"]) !== undefined );
      if(dist){
        let demo = this.props.distView;
        let popTotal = Object.keys(demoMap).map(key => dist[key]).reduce((a,b) => a+b);
        let percent = dist[demo]/popTotal;
        return{
          fillColor: this.props.colorMap[this.props.distView],
          fillOpacity:percent,
          color:"black",
          weight:1,
          opacity:1,
        }
      }
      
    }
    return{
      fillColor: "gray",
      fillOpacity: 1,
      color:"gray",
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
              if (split[1] == "district" || split[1] == "precinct"){
              let key = split[0] + split[1];
              fetch(data_server+"/geojson/"+name).then(res=>res.json()).
              then(result => {this.setState({[key]:result});
              this.props.updateState(key,result)});}
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
        console.log("FEATURE",feature)
        feature.eachLayer( layer => {
          console.log("HERE")
          layer.off()
          layer.on("click",e=>this.handleClick(e))
          layer.on("mouseover",e=>this.onHover(e))
          layer.on("mouseout",e=>this.onHoverOff(e))
          
        })
      }
      if(this.props.state!==this.state.currState){   
        this.setState({currState:this.props.state})
        this.props.resetData();
      }
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
            if(this.props.distView === "PF"){
              style = this.pfStyle;
            }else if(this.props.distView && Object.keys(demoMap).find(key=>this.props.distView==key)){
              style = this.demoStyle;
            }
          }else{
            mapkey = this.props.state.toLowerCase()+"precinct";
            if(this.props.view === "ND"){
              style = this.newStyle;
              if(this.props.distView === "PF"){
                style = this.pfStyle;
              }else if(this.props.distView && Object.keys(demoMap).find(key=>this.props.distView==key)){
                style = this.demoStyle;
              }
            }else if(this.props.view === "VPB"){
              style = this.blocStyle;
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