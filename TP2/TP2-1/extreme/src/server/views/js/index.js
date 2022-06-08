function locateFilename(address){
  actualAddress = address;
  let file = document.querySelector("#postFiles").value;
  postFile(file, address)
}

function allfiles(address){
  const response = httpQuery("GET", "http://"+address+"/api/p2p/all");

  let table = '<table class="table"><thead><tr><th scope="col">Owner</th><th scope="col">File</th></tr></thead><tbody>';

  response.forEach(({owner, files}) => {
      let aux = "";
      files.forEach( file => aux += '<p>'+file+'</p>');
      table += '<tr><td>'+owner+'</td><td>'+aux+'</td></tr>'
  });
  
  table += '</tbody></table>';

  document.querySelector("#file").innerHTML = table;

}

function httpQuery(method, url, body) {
  const Http = new XMLHttpRequest();
  console.log(method, url)
  Http.open(method, url, false);
  if (body) {
    console.log(body)
    Http.setRequestHeader('Content-Type', 'application/json');
    Http.send(JSON.stringify(body));
  } else {
    Http.send();
  }

  return JSON.parse(Http.responseText);
}

function postFile(file, address) {
  const response = httpQuery("POST", "http://"+address+"/api/p2p/locate", { file });
  let table = '<table class="table"><thead><tr><th scope="col">Owner</th><th scope="col">Descargar</th></tr></thead><tbody><tr>';


  response.forEach(({ owner }) => {
    table += '<tr><td>' + owner + '<td><a href="http://'+owner+'/descargar/'+ file+'">Descargar</a></td></td></tr>'//'+owner+','+ file+' <button class="btn btn-primary" id="findAll" onclick="allfiles(`{{url}}`)">Buscar</button>
    console.log(owner, file)
  });

  table += '</tr></tbody></table>';

  document.querySelector("#file").innerHTML = table;
}
