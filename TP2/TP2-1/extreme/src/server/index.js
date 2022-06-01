const { setFile } = require('./controllers/setFiles');
const express = require('express')
const hbs = require('hbs');
const app = express()
const port = 3000
app.use(express.static(__dirname + '/views'));

// Exoress HBS engine
hbs.registerPartials(__dirname + '/views');
app.set('view engine', 'hbs');

// de esta manera importamos todo el contenido de un js
//require('./hbs/helpers');

app.get('/', setFile)

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})