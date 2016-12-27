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
import * as Cookies from "js-cookie"

import TrackInfo from './js/components/track-info'
import App from './js/components/app'
import Uploader from './js/components/uploader'
import authenticate from './js/actions'
import * as reducers from './js/reducers'

const reduxDevToolsHack = window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__();

const store = createStore(
    combineReducers({
        ...reducers,
        routing: routerReducer
    }), reduxDevToolsHack);

let auth = Cookies.get('auth');
if(auth !== undefined){
    store.dispatch(authenticate(auth.userId, auth.roles))
}

const history = syncHistoryWithStore(browserHistory, store);

function dummy(text){
    return function (props) {
        return <div>{text}</div>;
    }
}

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
