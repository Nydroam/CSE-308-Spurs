import React from 'react';
import {Map, TileLayer, GeoJSON} from 'react-leaflet';
import './Map.css';
import * as pa from '../../data/penndata.json';
import * as ri from '../../data/ridata.json';
import * as ca from '../../data/calidata.json';

const position = [38, -98]
class LeafletMap extends React.Component{
    render(){
        return(
          <Map center={position} zoom={5}id="map">
              <TileLayer
      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      attribution="&copy; <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
    />
                <GeoJSON data={pa['default']}></GeoJSON>
                <GeoJSON data={ri['default']}></GeoJSON>
                <GeoJSON data={ca['default']}></GeoJSON>
          </Map>
        );
    }
}

export default LeafletMap;