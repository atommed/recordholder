require("./styles/style.scss");
require('material-icons/css/material-icons.css');
require('materialize-css/dist/css/materialize.css');
require('materialize-css/dist/js/materialize');

import React from 'react'
import ReactDOM from 'react-dom'
import {createStore, combineReducers} from 'redux'
import {Provider, connect} from 'react-redux'
import {Link, Router, Route, hashHistory} from 'react-router'
import {syncHistoryWithStore, routerReducer} from 'react-router-redux'
import * as Cookies from "js-cookie"

import AlbumsView from './js/components/presentational/AlbumsView'
import TrackInfo from './js/components/track-info'
import App from './js/components/app'
import Uploader from './js/components/uploader'
import AlbumView from './js/components/AlbumView'
import {authenticate} from './js/actions'
import * as reducers from './js/reducers'


const reduxDevToolsHack = window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__();

const store = createStore(
    combineReducers({
        ...reducers,
        routing: routerReducer
    }), reduxDevToolsHack);

const auth = Cookies.getJSON('auth');
if(auth !== undefined){
    store.dispatch(authenticate(auth.id, auth.name, []))
}

const history = syncHistoryWithStore(hashHistory, store);

$.ajaxPrefilter(function (options, originalOptions, jqXHR) {
    const token = Cookies.get('Csrf-Token');
    jqXHR.setRequestHeader('Csrf-Token', token);
});

$.postJson = function(url, data){
    return $.ajax({
        url,
        type: 'POST',
        data: JSON.stringify(data),
        contentType: 'application/json',
        dataType: 'json'
    })
};
ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>
            <Route path="/" component={App}>
                <Route path="collection/:userId/albums" components={AlbumsView} />
                <Route path="albums/:albumId" components={AlbumView} />
                <Route path="trackInfo" component={TrackInfo}/>
                <Route path="uploader" component={Uploader}/>
            </Route>
        </Router>
    </Provider>,
    document.getElementById('app_mount')
);
