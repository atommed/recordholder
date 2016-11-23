require("./styles/style.scss");

import React from 'react'
import ReactDOM from 'react-dom'
import {createStore, combineReducers} from 'redux'
import {Provider} from 'react-redux'
import {Link, Router, Route, browserHistory} from 'react-router'
import {syncHistoryWithStore, routerReducer} from 'react-router-redux'
import {UploadForm} from './js/components/upload-form'


// Add the reducer to your store on the `routing` key 
const store = createStore(
    combineReducers({
        routing: routerReducer
    }), window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());

// Create an enhanced history that syncs navigation events with the store 
const history = syncHistoryWithStore(browserHistory, store);

console.log(UploadForm)

/*
class UploadForm extends React.Component {
    handleSubmit(ev) {
        ev.preventDefault();
        var oData = new FormData(this.form);
        var req = new XMLHttpRequest();
        req.open("POST","api/tracks/upload", true);
        req.onload = ()=>{
            if(req.status==200)
                this.setState({result: req.response})
            else
                this.setState({result: "Error " + req.status+ ":"+req.response})
        }
        req.send(oData)
    }

    constructor(props) {
        super(props);
        this.state = {
            result: ""
        };
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    render() {
        console.log(this)
        return (
            <div>
                {this.state.result}
                <form ref={(form)=> {this.form = form;}}
                      onSubmit={this.handleSubmit}
                      encType="multipart/form-data"
                      method="post">
                    <label>File uploader</label><br/>
                    <input type="file" required name="track"/>
                    <input type="submit" value="Upload file!"/>
                </form>
            </div>
        )
    }
}
*/

function App(props) {
    return <div>Moi detki <br/> {props.children} <br/>
        <Link to="/foo">Nalevo</Link><br />
        <Link to="/bar">Napravo</Link>
    </div>
}

function Bar() {
    return <div>Yeah! Bar!</div>
}
function Foo() {
    return <div>Oh no( fffuuu!</div>
}

ReactDOM.render(
    <Provider store={store}>
        { /* Tell the Router to use our enhanced history */ }
        <Router history={history}>
            <Route path="/" component={App}>
                <Route path="foo" component={UploadForm}/>
                <Route path="bar" component={Bar}/>
            </Route>
        </Router>
    </Provider>,
    document.getElementById('app_mount')
);
