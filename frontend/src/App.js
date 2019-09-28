import React from 'react';
import Map from './components/Map/LeafletMap';
import Sidebar from './components/Sidebar/Sidebar';
import './App.css';
function App() {
  return (
    <div className="App flex" >
      <Sidebar></Sidebar>
      <Map></Map>
    </div>
  );
}

export default App;
