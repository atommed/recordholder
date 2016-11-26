import {connect} from 'react-redux'
import AuthForm from './presentational/auth-form'
import {authenticate} from '../actions'

function mapDispatchToProps(dispatch){
    return {
        onSignIn: function(data){
            const req = new XMLHttpRequest();
            req.open("POST", "api/auth", true);
            req.onreadystatechange=()=>{
                if(req.readyState != 4) return;
                if(req.status == 200)
                    dispatch(authenticate(req.getResponseHeader("user"), req.getResponseHeader("token")));
            };
            req.send(data);
        }
    }
}

export default connect(null, mapDispatchToProps)(AuthForm);
