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
    handleClick = (e,element)=>{
      this.mapRef.current.leafletElement.fitBounds(e.layer.getBounds())
    }
    render(){
        return(
          <Map center={position} ref={this.mapRef} zoom={5}id="map">
              <TileLayer
      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      attribution="&copy; <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
    />        <FeatureGroup>
                <GeoJSON data={pa['default']} onClick={(e)=>this.handleClick(e,this)} ></GeoJSON>
                <GeoJSON data={ri['default']} onClick={(e)=>this.handleClick(e,this)}></GeoJSON>
                <GeoJSON data={ca['default']} onClick={(e)=>this.handleClick(e,this)}></GeoJSON>
                </FeatureGroup>
          </Map>
        );
    }
}

export default LeafletMap;