import React from 'react';
import './Map.css';
import 'ol/ol.css';
import {Map, View} from 'ol';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import {fromLonLat} from 'ol/proj';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import GeoJSON from 'ol/format/GeoJSON';
import * as penn from '../data/penndata.json';
import * as ri from '../data/ridata.json';
import * as cali from '../data/calidata.json';
import { Fill, Stroke, Style} from 'ol/style.js';

class MyMap extends React.Component{
    componentDidMount(){
      var styles = {
        
        'LineString': new Style({
          stroke: new Stroke({
            color: 'green',
            width: 1
          })
        }),
        'Polygon': new Style({
          stroke: new Stroke({
            color: 'blue',
            lineDash: [4],
            width: 3
          }),
          fill: new Fill({
            color: 'rgba(0, 0, 255, 0.1)'
          })
        }),
      };
      var styleFunction = function(feature) {
        return styles[feature.getGeometry().getType()];
      };
      var p = penn['default'];
      var r = ri['default'];
      var c = cali['default'];
      const map = new Map({
        target: 'map',
        layers: [
          new TileLayer({
            source: new OSM()
          }),
          new VectorLayer({
            source: new VectorSource({
              features: (new GeoJSON({featureProjection: 'EPSG:3857'})).readFeatures(p),
              style: styleFunction
            })
          }),
          new VectorLayer({
            source: new VectorSource({
              features: (new GeoJSON({featureProjection: 'EPSG:3857'})).readFeatures(r),
              style: styleFunction
            })
          }),
          new VectorLayer({
            source: new VectorSource({
              features: (new GeoJSON({featureProjection: 'EPSG:3857'})).readFeatures(c),
              style: styleFunction
            })
          })
        ],
        view: new View({
          center: fromLonLat([-97,38]),
          zoom: 5
        })
      }); 
    }
    
    render(){
        return(
          <div id="map">

          </div>
        );
    }
}

export default MyMap;