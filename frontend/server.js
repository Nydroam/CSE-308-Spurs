const express = require('express');
const app = express();
const port = process.env.PORT || 5000;
const cors = require('cors');

app.use(cors())

// console.log that your server is up and running
app.listen(port, () => console.log(`Listening on port ${port}`));

// create a GET route
app.get('/geojson/:name', (req, res) => {
  let data = require("./src/data/" + req.params.name)
  console.log(data)
  res.send(data);
});