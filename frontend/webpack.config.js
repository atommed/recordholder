var path = require('path')

module.exports = {
    entry: {
      app: ["./app/js/main.jsx"]
    },
    output: {
      path: "./public_html",
      filename: "bundle.js"
    },
    module: {
     loaders: [{
       test:/\.js?x/,
       loaders: ['babel'],
       include: path.join(__dirname, 'app')
     }]
    },
    devtool: 'source-map'
}
