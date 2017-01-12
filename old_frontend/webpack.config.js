const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpack = require('webpack')

module.exports = {
    entry: './app/index.js',
    output: {
        filename: 'bundle.js',
        path: 'public_html'
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
        }, {
            test: /\.css$/,
            loader: 'style!css?sourceMap'
        }]
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: "./index.ejs",
            title: "RecordHolder"
        }),
        new webpack.ProvidePlugin({
            $: 'jquery'
        })
    ]
};
