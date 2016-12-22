require("./styles/style.scss");
require('material-icons/css/material-icons.css');
require('materialize-css/dist/css/materialize.css');
require('materialize-css/dist/js/materialize');

import React from 'react'
import ReactDOM from 'react-dom'
import {createStore, combineReducers} from 'redux'
import {Provider, connect} from 'react-redux'
import {Link, Router, Route, browserHistory} from 'react-router'
import {syncHistoryWithStore, routerReducer} from 'react-router-redux'

import TrackInfo from './js/components/track-info'
import App from './js/components/app'
import Uploader from './js/components/uploader'

import * as reducers from './js/reducers'

const reduxDevToolsHack = window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__();
const store = createStore(
    combineReducers({
        ...reducers,
        routing: routerReducer
    }), reduxDevToolsHack);
const history = syncHistoryWithStore(browserHistory, store);

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
                <Route path="trackInfo" component={TrackInfo}/>
                <Route path="uploader" component={Uploader}/>
            </Route>
        </Router>
    </Provider>,
    document.getElementById('app_mount')
);
