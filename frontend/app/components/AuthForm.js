import {connect} from 'react-redux'
import {signUpSuccess, requestSignUp, requestSignIn, signInFail, signUpFail} from '../actions'
import {ensuringCsrf} from "../util"
import * as Cookies from 'js-cookie'

import AuthForm from "./presentational/AuthForm"

function mapStateToProps(state) {
    return {
        status: state.auth.message
    }
}

function mapDispatchToProps(dispatch){
    function handleLoginData(user){
        Cookies.set('auth', {user},{
            expires: 365
        });
        dispatch(signUpSuccess(user));
    }

    function onSignUpFail(req){
        dispatch(signUpFail(req.responseText))
    }

    function onSignInFail(req){
        dispatch(signInFail(req.responseText))
    }

    return {
        handleSignIn: function (credentials) {
            dispatch(requestSignIn());
            ensuringCsrf(()=>{
                $.postJson("/api/signIn", credentials)
                    .done(handleLoginData).fail(onSignInFail)
            })
        },
        handleSignUp: function (credentials) {
            dispatch(requestSignUp());
            ensuringCsrf(()=> {
                $.postJson("/api/signUp", credentials)
                    .done(handleLoginData).fail(onSignUpFail);
            });
        }
    }
}

export default connect(mapStateToProps,mapDispatchToProps)(AuthForm);