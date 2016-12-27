import {connect} from 'react-redux'
import AuthForm from './presentational/auth-form'
import {authenticate} from '../actions'
import {ensuringCsrf} from "../util"
import * as Cookies from 'js-cookie'

function mapDispatchToProps(dispatch){
    function handleLoginData(user){
        Cookies.set('auth', user,{
            expires: 365
        });
        dispatch(authenticate(user.id, user.name, []));
    }

    return {
        onSignIn: function(data){
            ensuringCsrf(()=>{
                $.postJson("/api/signIn", data).done(handleLoginData);
            });
        },
        onSignUp: function (data) {
            ensuringCsrf(()=> {
                $.postJson("/api/signUp", data).done(handleLoginData);
            });
        }
    }
}

export default connect(null, mapDispatchToProps)(AuthForm);
