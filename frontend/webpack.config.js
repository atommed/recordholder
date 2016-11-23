const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    entry: './app/index.js',
    output: {
        filename: 'bundle.js',
        path: '/var/www/recordholder/public_html'
    },
    module: {
        loaders: [{
            test: /(\.js$|\.jsx$)/,
            exclude: /node_modules/,
            loader: 'babel-loader',
            query: {
                presets: ['es2015'],
                plugins: ["transform-react-jsx", 
                          "transform-es2015-modules-amd",
                          "transform-object-rest-spread",
                          "transform-decorators-legacy"]
            }
        }, {
            test: /\.scss$/,
            loader: 'style!css!sass!resolve-url!sass?sourceMap'
        }, {
            test: /\.(eot|svg|ttf|woff|woff2)$/,
            loader: "file"
        }]
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: "./index.ejs",
        })
    ]
};
