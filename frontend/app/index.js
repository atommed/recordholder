require("./styles/style.scss");
//require("materialize-css/sass/materialize.scss");
//require('expose?$!expose?jQuery!jquery');
require('material-icons/css/material-icons.css');
require('materialize-css/dist/css/materialize.css');
require('materialize-css/dist/js/materialize');
//require("bootstrap-webpack");

import React from 'react'
import ReactDOM from 'react-dom'
import {createStore, combineReducers} from 'redux'
import {Provider, connect} from 'react-redux'
import {Link, Router, Route, browserHistory} from 'react-router'
import {syncHistoryWithStore, routerReducer} from 'react-router-redux'

import App from './js/components/presentational/app'
import PlayButton from './js/components/play-button'
import Uploader from './js/components/uploader'
import Player from './js/components/player'
import * as reducers from './js/reducers'

const reduxDevToolsHack = window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__();
const store = createStore(
    combineReducers({
        ...reducers,
        routing: routerReducer
    }), reduxDevToolsHack);
const history = syncHistoryWithStore(browserHistory, store);



@connect(state=>{return {text: state.trackUploadResult}})
class Display extends React.Component{
    render() {return <div>{this.props.text}</div>;}
}

function dummy(text){
    return function (props) {
        return <div>{text}</div>;
    }
}

//TODO: FFMPEG returns mov extension for 28 days later theme

ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>
            <Route path="/" component={App}>
                <Route path="a" component={dummy("A!!!")}/>
                <Route path="b" component={dummy("B!!!")}/>
                <Route path="c" component={dummy("C!!!")}/>
                <Route path="uploader" component={Uploader}/>
            </Route>
        </Router>
    </Provider>,
    document.getElementById('app_mount')
);
