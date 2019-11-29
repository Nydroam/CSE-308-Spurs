const express = require('express');
const app = express();
const port = process.env.PORT || 5000;
const cors = require('cors');
const fs = require('fs');

app.use(cors())

// console.log that your server is up and running
app.listen(port, () => console.log(`Listening on port ${port}`));

// create a GET route
app.get('/geojson/:name', (req, res) => {
  let data = require("./src/data/" + req.params.name)
  console.log(data)
  res.send(data);
});

// get filenames
app.get('/files', (req, res) =>{
  let filenames = [];
  fs.readdirSync('./src/data/').forEach( (file) =>{
    filenames.push(file);
  }
  );
  filenames.sort(function(a, b) {
    return fs.statSync('./src/data/'+a).size - fs.statSync('./src/data/'+b).size;
  });
  res.send(filenames);
});