import {connect} from 'react-redux'
import AuthForm from './presentational/auth-form'
import {authenticate} from '../actions'
import {setAJAX} from "../util"

function mapDispatchToProps(dispatch){
    function ajaxLogIn(req){
        if(req.readyState != 4) return;
        if(req.status == 200) {
            let u = JSON.parse(req.responseText);
            dispatch(authenticate(u.id, []))
        }
    }

    function requestLogin(req) {
        req.onreadystatechange = ajaxLogIn(req);
        setAJAX(req)
    }

    return {
        onSignIn: function(data){
            console.log(data);
            const req = new XMLHttpRequest();
            req.open("POST", "/api/signIn", true);
            requestLogin(req);
            req.send(data);
        },
        onSignUp: function (data) {
            const req = new XMLHttpRequest();
            req.open("POST", "/api/signUp");
            requestLogin(req);
            req.send(data)
        }
    }
}

export default connect(null, mapDispatchToProps)(AuthForm);
