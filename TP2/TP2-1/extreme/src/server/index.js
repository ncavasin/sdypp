const { setFile } = require('./controllers/setFiles');
const express = require('express')
const hbs = require('hbs');
const app = express()
const port = 3000
app.use(express.static(__dirname + '/public'));
app.use(express.static(__dirname + '/views'));
// Exoress HBS engine
hbs.registerPartials(__dirname + 'views');
app.set('view engine', 'hbs');

//require('./hbs/helpers');


app.get('/', setFile)

app.get('/descargar/:id', (req,res)=>{
  res.download(__dirname + '/public/share/'+req.params.id,
  req.params.id,function(err){
    if(err){
      console.log(err);
    }else{
      console.log("listo")
    }
  } );
})


app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})