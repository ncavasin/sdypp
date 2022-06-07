//const files = require("../files.json")
const fs = require('fs');
const address = require("../address.json")
const axios = require("axios");
let index = 0;
const { networkInterfaces } = require('os');
var ip = require('ip');

const nets = networkInterfaces();
const results = Object.create(null);

function httpQuery(method, endpoint, data) {
    console.log(address[index]+endpoint);
    return axios({
        method,
        url: "http://"+address[index]+endpoint,
        data

    }).then(()=>{
        console.log("/hello")
        return true;
    }).catch((e)=>{
        console(e);
        if ((address.length -1)> index) {
            index += 1;
            return  httpQuery(method, endpoint, data)
        } else {
            throw (new Error(e));
        }
    })

}


const setFile = async (req, res) => {
    try {
         let dir = __dirname.replace('\\controllers','');
        dir =dir + '/public/share/';
        const names = fs.readdirSync(dir);
        let arrayFiles = [];
        for (let name of names) {
                arrayFiles.push(name)
              }
        host = ip.address()+":3000";
        console.log(host);
        const files = {
                "owner": host,
                "files": arrayFiles
        }
        await httpQuery("POST","/api/p2p/hello", files)
       return res.render('index', {url:address[index]});
    } catch (e) {
      info = address[index];

        //return res.render('notfound');
        return res.render('notfound', {respuesta:info});
    }
}

module.exports = { setFile }