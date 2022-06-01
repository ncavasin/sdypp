const files = require("../files.json")
const address = require("../address.json")
const axios = require("axios");
let index = 0;



async function httpQuery(method, endpoint, data) {
    try {
        await axios({
            method,
            url: address[index] + endpoint,
            data
        })
    }catch {
        if (address.length > index) {
            index += 1;
            httpQuery(method, endpoint, data)
        } else {
            throw (new Error());

        }
    }
}


const setFile = async (req, res) => {
    try {
        await httpQuery("POST", "/api/p2p/hello", files)
        res.render('index', {url:address[index]});
    } catch (e) {
        res.render('notfound');
    }
}

module.exports = { setFile }