import React from 'react';
import {Map, TileLayer, GeoJSON, FeatureGroup} from 'react-leaflet';
import './Map.css';
import * as pa from '../../data/penndata.json';
import * as ri from '../../data/ridata.json';
import * as ca from '../../data/calidata.json';

const position = [38, -98]
class LeafletMap extends React.Component{

    constructor(props) {
      super(props);
      this.mapRef = React.createRef();
    }
    handleClick = (e)=>{
      this.mapRef.current.leafletElement.fitBounds(e.layer.getBounds())
    }
    setStyle = (feature) =>{
      return{
        fillColor: 'blue',
        fillOpacity:1,
        color:'white',
        weight:1,
        opacity:1,
      }
    }
    render(){
        return(
          <Map center={position} ref={this.mapRef} zoom={5}id="map">
              <TileLayer
      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      attribution="&copy; <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
    />        <FeatureGroup >
                <GeoJSON data={pa['default']} style={this.setStyle} onClick={(e)=>this.handleClick(e)} ></GeoJSON>
                <GeoJSON data={ri['default']} style={this.setStyle} onClick={(e)=>this.handleClick(e)}></GeoJSON>
                <GeoJSON data={ca['default']} style={this.setStyle} onClick={(e)=>this.handleClick(e)}></GeoJSON>
              </FeatureGroup>
          </Map>
        );
    }
}

export default LeafletMap;